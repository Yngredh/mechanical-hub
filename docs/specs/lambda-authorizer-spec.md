# Spec — Autenticação Serverless (Lambda Login + Lambda Authorizer)

**Status:** Draft
**Referências:** [RFC-0003](../architecture/rfc/0003-autenticacao-funcionarios-lambda-authorizer-cpf.md) · [Checklist de migração](../planning/CHECKLIST-LAMBDA-AUTHORIZER.md) · [Plano Fase 3](../planning/FASE3-PLANO-DE-ATIVIDADES.md)
**Repositório alvo:** Repo 1 — `mechanical-hub-auth` (Lambda), independente do monolito.

---

## 1. Objetivo e escopo

Substituir a autenticação embutida no monolito Spring Boot (`TokenService` + `SecurityConfiguration`) por duas funções serverless atrás do API Gateway:

| Função | Responsabilidade |
|---|---|
| **`auth-login`** | Recebe CPF + senha, valida contra o RDS, emite JWT. Rota **pública** no Gateway. |
| **`auth-authorizer`** | Lambda Authorizer (REQUEST) invocado pelo Gateway nas rotas protegidas. Valida o JWT e devolve política Allow/Deny + contexto. |

### Dentro do escopo
- Login por CPF + senha de **funcionários** (perfis `MECHANICAL` e `ADMINISTRATOR`).
- Emissão e validação de JWT (HS256).
- Autorização por perfil (role) nas rotas protegidas.
- Acesso somente-leitura ao RDS via RDS Proxy.

### Fora do escopo
- Cadastro/edição de usuários — permanece na aplicação principal (`POST /auth/register`, restrito a ADMINISTRATOR).
- Autenticação de cliente final — **não existe**. As rotas do cliente (aprovar orçamento, rejeitar orçamento, consultar OS por número) seguem públicas, sem authorizer.
- Refresh token, MFA, recuperação de senha, revogação/blacklist de token (v2).

---

## 2. Decisões técnicas

| Item | Decisão | Motivo |
|---|---|---|
| Runtime | Node.js 20 + TypeScript | Cold start baixo, empacotamento leve, `bcryptjs`/`jsonwebtoken` maduros. |
| Gateway | API Gateway **REST (v1)** com Lambda Authorizer tipo `REQUEST` | Permite lógica própria de validação e cache de política. |
| Acesso ao banco | Lambda na VPC → RDS PostgreSQL (**conexão direta**, pool com `max: 2`) | RDS Proxy exige criar uma IAM role própria, o que o AWS Academy Lab não permite. O teto baixo de conexões por instância cumpre o papel de contenção. Ver §16. |
| Credenciais do banco | Porta `SecretProvider`; implementação de **variável de ambiente** no lab, **Secrets Manager** em produção | O lab não libera o Secrets Manager. A abstração deixa a troca ser de configuração, não de código. Ver §16. |
| Assinatura do JWT | **HS256**, chave resolvida pelo `SecretProvider` | Compatível com o `TokenService` que o monolito usava (`Algorithm.HMAC256`), minimiza o atrito da migração. |
| Hash de senha | **BCrypt** (`bcryptjs.compare`) | Mesmo algoritmo do `BCryptPasswordEncoder`; senhas existentes continuam válidas. |
| Empacotamento | esbuild bundle + zip | Cold start menor; bundle final de ~170 KB por função. |
| IaC | Terraform, dentro do repo da Lambda (`infra/terraform`) | Requisito do desafio (repos independentes com CI/CD próprio). |
| Arquitetura interna | Hexagonal: `core/` sem nenhuma dependência de nuvem, adaptadores de AWS isolados em `entrypoints/aws-lambda` | Migrar de provedor passa a ser escrever um novo entrypoint, não reescrever a regra. |

---

## 3. Pré-requisitos de modelo de dados

A tabela `users` hoje é `(id, profile_id, name, email, password_hash, created_at, updated_at, deleted_at)`. Migrations necessárias **no repo da aplicação principal** (dono do schema):

> **Nomenclatura:** a coluna do documento se chama `document_number` no banco. O termo `cpf` permanece apenas na camada de API/domínio (payload do login, claim do JWT, validação de dígito verificador).

Implementado em três migrations, para que o backfill fique entre a criação da coluna e a restrição:

| Migration | O que faz |
|---|---|
| `V18__add_document_number_to_users.sql` | `ALTER TABLE users ADD COLUMN document_number VARCHAR(11)` — anulável de propósito |
| `V19__backfill_users_document_number.sql` | Preenche os usuários já existentes (os do seed `V15`) |
| `V20__enforce_users_document_number.sql` | Checa que ninguém ficou sem documento (`RAISE EXCEPTION` se sobrou), depois aplica `NOT NULL`, `UNIQUE` e o índice |

```sql
-- V20, trecho relevante
DO $$
DECLARE missing INTEGER;
BEGIN
    SELECT COUNT(*) INTO missing FROM users WHERE document_number IS NULL;
    IF missing > 0 THEN
        RAISE EXCEPTION 'Existem % usuario(s) sem document_number...', missing;
    END IF;
END $$;

ALTER TABLE users ALTER COLUMN document_number SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT uk_users_document_number UNIQUE (document_number);
CREATE INDEX idx_users_document_number ON users(document_number);
```

> A checagem explícita na `V20` é intencional: sem ela, um funcionário sem CPF só descobriria o problema na primeira tentativa de login. Melhor quebrar o deploy.

Regras:
- A coluna se chama **`document_number`** (nome neutro no modelo de dados); o CPF é o tipo de documento adotado nesta fase. Na API e no JWT o campo continua sendo exposto como `cpf`, que é o termo de negócio — a tradução acontece no repositório da Lambda.
- `document_number` armazenado **somente com dígitos** (11 chars, sem máscara). Normalização é responsabilidade de quem escreve (aplicação principal) e de quem lê (Lambda).
- **Não há coluna `status`.** O soft delete já existente (`deleted_at`, criado em `V17`) é a única fonte de verdade sobre atividade do usuário: `deleted_at IS NULL` = ativo, preenchido = inativo. Desativar um funcionário é o mesmo fluxo de exclusão lógica que a aplicação principal já executa.
- Mudanças nesse contrato (colunas `document_number`, `password_hash`, `deleted_at`, `profile_id`, `profiles.name`) exigem coordenação entre os dois repositórios — ver §10.

### Query única usada pelo login

```sql
SELECT u.id, u.name, u.document_number, u.password_hash, u.deleted_at, p.name AS profile
FROM users u
JOIN profiles p ON p.id = u.profile_id
WHERE u.document_number = $1;
```

> A filtragem por `deleted_at` acontece **na aplicação**, não na cláusula `WHERE`. Isso permite distinguir "não existe" de "existe mas está inativo" e responder com o status HTTP correto (§4.1).

Role de banco dedicada:

```sql
CREATE ROLE lambda_auth LOGIN;
GRANT CONNECT ON DATABASE mechanical_hub TO lambda_auth;
GRANT USAGE ON SCHEMA public TO lambda_auth;
GRANT SELECT (id, name, document_number, password_hash, deleted_at, profile_id) ON users TO lambda_auth;
GRANT SELECT ON profiles TO lambda_auth;
-- nenhum INSERT/UPDATE/DELETE, nenhuma outra tabela de domínio
```

---

## 4. Função `auth-login`

### 4.1 Contrato

`POST /auth/login` (rota pública no Gateway)

**Request**
```json
{ "cpf": "12345678901", "password": "senha-em-texto-plano" }
```
Aceita CPF com ou sem máscara (`123.456.789-01`); a função normaliza.

**200 OK**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 7200
}
```

**Erros** — corpo padronizado, sem vazar qual campo falhou:
```json
{ "error": "INVALID_CREDENTIALS", "message": "CPF ou senha inválidos", "traceId": "..." }
```

| Situação | HTTP | `error` |
|---|---|---|
| Body ausente/malformado, campo faltando | 400 | `INVALID_REQUEST` |
| CPF com formato/dígito verificador inválido | 400 | `INVALID_CPF` |
| CPF não encontrado | 401 | `INVALID_CREDENTIALS` |
| Senha incorreta | 401 | `INVALID_CREDENTIALS` |
| Usuário inativo (`deleted_at` preenchido) | 403 | `USER_INACTIVE` |
| Excesso de tentativas | 429 | `TOO_MANY_ATTEMPTS` |
| Falha de banco / segredo / erro não tratado | 500 | `INTERNAL_ERROR` |

> CPF inexistente e senha incorreta devolvem **a mesma resposta** (401 `INVALID_CREDENTIALS`) para não permitir enumeração de usuários. `USER_INACTIVE` é distinguível por decisão de produto (o funcionário precisa saber que deve procurar o admin) — se optar por endurecer, colapsar em 401.

### 4.2 Fluxo passo a passo

1. **Parse & validação sintática** — `cpf` e `password` presentes, tipos corretos, `password` com 1..72 bytes (limite do BCrypt).
2. **Normalização do CPF** — remover tudo que não é dígito.
3. **Validação de CPF** — 11 dígitos, não todos iguais, dígitos verificadores corretos. Falha aqui evita ida ao banco.
4. **Rate limiting** — ver §7.3. Chave: `cpf` normalizado + IP de origem.
5. **Obter segredos** — credenciais do banco e `JWT_SECRET` do Secrets Manager, com **cache em memória fora do handler** (TTL 15 min) para reaproveitar entre invocações quentes.
6. **Consultar usuário** — pool `pg` inicializado fora do handler, apontando para o endpoint do RDS Proxy. Query da §3.
7. **Não encontrado** → dummy-compare de bcrypt contra um hash fixo (evita *timing attack* que distingue CPF existente de inexistente) → 401.
8. **Verificar senha** — `bcrypt.compare(password, password_hash)`. Falso → 401.
9. **Verificar se está ativo** — `deleted_at != null` → 403.
10. **Emitir JWT** — claims da §4.3, assinado HS256.
11. **Registrar sucesso** — log estruturado + métrica; zerar contador de tentativas.
12. **Responder 200.**

### 4.3 Claims do JWT

```json
{
  "iss": "mechanical-hub-auth",
  "sub": "9f1c...-uuid-do-usuario",
  "aud": "mechanical-hub-api",
  "cpf": "12345678901",
  "name": "Maria Silva",
  "role": "ADMINISTRATOR",
  "iat": 1753600000,
  "exp": 1753607200,
  "jti": "uuid-v4"
}
```

- `sub` = **UUID do usuário** (o monolito hoje usa o e-mail como `sub`; ver §9).
- `role` ∈ `{ MECHANICAL, ADMINISTRATOR }`, vindo de **`profiles.name`** (valor canônico do banco).

> ⚠️ **Divergência de nomenclatura a resolver.** O `ProfileEnum` do monolito expõe `displayName` = `MECANICO` / `ADMIN`, e é esse valor que a `SecurityConfiguration` usa em `hasRole(...)` (authority `ROLE_MECANICO` / `ROLE_ADMIN`). O banco, porém, guarda `MECHANICAL` / `ADMINISTRATOR` em `profiles.name`. A Lambda usa o valor do banco. Ao ajustar o monolito (§9), alinhar os dois — preferencialmente adotando `profiles.name` como valor único em todo o sistema e descontinuando o `displayName` como identificador de role.
- Expiração: **2 horas** (mantém o comportamento atual do `TokenService`), configurável via env `JWT_TTL_SECONDS`.
- **Nunca** incluir `password_hash` ou qualquer dado sensível além do CPF.

### 4.4 Variáveis de ambiente

| Var | Exemplo | Origem |
|---|---|---|
| `DB_SECRET_ARN` | `arn:aws:secretsmanager:...:rds-lambda-auth` | Terraform |
| `JWT_SECRET_ARN` | `arn:aws:secretsmanager:...:jwt-signing-key` | Terraform |
| `DB_PROXY_HOST` | `mh-proxy.proxy-xxx.us-east-1.rds.amazonaws.com` | Terraform |
| `DB_NAME` | `mechanical_hub` | Terraform |
| `JWT_ISSUER` | `mechanical-hub-auth` | Terraform |
| `JWT_AUDIENCE` | `mechanical-hub-api` | Terraform |
| `JWT_TTL_SECONDS` | `7200` | Terraform |
| `LOG_LEVEL` | `info` | Terraform |

---

## 5. Função `auth-authorizer`

Lambda Authorizer tipo **REQUEST**, invocado pelo API Gateway antes de encaminhar a requisição à aplicação principal.

### 5.1 Fluxo

1. Extrair header `Authorization`. Ausente ou fora do padrão `Bearer <token>` → `Deny` (Gateway responde **401**).
2. Buscar `JWT_SECRET` (cache em memória, mesmo padrão da §4.2.5).
3. `jwt.verify(token, secret, { algorithms: ['HS256'], issuer, audience })` — valida assinatura, `exp`, `iss`, `aud`. Rejeita explicitamente `alg: none` e algoritmos assimétricos.
4. Token inválido/expirado/assinatura errada → `Deny` (**401**).
5. Token válido mas `role` não autorizada para o `methodArn` da requisição (tabela §5.3) → `Deny` (**403**).
6. Válido → **`Allow`** com política IAM e `context` propagado.

### 5.2 Resposta

```json
{
  "principalId": "9f1c...-uuid",
  "policyDocument": {
    "Version": "2012-10-17",
    "Statement": [{ "Action": "execute-api:Invoke", "Effect": "Allow", "Resource": "arn:aws:execute-api:...:*/*/*" }]
  },
  "context": { "userId": "9f1c...", "role": "ADMINISTRATOR", "cpf": "12345678901", "name": "Maria Silva" }
}
```

- O `context` chega à aplicação principal como headers injetados pelo Gateway (`x-user-id`, `x-user-role`) via *integration request mapping* — evita que o monolito precise reparsear o JWT.
- **Cache do authorizer:** TTL de **300s**, chave = header `Authorization`. Como a `Resource` da política é curinga (`*/*/*`), o cache é seguro entre rotas. Consequência: revogação/desativação de usuário leva até 5 min + tempo restante do token para surtir efeito. Aceito nesta fase; documentar no README.
- O authorizer **não consulta o banco** — decisão deliberada: latência e acoplamento. A checagem de `deleted_at` acontece só no login.

### 5.3 Matriz de autorização

Espelha a `SecurityConfiguration` atual do monolito:

| Rota (path do Gateway) | Método | Proteção |
|---|---|---|
| `/auth/login` | POST | **Pública** (integra com `auth-login`) |
| `/actuator/health/**` | GET | Pública |
| `/swagger-ui/**`, `/v3/api-docs/**` | GET | Pública (avaliar restringir em prod) |
| `/mechanical-hub/service-orders/**` | GET/POST | **Pública** — rotas do cliente final (consulta por número, aprovação, rejeição) |
| `/auth/register`, `/users/**` | ALL | `ADMINISTRATOR` |
| `/customers/**`, `/vehicles/**`, `/services/**`, `/materials/**`, `/stock/**`, `/reports/**` | ALL | `ADMINISTRATOR` |
| `/service-orders/**` | ALL | `MECHANICAL` ou `ADMINISTRATOR` |

> A matriz vive **em código** na Lambda (array de `{ pattern, methods, roles }`) e é coberta por testes unitários. Um path não previsto na matriz cai no default: **exigir autenticação, negar por padrão**.

---

## 6. Diagramas

### 6.1 Componentes

```
Cliente final ─┐
               ├──► API Gateway ──┬── /auth/login ─────────────► λ auth-login ──► RDS Proxy ──► RDS (users, profiles)
Funcionário ───┘                  │                                   │
                                  │                                   └──► Secrets Manager (db creds, jwt secret)
                                  ├── rotas públicas ────────────► EKS / App Spring Boot
                                  │
                                  └── rotas protegidas ──► λ auth-authorizer ──(Allow + context)──► EKS / App Spring Boot
```

> Versão em Mermaid, versionada: `docs/specs/diagrams/authentication-sequence.mermaid`.

### 6.2 Sequência — login

```
Funcionário → Gateway: POST /auth/login {cpf, password}
Gateway → auth-login: invoke
auth-login → auth-login: normaliza + valida CPF
auth-login → Secrets Manager: get (cache miss)
auth-login → RDS Proxy → RDS: SELECT ... WHERE document_number = $1
RDS → auth-login: user row
auth-login → auth-login: bcrypt.compare + checa deleted_at
auth-login → Gateway: 200 {accessToken, expiresIn}
Gateway → Funcionário: 200
```

### 6.3 Sequência — rota protegida

```
Funcionário → Gateway: GET /service-orders  (Authorization: Bearer ...)
Gateway → auth-authorizer: invoke (cache miss)
auth-authorizer: verifica assinatura/exp/iss/aud + role vs methodArn
auth-authorizer → Gateway: Allow + context{userId, role}
Gateway → App (EKS): GET /service-orders + headers x-user-id, x-user-role
App → Gateway: 200
Gateway → Funcionário: 200
```

---

## 7. Segurança

### 7.1 Rede e permissões
- `auth-login` em **subnets privadas** da mesma VPC do RDS; egress só para o RDS na 5432 e para o CloudWatch Logs. Sem NAT Gateway.
- `auth-authorizer` **fora da VPC**: não fala com o banco, e ficar fora reduz cold start e não consome ENI.
- Security Group: `sg-lambda-auth` → `sg-rds` na porta 5432; nada mais.
- Role de execução: no lab é a `LabRole` (não há permissão para criar roles). Em produção, role dedicada com apenas `secretsmanager:GetSecretValue` nos ARNs específicos e escrita de log. Sem `*`.
- Role de banco `mechanical_hub_auth`: somente `SELECT` em colunas específicas de `users` e em `profiles` (script em `infra/sql/auth-database-role.sql`, no repo da Lambda).

### 7.2 Dados sensíveis
- Senha em texto plano **nunca** logada, nem em log de debug, nem em resposta de erro.
- CPF é dado pessoal (LGPD): logar **mascarado** (`***.***.789-01`). No JWT ele trafega íntegro — aceitável porque o token é assinado e transita em HTTPS, mas registrar essa decisão.
- TLS obrigatório: Gateway só HTTPS; conexão ao RDS com TLS habilitado (`DATABASE_SSL=true`).

### 7.3 Rate limiting
- **Camada Gateway (defesa principal):** throttling na rota `/auth/login` — 20 req/s, burst 40, via `aws_api_gateway_method_settings`.
- **Camada aplicação:** contador de falhas por documento atrás da porta `AttemptLimiter`. Após **5 falhas em 15 min**, responde 429 até a janela expirar; sucesso limpa o contador.
- Implementação atual: **em memória, por instância de execução** — o limite não é global. Suficiente para conter tentativa manual; volume real é problema do throttling do Gateway. Um adaptador com armazenamento distribuído (DynamoDB) pluga na mesma porta sem tocar no caso de uso.
- Sem bloqueio permanente de conta nesta fase (evita DoS de conta por terceiros).

### 7.4 Rotação de segredo JWT
Segredo HS256 é compartilhado com o monolito. Rotação exige janela de aceitação dupla no validador (aceitar segredo antigo e novo por 1 TTL de token). Documentar o runbook antes da primeira rotação.

---

## 8. Observabilidade

Requisito da Fase 3 — Datadog/New Relic + logs estruturados JSON correlacionados.

**Log (JSON, uma linha por evento):**
```json
{ "level":"warn","event":"login.failed","reason":"INVALID_CREDENTIALS","cpfMasked":"***.***.789-01",
  "traceId":"1-abc","requestId":"...","sourceIp":"1.2.3.4","durationMs":142 }
```

Eventos obrigatórios: `login.attempt`, `login.success`, `login.failed` (com `reason`), `login.blocked`, `authorizer.allow`, `authorizer.deny` (com `reason`), `db.error`, `secret.error`.

**Métricas (EMF → CloudWatch → Datadog):**

| Métrica | Uso |
|---|---|
| `auth.login.success` / `auth.login.failure` (dim: `reason`) | Taxa de erro, detecção de ataque |
| `auth.login.latency_ms` (p50/p95/p99) | Requisito de latência de API |
| `auth.db.query_latency_ms` | Isolar gargalo de banco |
| `auth.coldstart` (count) | Tuning de memória/provisioned concurrency |
| `auth.authorizer.deny` (dim: `reason`) | Tokens expirados vs role negada |

**Alertas:** taxa de falha de login > 30% em 5 min; p95 de latência > 1500 ms; qualquer `db.error`; erro 5xx > 1% na rota de login.

**Correlação:** o `requestId` do API Gateway vira `traceId` em todos os eventos da requisição. (X-Ray não está disponível no AWS Academy Lab — ver §16.)

---

## 9. Impacto na aplicação principal (Repo 4)

**Decisão adotada:** o monolito passa a confiar nos cabeçalhos injetados pelo Gateway; não valida token nem conhece o segredo de assinatura.

| Antes | Depois |
|---|---|
| `TokenService` emitia e validava JWT | Removido (e as dependências `jjwt` / `auth0:java-jwt` saíram do `pom.xml`) |
| `SecurityFilter` abria o token e carregava o usuário do banco a cada requisição | `GatewayAuthenticationFilter` lê `x-user-id`, `x-user-role`, `x-user-name` — sem ida ao banco |
| `AuthenticationController` expunha `POST /auth/login` | Removido; o login é a função `authenticate` |
| `AuthorizationService` (`UserDetailsService`) + `AuthenticationManager` | Removidos; não há mais fluxo de autenticação local |
| Principal do contexto era `UserSecurityAdapter` (envolvia a entidade `User`) | `GatewayPrincipal` (record com `id`, `name`, `role`) |
| `JWT_SECRET` no `application.yml`, no Secret do K8s e no workflow | Removido dos três |

Outras mudanças de contrato:

1. **`sub` do token passa de `email` para `UUID`.** O `SecurityFilter` antigo fazia `findByEmail(sub)`; o `ServiceOrderController` agora obtém o id direto de `GatewayPrincipal.id()`.
2. **Nomes de perfil alinhados.** `Profile.create()` passava a usar `ProfileEnum.getDisplayName()` (`ADMIN` / `MECANICO`), que não batia com `profiles.name` no banco (`ADMINISTRATOR` / `MECHANICAL`) — isso quebrava `ProfileJpaRepository.findByName` no cadastro de usuário. Agora usa `ProfileEnum.name()`, e a `SecurityConfiguration` também.
3. **Cadastro exige CPF** válido e único, persistido normalizado em `users.document_number`. Documento duplicado devolve `DuplicatedDocumentException`.
4. `UserResponse` ganhou `documentNumber`, devolvido formatado.

> **Premissa de segurança que passa a valer:** a aplicação principal precisa ser inalcançável fora do API Gateway (ALB interno + Security Group restrito). Quem chegar ao pod diretamente consegue forjar `x-user-role` e se tornar administrador. Isso está documentado no javadoc do `GatewayAuthenticationFilter`.

---

## 10. Contrato entre repositórios

A tabela `users` é contrato implícito entre Repo 1 (Lambda) e Repo 4 (app). Regras de convivência:

- O **Repo 4 é o dono do schema** (Flyway). O Repo 1 nunca escreve migration.
- Colunas do contrato: `users.document_number`, `users.password_hash`, `users.deleted_at`, `users.profile_id`, `profiles.name`. Alterar qualquer uma exige PR coordenado e teste de integração da Lambda contra o novo schema.
- O pipeline da Lambda roda um **teste de contrato** contra um Postgres em container com as migrations do Repo 4 aplicadas — quebra se o schema divergir.

---

## 11. Estrutura do repositório (Repo 1)

Arquitetura hexagonal, com a dependência sempre apontando para dentro. O objetivo explícito é que trocar de nuvem seja escrever um novo entrypoint, não reescrever a regra.

```
mechanical-hub-auth/
├── src/
│   ├── core/                          # NÃO importa AWS, HTTP, pg nem process.env
│   │   ├── domain/
│   │   │   ├── document-number.ts     # normalização, dígito verificador, máscara p/ log
│   │   │   ├── errors.ts              # AuthError tipado → código + status
│   │   │   ├── role.ts                # MECHANICAL | ADMINISTRATOR
│   │   │   ├── user.ts                # AuthenticatableUser
│   │   │   └── access-policy.ts       # matriz rota × perfil (§5.3)
│   │   ├── ports/
│   │   │   ├── user-repository.ts     ├── token-service.ts
│   │   │   ├── secret-provider.ts     ├── password-verifier.ts
│   │   │   ├── attempt-limiter.ts     ├── logger.ts   └── clock.ts
│   │   └── usecases/
│   │       ├── authenticate-user.ts   # fluxo da §4.2
│   │       └── authorize-access.ts    # fluxo da §5.1
│   ├── adapters/
│   │   ├── persistence/sql-user-repository.ts
│   │   ├── secrets/{environment,aws-secrets-manager,cached}-secret-provider.ts
│   │   ├── security/{jwt-token-service,bcrypt-password-verifier}.ts
│   │   ├── ratelimit/in-memory-attempt-limiter.ts
│   │   └── observability/structured-logger.ts
│   ├── entrypoints/
│   │   ├── http/                      # contrato HTTP neutro + controllers
│   │   │   ├── http-contract.ts
│   │   │   └── authenticate-controller.ts
│   │   └── aws-lambda/                # ÚNICO lugar que conhece evento da AWS
│   │       ├── event-mapper.ts
│   │       ├── authenticate.handler.ts
│   │       └── authorize.handler.ts
│   └── composition/
│       ├── config.ts                  # único ponto que lê variável de ambiente
│       └── container.ts               # instanciado 1x por instância de execução
├── tests/unit/                        # 133 testes
├── infra/
│   ├── terraform/                     # Gateway, funções, logs
│   └── sql/auth-database-role.sql     # role de banco somente-leitura
├── scripts/{build,package}.mjs        # esbuild + zip
└── .github/workflows/{ci,deploy}.yml
```

**Regra de ouro:** se um arquivo em `core/` precisar de um `import` de `aws-lambda`, `@aws-sdk`, `pg` ou `process.env`, a abstração está no lugar errado.

---

## 12. Estratégia de testes

**Unitários (alvo ≥ 80%)**
- `cpf.ts`: válidos, inválidos, com máscara, 11 dígitos iguais, tamanho errado, não-numérico.
- `authorization.ts`: cada linha da matriz §5.3 — allow e deny — e path desconhecido → deny.
- `sign/verify`: claims corretos, `exp` respeitado, token expirado rejeitado, assinatura adulterada rejeitada, `alg: none` rejeitado, `iss`/`aud` errados rejeitados.
- `login.ts` com repositório mockado: cada linha da tabela de erros §4.1.

**Integração (testcontainers PostgreSQL + migrations do Repo 4)**
- Login de usuário ativo → 200 com JWT verificável.
- CPF inexistente → 401.
- Senha errada → 401.
- Usuário com `deleted_at` preenchido → 403.
- Usuário reativado (`deleted_at` de volta para `NULL`) volta a logar.
- Hash BCrypt gerado pelo monolito valida na Lambda (**teste de compatibilidade crítico**).

**End-to-end (ambiente provisionado, roda no pipeline de deploy)**
- Login → usa o token em `/service-orders` → 200.
- Token de `MECHANICAL` em `/customers` → 403.
- Sem token em `/service-orders` → 401.
- Sem token nas rotas do cliente final → 200 (segue pública).
- Token expirado → 401.

---

## 13. Critérios de aceite

- [ ] Funcionário ativo faz login com CPF e senha existente (hash gerado pelo monolito) e recebe JWT válido.
- [ ] CPF inexistente e senha errada devolvem resposta idêntica (401), sem enumeração.
- [ ] Usuário inativo ou soft-deleted não consegue logar.
- [ ] Rotas protegidas rejeitam requisição sem token, com token inválido e com token expirado.
- [ ] Autorização por perfil respeita a matriz §5.3.
- [ ] Rotas do cliente final continuam acessíveis sem token.
- [ ] Nenhuma senha ou CPF não mascarado aparece em log.
- [ ] Lambda não tem permissão de escrita em nenhuma tabela.
- [ ] Logs JSON com `traceId` chegam ao Datadog; dashboard e alertas da §8 ativos.
- [ ] p95 de latência do login < 1500 ms (com conexão quente).
- [ ] Cobertura de testes ≥ 80%; pipeline com branch `main` protegida e PR obrigatório.

---

## 14. Riscos e mitigações

| Risco | Impacto | Mitigação |
|---|---|---|
| Cold start com VPC + pool de conexões | Login lento (>3s) na primeira chamada | Bundle esbuild (~170 KB), init fora do handler, 512 MB. Concorrência provisionada não existe no lab; o autorizador ficou fora da VPC para não pagar esse custo |
| Backfill de CPF dos usuários existentes | Usuários sem CPF ficam sem conseguir logar | Migration de backfill antes de `NOT NULL`; validar contagem antes do cutover |
| Incompatibilidade de hash BCrypt (Java ↔ Node) | Ninguém consegue logar | Teste de compatibilidade obrigatório no pipeline (§12). Hashes atuais no seed usam prefixo **`$2a$` custo 10** — `bcryptjs` lê `$2a$` corretamente, mas o teste é obrigatório |
| Divergência de nome de role entre `ProfileEnum` e `profiles.name` | Authorizer libera/nega errado | **Resolvido:** `Profile.create()` passou a usar `ProfileEnum.name()`; `SecurityConfiguration` idem. Coberto por teste |
| Cache do authorizer atrasa desativação de usuário | Usuário desligado mantém acesso até ~5 min + TTL do token | Aceito; TTL do token 2h; documentado no README. v2: lista de revogação por `jti` |
| Segredo JWT sem cofre gerenciado (lab) | Segredo visível para quem lê a configuração da função; não rotaciona sozinho | Aceito no lab. `SecretProvider` permite trocar para Secrets Manager sem alterar código |
| Rotação da chave HS256 | Quebra tokens em voo | Runbook de rotação com aceitação dupla (§7.4) |
| Esgotamento de conexões no RDS (sem RDS Proxy) | Indisponibilidade do banco para o app | Pool com `max: 2` por instância; conexão ociosa expira em 30 s; timeout de conexão de 5 s |
| App alcançável fora do Gateway | Qualquer um forja `x-user-role` e vira administrador | ALB interno + Security Group restrito. Premissa registrada no javadoc do `GatewayAuthenticationFilter` |

---

## 15. Estado da implementação

| # | Item | Status |
|---|---|---|
| 1 | Migrations `V18`/`V19`/`V20` (`document_number`) no Repo 4 | ✅ |
| 2 | Repo 1: core, adaptadores, entrypoints, 133 testes unitários (97,9% de cobertura) | ✅ |
| 3 | Matriz de autorização em código + testes por rota e perfil | ✅ |
| 4 | Terraform do API Gateway, funções, logs e throttling | ✅ |
| 5 | Script da role de banco somente-leitura (`infra/sql/auth-database-role.sql`) | ✅ |
| 6 | Pipelines de CI e deploy com smoke test pós-deploy | ✅ |
| 7 | Ajuste do monolito (§9): filtro por cabeçalho, remoção do login interno e das libs de JWT | ✅ |
| 8 | Log estruturado JSON com correlação | ✅ |
| 9 | Atualizar diagrama ER em `docs/specs/mechanical-hub-data-model.md` | ⬜ |
| 10 | Proteção da branch `main` nos dois repositórios (PR obrigatório, status checks) | ⬜ |
| 11 | Executar as migrations e o script da role no ambiente do lab | ⬜ |
| 12 | Provisionar (`terraform apply`) e rodar a validação E2E da §12 | ⬜ |
| 13 | Dashboards e alertas no Datadog/New Relic | ⬜ |
| 14 | Diagrama de sequência da autenticação; RFC-0003 → **Aceito** | ⬜ |

---

## 16. Desvios impostos pelo AWS Academy Lab

O ambiente do laboratório não oferece parte dos recursos que a versão original desta spec assumia. Cada desvio abaixo foi absorvido **atrás de uma abstração**, para que a versão de produção seja uma troca de configuração e não uma reescrita.

| Recurso indisponível | Motivo | Solução adotada | Como reverter em produção |
|---|---|---|---|
| **AWS Secrets Manager** | Serviço fora da lista permitida | `EnvironmentSecretProvider` lê os segredos das variáveis de ambiente da função | `SECRET_PROVIDER=aws-secrets-manager` + informar os ARNs. O `AwsSecretsManagerSecretProvider` já está implementado |
| **RDS Proxy** | Exige criar uma IAM role própria | Conexão direta ao RDS, pool com `max: 2` por instância | Apontar `DATABASE_HOST` para o endpoint do Proxy — nada muda no código |
| **Criação de IAM roles** | Só existe a `LabRole` | Terraform recebe o ARN da role por variável (`lambda_execution_role_arn`) | Passar o ARN de uma role dedicada com privilégio mínimo |
| **DynamoDB para rate limit** | Escopo/custo no lab | `InMemoryAttemptLimiter` (por instância) + throttling no Gateway | Implementar a porta `AttemptLimiter` com armazenamento distribuído |
| **Concorrência provisionada** | Não disponível | Bundle enxuto, init fora do handler, autorizador fora da VPC | Habilitar `provisioned_concurrent_executions` na função de login |
| **AWS X-Ray** | Não disponível | `requestId` do Gateway propagado como `traceId` no log JSON | Habilitar `tracing_config` nas funções |

**Consequência operacional do lab:** as credenciais AWS são temporárias e expiram a cada sessão. Os secrets `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` e `AWS_SESSION_TOKEN` do repositório precisam ser atualizados antes de cada deploy.
