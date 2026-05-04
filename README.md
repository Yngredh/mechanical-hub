# 🔧 Mechanical Hub

Sistema de Gestão de Oficina Mecânica — automatiza o atendimento ao cliente, o ciclo completo das Ordens de Serviço, o controle de estoque de peças e insumos, e gera métricas de execução.

---

## 📋 Sumário

- [Visão Geral](#visao-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Regras de Negócio](#regras-de-negócio)
- [Entidades do Domínio](#entidades-do-domínio)
- [Pré-requisitos](#pré-requisitos)
- [Configuração de Ambiente](#configuração-de-ambiente)
- [Como Executar](#como-executar)
  - [Com Docker (recomendado)](#com-docker-recomendado)
  - [Localmente (sem Docker)](#localmente-sem-docker)
- [Usuários Padrão](#usuários-padrão)
- [Documentação da API - Endpoints](#documentação-da-api)
- [Como Testar o Projeto](#como-testar-o-projeto)
  - [Fluxo completo passo a passo](#fluxo-completo-passo-a-passo)
  - [Testes automatizados](#testes-automatizados)

---

<a id="visao-geral"></a>
## 💡 Visão Geral

O Mechanical Hub resolve os problemas de uma oficina mecânica que opera com anotações manuais e planilhas isoladas:

- **Gestão de Ordens de Serviço** — ciclo completo: abertura, diagnóstico, aprovação, execução, finalização e entrega.
- **Controle de Estoque** — entrada, reserva automática por OS, rastreamento de pendências e notificações de estoque mínimo.
- **Orçamento Automático** — cálculo baseado nos serviços e materiais incluídos na OS.
- **Métricas de Execução** — relatório de tempo médio de execução por tipo de serviço.
- **Portal do Cliente** — o cliente recebe o orçamento via WhatsApp e pode aprovar ou recusar sem precisar de login.

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Banco de Dados | PostgreSQL 16 |
| Autenticação | JWT (JJWT 0.11.5 + Auth0 Java JWT 4.4.0) |
| Documentação | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven |
| Contêineres | Docker + Docker Compose |

---

## 🏗️ Arquitetura

- [Estrutura do Projeto](src/docs/rules/rules.md#project-structure)

---

## 📖 Regras de Negócio

- [Regras de Negócio](src/docs/spec/mechanical_hub_spec.md#regras-de-negócio)
- [Linguagem Ubíqua](src/docs/spec/mechanical_hub_spec.md#linguagem-ubíqua)
- [Fluxo de Status da OS](src/docs/spec/mechanical_hub_spec.md#fluxo-de-status)
- [Perfis e Controle de Acesso](src/docs/spec/mechanical_hub_spec.md#perfis-e-controle-de-acesso)

---

## 🗃️ Entidades do Domínio

- [Modelagem de Dados](src/docs/spec/mechanical-hub-data-model.md)

---

## ✅ Pré-requisitos

- Executar via contêiner - [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/)
- Executar Localmente - JDK 21 + Maven 3.9+ + PostgreSQL 16

---

## ⚙️ Configuração de Ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
DB_HOST=postgres
DB_PORT=5432
DB_NAME=mechanical_hub_db
DB_USER=admin
DB_PASSWORD=12345678
JWT_SECRET=minha-chave-jwt-mechanical-hub
JWT_EXPIRATION_MS=3600000
```

---

## 🚀 Como Executar

### 🐳 Com Docker (recomendado)

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd mechanical-hub

# 2. Suba todos os serviços (banco + aplicação)
docker compose up --build

# 3. A API estará disponível em:
http://localhost:8080
```

Para parar os serviços:
```bash
docker compose down
```

Para parar e remover o volume do banco (dados serão apagados):
```bash
docker compose down -v
```

### 💻 Localmente (sem Docker)

**1. Suba apenas o banco com Docker:**
```bash
docker compose up postgres
```

**2. Configure as variáveis de ambiente no terminal:**
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=mechanical_hub_db
export DB_USER=admin
export DB_PASSWORD=PmN2iDvdNz
export JWT_SECRET=minha-chave-jwt-mechanical-hub
export JWT_EXPIRATION_MS=3600000
```

**3. Execute a aplicação:**
```bash
./mvnw spring-boot:run
```

**ou com Maven instalado:**
```bash
mvn spring-boot:run
```

**4. Para gerar o JAR e executar:**
```bash
mvn clean package -DskipTests
java -jar target/mechanical-hub-0.0.1-SNAPSHOT.jar
```

---

## 👤 Usuários Padrão

O Flyway cria automaticamente dois usuários na primeira execução (migration `V15`):

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | `admin@mechanicalhub.com` | consultar PDF |
| Mecânico | `mecanico@mechanicalhub.com` | consultar PDF |

> Use o endpoint `POST /auth/login` para obter o token JWT e incluí-lo no header `Authorization: Bearer <token>` nas demais requisições.

---

## 📚 Documentação da API

Com a aplicação rodando, acesse o Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

Esquema OpenAPI em JSON:
```
http://localhost:8080/v3/api-docs
```

---

## 🧪 Como Testar o Projeto

### Fluxo completo passo a passo

O fluxo abaixo cobre o ciclo completo de uma Ordem de Serviço desde a criação até a entrega do veículo. Use o Swagger (`http://localhost:8080/swagger-ui/index.html`) ou um cliente HTTP como Insomnia / Postman.

---

#### Passo 1 — Autenticar

```
POST /auth/login
```
```json
{
  "login": "admin@mechanicalhub.com",
  "password": "admin123"
}
```
Copie o `token` da resposta e use-o em todos os próximos requests no header:
```
Authorization: Bearer <token>
```

---

#### Passo 2 — Cadastrar material

```
POST /materials
```
```json
{
  "name": "Filtro de Óleo",
  "description": "Filtro de óleo para motor 1.8",
  "unitPrice": 25.00,
  "minStockQuantity": 5
}
```
Salve o `id` retornado.

---

#### Passo 3 — Dar entrada no estoque

```
POST /stock/entry
```
```json
{
  "materialId": "<id-do-material>",
  "quantity": 10
}
```

---

#### Passo 4 — Cadastrar um serviço

```
POST /services
```
```json
{
  "name": "Troca de Filtro de Óleo",
  "description": "Substituição do filtro de óleo",
  "laborCost": 60.00,
  "materials": [
    {
      "materialId": "<id-do-material>",
      "quantity": 1
    }
  ]
}
```
Salve o `id` do serviço.

---

#### Passo 5 — Criar a Ordem de Serviço

```
POST /service-orders
```
```json
{
  "customer": {
    "name": "Maria Souza",
    "documentType": "CPF",
    "documentNumber": "529.982.247-25",
    "telephone": "(11) 98765-4321",
    "email": "maria@email.com",
    "address": "Av. Paulista, 1000 - São Paulo/SP"
  },
  "vehicle": {
    "licensePlate": "BRA2E19",
    "brand": "Honda",
    "model": "Civic",
    "year": 2021,
    "color": "Preto"
  },
  "requestDescription": "Troca de filtro de óleo preventiva."
}
```
Salve o `id` e o `orderNumber` da OS.

---

#### Passo 6 — Iniciar diagnóstico (Mecânico)

Autentique-se como mecânico ou use o token de admin:
```
PATCH /service-orders/<order-id>/status
```
```json
{
  "status": "EM_DIAGNOSTICO",
  "responsibleUserId": "<uuid-do-mecanico>"
}
```

---

#### Passo 7 — Adicionar serviços à OS

```
POST /service-orders/<id>/services
```
```json
{
  "serviceIds": ["<id-do-servico>"]
}
```
O sistema calcula o orçamento e reserva os materiais do estoque.

---

#### Passo 8 — Enviar orçamento para aprovação

```
PATCH /service-orders/<id>/status
```
```json
{
  "status": "AGUARDANDO_APROVACAO"
}
```
Neste ponto o sistema enviaria o orçamento ao cliente via WhatsApp (mockado).

---

#### Passo 9 — Cliente aprova o orçamento (endpoint público)

```
POST /mechanical-hub/service-orders/<id>/approve
```
> Sem token — endpoint público.

---

#### Passo 10 — Iniciar execução do serviço

```
PATCH /service-orders/<id>/services/<taskId>/status
```
```json
{
  "status": "INICIADO"
}
```
A OS muda automaticamente para `EM_EXECUCAO`.

---

#### Passo 11 — Finalizar o serviço

```
PATCH /service-orders/<id>/services/<taskId>/status
```
```json
{
  "status": "FINALIZADO"
}
```

---

#### Passo 12 — Finalizar a OS

```
PATCH /service-orders/<id>/status
```
```json
{
  "status": "FINALIZADO"
}
```
> Só é possível quando **todos** os serviços da OS estiverem `FINALIZADO`.

---

#### Passo 13 — Registrar entrega do veículo

```
PATCH /service-orders/<id>/status
```
```json
{
  "status": "ENTREGUE"
}
```

---

#### Consultar OS pelo número (visão do cliente — sem token)

```
GET /mechanical-hub/service-orders/OS-202604-0001
```

---

### ❌ Cenário de teste: Recusa de orçamento e retorno de estoque

1. Siga os passos 1–8 acima.
2. No lugar de aprovar, **recuse** o orçamento:
   ```
   POST /mechanical-hub/service-orders/<id>/reject
   ```
3. Verifique que os materiais reservados voltaram para `disponivel`:
   ```
   GET /stock/<materialId>
   ```

---

### ⚠️ Cenário de teste: Pendência de estoque

1. Crie um serviço com material.
2. **Não** dê entrada em estoque (quantidade = 0).
3. Crie uma OS e adicione o serviço.
4. Verifique que `hasStockPending = true` na OS.
5. Tente avançar para `EM_EXECUCAO` — deve retornar erro.
6. Dê entrada no estoque:
   ```
   POST /stock/entry
   ```
7. Verifique que `hasStockPending` foi automaticamente resolvido para `false`.

---

### 🤖 Testes automatizados

Execute os testes com Maven:

```bash
# Rodar todos os testes
mvn test

# Rodar um teste específico
mvn test -Dtest=CustomerUseCaseTest

# Rodar com relatório detalhado
mvn test -Dsurefire.useFile=false
```

> **Atenção:** Os testes de integração precisam de banco PostgreSQL em execução. Use `docker compose up postgres` antes de rodar os testes.

Para gerar o relatório de cobertura (se configurado com JaCoCo):
```bash
mvn verify
```

---
