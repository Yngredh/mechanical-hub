# RFC-0003: Autenticação de Funcionários via Lambda Authorizer com CPF

## Status
Aceito

## Contexto

Hoje, a autenticação vive dentro do monolito: a tabela `USERS` (mecânicos e administradores) guarda `email` + `password_hash`, e o próprio Spring Boot valida a senha e emite o JWT via Spring Security. Clientes finais (donos dos veículos) **não autenticam na plataforma** — a única interação deles com o sistema é através de rotas públicas e sem autenticação: aprovação de orçamento, rejeição de orçamento e consulta de detalhes da OS por número de identificação.

Ficou definido que:
- Os únicos usuários que autenticam são os funcionários da oficina (mecânicos e administradores).
- A autenticação passará a ser feita por CPF (não mais por e-mail), atendendo ao requisito do desafio.
- A tabela `USERS` não possui hoje o campo CPF — será necessário um ajuste no modelo relacional.

A questão central desta RFC é: a Lambda deve acessar o banco de dados diretamente, ou deve chamar a aplicação principal para validar o usuário?

## Proposta

Implementar um **Lambda Authorizer** que:

1. Recebe CPF e senha do funcionário.
2. Conecta-se **diretamente ao banco de dados gerenciado** (RDS, provisionado no repositório de infraestrutura de banco), consultando a tabela `USERS` por CPF.
3. Verifica existência do usuário, seu `status` (ativo/inativo) e a senha (usando o **mesmo algoritmo de hash** já usado pelo monolito, para não invalidar senhas existentes).
4. Gera e retorna um JWT (claims: id do usuário, perfil/role, expiração) para consumo das rotas protegidas via API Gateway.

A Lambda **não** chama a aplicação principal para executar essa validação — o acesso é direto ao banco compartilhado.

As rotas públicas do cliente final (aprovação, rejeição e consulta de OS por número) permanecem sem autenticação, roteadas pelo API Gateway diretamente para a aplicação principal, sem passar pelo authorizer.

A criação/cadastro de novos usuários (funcionários) continua sendo responsabilidade da aplicação principal (fluxo administrativo já existente) — a Lambda cobre apenas o fluxo de login.

## Justificativa

- **Disponibilidade de login independente da aplicação principal.** Se a aplicação principal estiver indisponível ou em deploy, o login continua funcionando, já que a Lambda não depende dela.
- **Repositórios evoluem e fazem deploy de forma independente**, como pede o desafio — sem dependência síncrona entre serviços com ciclos de deploy distintos.
- **Atende literalmente ao requisito do desafio** ("consultar a existência e o status do cliente na base de dados").
- **Evita o problema de ovo-e-galinha**: chamar um endpoint interno da aplicação principal para validar credenciais exigiria expor essa rota sem proteção prévia do Gateway, já que o JWT ainda não existe nesse ponto do fluxo.

## Alternativas Consideradas

**A) Lambda chama um endpoint interno da aplicação principal para validar credenciais.**
Rejeitada. Cria dependência síncrona entre dois serviços com ciclos de deploy e repositórios independentes — se a aplicação principal estiver indisponível ou em deploy, o login para de funcionar mesmo a Lambda estando saudável. Também exigiria expor uma rota de validação de credenciais sem proteção prévia do próprio Gateway (problema de ovo-e-galinha, já que o JWT ainda não existe nesse ponto do fluxo).

**B) Manter autenticação inteira no monolito, sem Lambda.**
Rejeitada. Não atende ao requisito obrigatório do desafio (Function Serverless para autenticação).

## Consequências

- A tabela `USERS` passa a ser um contrato implícito entre dois repositórios (Lambda e aplicação principal). Mudanças de schema precisam ser coordenadas.
- Necessário criar uma role de banco dedicada à Lambda, com privilégio mínimo (leitura em `USERS`/`PROFILES`, sem acesso a outras tabelas de domínio).
- O RDS Proxy seria o caminho natural entre a Lambda e o RDS para evitar esgotamento de conexões sob concorrência (o modelo de execução da Lambda abre conexões por invocação), mas **não foi adotado** — exige IAM role própria e Secrets Manager, indisponíveis no AWS Academy Lab. A conexão é direta, com pool de no máximo 2 conexões por instância e expiração de conexão ociosa em 30 s como contenção (ver `docs/specs/lambda-authorizer-spec.md`, §Trade-offs, e "Sem RDS Proxy" no README do `mechanical-hub-database`).
- A Lambda precisa rodar na mesma VPC do RDS (ou usar Data API/IAM auth, se aplicável).
- Necessário ajuste no modelo relacional: adicionar coluna `cpf` (UNIQUE, NOT NULL) à tabela `USERS`, com migration para os usuários já existentes.
