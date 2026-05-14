# 📐 Clean Architecture & DDD – Implementation Spec (Mechanical Hub MVP)

## 🎯 Objetivo

Este documento define **regras obrigatórias** para implementação de endpoints e processos do sistema seguindo **Clean Architecture + DDD**, garantindo:

* Separação clara de responsabilidades
* Domínio rico e protegido
* Baixo acoplamento entre camadas
* Alta testabilidade

---

## 🧱 Estrutura de Camadas (OBRIGATÓRIA)

```
Controller (Interface)
   ↓
Use Case (Application)
   ↓
Domain (Entities, Value Objects, Domain Services)
   ↓
Repository (interface no domain, implementação na infra)
```

---

## 📦 Organização de Pastas

```
domain/
application/
infrastructure/
```

### 🔴 domain/

* entities/
* valueobjects/
* enums/
* repositories/
* services/
* exceptions/

### 🟡 application/

* usecases/
* commands/
* dto/
* mappers/ (opcional)

### 🔵 infrastructure/

* http/
* database/
* integrations/
* mappers/ 

---

# 🚨 REGRAS GERAIS (NÃO NEGOCIÁVEIS)

## 1. Separação de responsabilidades

* ❌ É PROIBIDO colocar lógica de negócio em controllers

* ❌ É PROIBIDO colocar lógica de negócio em DTOs

* ❌ É PROIBIDO colocar lógica de negócio em repositories

* ❌ É PROIBIDO colocar lógica de negócio na camada infrastructure

* ✅ TODA regra de negócio DEVE estar no domínio

---

## 2. Domínio deve ser puro

* ❌ NÃO usar:

    * `@Service`
    * `@Component`
    * `@Entity`
    * qualquer import de framework (Spring, JPA, etc)

* ❌ NÃO acessar banco diretamente

* ✅ Domínio deve:

    * ser independente
    * ser testável sem framework
    * conter regras e invariantes

---

## 3. Use Cases NÃO pertencem ao domínio

* ❌ NÃO colocar use cases em `domain/`
* ✅ Use cases DEVEM ficar em `application/usecases/`

---

## 4. Cada processo = 1 Use Case

* ❌ NÃO criar classes com múltiplas responsabilidades

* ❌ NÃO agrupar vários fluxos em um único use case

* ✅ REGRA:

```
1 endpoint/processo de negócio = 1 classe UseCase
```

### Exemplo:

ANTES ❌

```
CustomerService:
 - createCustomer()
 - updateCustomer()
 - deleteCustomer()
```

DEPOIS ✅

```
CreateCustomerUseCase
UpdateCustomerUseCase
DeleteCustomerUseCase
```

---

## 5. Use Case é orquestrador

Use case DEVE:

* Receber um **Command**
* Controlar transação
* Orquestrar chamadas ao domínio
* Persistir dados

Use case NÃO DEVE:

* Conter regra de negócio complexa
* Validar regras de domínio
* Manipular DTO diretamente

---

## 6. Transações

* ✅ `@Transactional` DEVE ficar no Use Case
* ❌ NUNCA no domínio
* ❌ NUNCA na entidade

---

# 🔄 FLUXO PADRÃO (OBRIGATÓRIO)

## 1. Controller

Responsabilidades:

* Receber request (DTO)
* Converter DTO → Command
* Executar use case
* Converter output → response

```java
var command = mapper.toCommand(request);
var output = useCase.execute(command);
return mapper.toResponse(output);
```

---

## 2. DTO (Interface Layer)

* Representa entrada HTTP
* NÃO contém regra de negócio

---

## 3. Command (Application Layer)

* Representa entrada do Use Case
* É um objeto simples (record/class)
* NÃO contém validação complexa

---

## 4. Use Case (Application Layer)

Deve:

1. Converter dados primitivos → Value Objects
2. Chamar Domain Services (se necessário)
3. Criar/alterar entidades
4. Persistir via repository
5. Retornar Output

---

## 5. Domain

### Entidades

* DEVEM conter comportamento
* DEVEM proteger invariantes
* DEVEM expor métodos com intenção

Exemplo:

```java
order.finish();
```

❌ Nunca:

```java
order.setStatus(FINISHED);
```

---

### Value Objects (OBRIGATÓRIO quando aplicável)

Devem ser usados para:

* CPF/CNPJ (Document)
* Placa (LicensePlate)
* Valores monetários (Budget)

Devem:

* ser imutáveis
* validar dados na criação
* normalizar dados

---

### Domain Services

Devem ser usados quando:

* regra envolve repositório
* regra envolve múltiplas entidades

Exemplo:

* verificar duplicidade de cliente
* regras de estoque

---

## 6. Repository

* Interface DEVE ficar no domain
* Implementação DEVE ficar na infrastructure

---

# ⚠️ REGRAS DE MODELAGEM

## 1. NÃO usar setters para regras

❌ Errado:

```java
customer.setEmail(email);
```

✅ Correto:

```java
customer.updateEmail(email);
```

---

## 2. NÃO usar tipos primitivos quando há conceito

❌ Errado:

```java
String document;
```

✅ Correto:

```java
Document document;
```

---

## 3. NÃO espalhar validação

* ❌ formatter util

* ❌ validação no controller

* ❌ validação no use case

* ✅ validação deve estar no domínio ou VO

---

# 🧪 TESTES (OBRIGATÓRIO)

## Devem existir testes para:

* regras de negócio
* transições de estado
* edge cases

## Domínio deve ser testado:

* sem banco
* sem Spring
* sem HTTP

---

# 🚫 ANTI-PATTERNS PROIBIDOS

* Anemic Domain Model
* Fat Controller
* God Class
* DTO com lógica
* Acesso direto ao banco fora de repository
* Lógica de negócio em Use Case
* Uso de framework no domínio

---

# 🧠 REGRA DE OURO

> Domínio decide
> Application orquestra
> Infrastructure executa

---

# ✅ CHECKLIST RÁPIDO

* [ ] Use case está na camada application
* [ ] Controller não tem lógica
* [ ] Domínio não depende de framework
* [ ] Entidade contém comportamento
* [ ] Value Objects estão sendo usados
* [ ] Regras estão centralizadas no domínio
* [ ] Transação está no use case
* [ ] Não existe lógica em DTO
* [ ] Cada processo tem seu próprio Use Case

---

# 🚀 EVOLUÇÃO DO PROJETO

A partir deste documento:

* Cada função atual de use case DEVE virar uma nova classe
* Código antigo DEVE ser refatorado gradualmente
* Novas features DEVEM seguir este padrão obrigatoriamente

---

**Este documento é a referência oficial para arquitetura do projeto.**
