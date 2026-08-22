# 🔧 Mechanical Hub

Sistema de Gestão de Oficina Mecânica — automatiza o atendimento ao cliente, o ciclo completo das Ordens de Serviço, o controle de estoque de peças e insumos, e gera métricas de execução.

---

## 📋 Sumário

- [Visão Geral e Objetivos da Fase 1](#visão-geral-fase1)
- [Visão Geral e Objetivos da Fase 2](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](docs/ARCHITECTURE.md)
- [Pré-requisitos](#pré-requisitos)
- [Configuração de Ambiente](#configuração-de-ambiente)
- [Execução Local](#execução-local)
- [Deploy em Kubernetes e infraestrutura consumida](docs/DEPLOY.md)
- [Usuários Padrão](#usuários-padrão)
- [Documentação Endpoints da API](#documentação-da-api)
- [Como Testar o Projeto](docs/TESTING.md)

---

<a id="visão-geral-fase1"></a>
## 💡 Objetivos da Fase 1

A Fase 1 estabeleceu o MVP do back-end aplicando Domain-Driven Design como base de modelagem, com foco em organizar o caos operacional da oficina em um sistema coeso e seguro. O domínio foi mapeado via Event Storming, a lógica de negócio encapsulada em casos de uso e a API protegida com JWT — tudo containerizado e coberto por testes desde o início.

| Competência | Como foi aplicada                                                                                            |
|---|--------------------------------------------------------------------------------------------------------------|
| **Domain-Driven Design (DDD)** | Event Storming dos fluxos de OS e estoque; Linguagem Ubíqua definida e aplicada no código                    |
| **Arquitetura em camadas** | Back-end monolítico estruturado em domínio, aplicação, infraestrutura e interface (base para Clean Architecture) |
| **APIs RESTful** | Endpoints documentados via Swagger/OpenAPI cobrindo todos os fluxos do sistema                               |
| **Segurança** | JWT nas rotas administrativas; validação de CPF/CNPJ e placa; análise de vulnerabilidades com relatório de scan |
| **Qualidade de código** | Cobertura ≥ 80% nos domínios críticos; testes unitários e de integração                                      |
| **Containerização** | Dockerfile multi-stage + docker-compose orquestrando aplicação e banco                                       |
| **Banco de dados** | Banco de dados relacional PostgreSQL; migrations com Flyway                                                 |
| **Documentação** | README completo, documentação DDD e relatório de vulnerabilidades                          |

---

<a id="visão-geral"></a>
## 💡 Objetivos da Fase 2

A Fase 2 evoluiu a aplicação do MVP para um ambiente de produção real na AWS, com foco em escalabilidade, resiliência e automação. O código foi refatorado para Clean Architecture, toda a infraestrutura foi provisionada como código via Terraform e o ciclo de entrega foi completamente automatizado por um pipeline CI/CD no GitHub Actions — do commit ao deploy em Kubernetes.

| Competência | Como foi aplicada |
|---|---|
| **Clean Architecture** | Refatoração do monolito em camadas para separação explícita de domínio, aplicação, infraestrutura e interface |
| **Clean Code** | Nomes claros, coesão de responsabilidades e eliminação de acoplamentos desnecessários |
| **Orquestração com Kubernetes** | Deployment com RollingUpdate, Service LoadBalancer, HPA (CPU/memória), ConfigMap e Secret no Amazon EKS |
| **Infraestrutura como Código (IaC)** | Terraform com módulos independentes para VPC, EKS, RDS e ECR; estado remoto em S3; testes unitários e de integração |
| **CI/CD** | Pipeline GitHub Actions com 5 jobs: testes, validação Terraform, apply de infra, build/push ECR e deploy K8s |
| **Containerização** | Dockerfile multi-stage (Maven → JRE 21); imagem publicada no Amazon ECR com política de retenção |
| **Escalabilidade** | HPA escalando de 2 a 4 réplicas com base em CPU (70%) e memória (300 Mi); node group EKS com min:1 / max:4 |
| **Qualidade de código** | Testes automatizados integrados ao pipeline; falha em testes bloqueia o deploy |
| **Documentação** | README reestruturado com arquitetura, fluxo de deploy e instruções separados em `docs/` |

---

<a id="tecnologias"></a>
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
| Orquestração | Kubernetes (Amazon EKS 1.33) |
| IaC | Terraform ≥ 1.7 |
| Registry | Amazon ECR |
| CI/CD | GitHub Actions |
| Nuvem | AWS (us-east-1) |

---

<a id="pré-requisitos"></a>
## ✅ Pré-requisitos

**Execução local:**
- Docker e Docker Compose

**Execução local sem Docker:**
- JDK 21, Maven 3.9+, PostgreSQL 16

**Deploy em Kubernetes:**
- `kubectl` configurado para o cluster EKS
- AWS CLI com credenciais válidas

**Leitura dos states de infraestrutura (`ci/remote-state/`):**
- Terraform ≥ 1.7
- AWS CLI com credenciais (AWS Academy: `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`)
- `mechanical-hub-infra` e `mechanical-hub-database` já aplicados

> Desde a Fase 3 este repositório não provisiona infraestrutura (ADR-0002).
> VPC, EKS e ECR vivem em `mechanical-hub-infra`; o RDS, em
> `mechanical-hub-database`. Aqui só existe a leitura desses states, em
> [`ci/remote-state/`](ci/remote-state/README.md).

---

<a id="configuração-de-ambiente"></a>
## ⚙️ Configuração de Ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
DB_HOST=postgres
DB_PORT=5432
DB_NAME=mechanical_hub_db
DB_USERNAME=admin
DB_PASSWORD=12345678
JWT_SECRET=minha-chave-jwt-mechanical-hub
JWT_EXPIRATION_MS=3600000
```

---

<a id="execução-local"></a>
## 🚀 Execução Local

### 🐳 Com Docker (recomendado)

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd mechanical-hub

# 2. Suba todos os serviços (banco + aplicação)
docker compose up --build

# 3. A API estará disponível em:
# http://localhost:8080
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
export DB_USERNAME=admin
export DB_PASSWORD=12345678
export JWT_SECRET=minha-chave-jwt-mechanical-hub
export JWT_EXPIRATION_MS=3600000
```

**3. Execute a aplicação:**
```bash
./mvnw spring-boot:run
```

**4. Para gerar o JAR e executar:**
```bash
mvn clean package -DskipTests
java -jar target/mechanical-hub-0.0.1-SNAPSHOT.jar
```

---

<a id="usuários-padrão"></a>
## 👤 Usuários Padrão

O Flyway cria automaticamente dois usuários na primeira execução (migration `V15`):

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | `admin@mechanicalhub.com` | consultar PDF |
| Mecânico | `mecanico@mechanicalhub.com` | consultar PDF |

> Use o endpoint `POST /auth/login` para obter o token JWT e incluí-lo no header `Authorization: Bearer <token>` nas demais requisições.

---

<a id="documentação-da-api"></a>
## 📚 Documentação de Endpoints da API

Com a aplicação rodando, acesse o Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

Esquema OpenAPI em JSON:
```
http://localhost:8080/v3/api-docs
```

---

## 📂 Documentação Complementar

| Documento | Conteúdo |
|---|---|
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Componentes da aplicação, infraestrutura AWS e fluxo de deploy |
| [docs/DEPLOY.md](docs/DEPLOY.md) | Deploy manual em Kubernetes e leitura dos states de infraestrutura |
| [docs/TESTING.md](docs/TESTING.md) | Fluxo completo de teste da API, cenários pré-carregados e testes automatizados |
