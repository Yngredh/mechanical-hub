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

> ❌ A pasta `application/mappers/` foi removida. Mappers NÃO pertencem à camada application.

### 🔵 infrastructure/

* http/
  * controllers/
  * routes/
  * middlewares/
  * mappers/  ← **HttpMapper** (DTO ↔ Command / Output ↔ Response)
* database/
  * repositories/
  * models/
  * migrations/
  * mappers/  ← **RepositoryMapper** (Entity ↔ Model)
* integrations/

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

## 4. Use Cases NÃO dependem de infrastructure

* ❌ NÃO importar nenhuma classe da camada `infrastructure/` em use cases
* ❌ NÃO injetar implementações concretas de repositório diretamente no use case
* ✅ Use cases DEVEM depender apenas de **interfaces de repositório** definidas no `domain/`
* ✅ A injeção da implementação concreta DEVE ser resolvida por inversão de dependência (IoC container)

### Exemplo:

❌ Errado — use case acoplado à infrastructure:

```java
// application/usecases/CreateVehicleUseCase.java
import infrastructure.database.repositories.JpaVehicleRepository; // ❌ PROIBIDO

public class CreateVehicleUseCase {
    private final JpaVehicleRepository repository; // ❌
}
```

✅ Correto — use case depende apenas da interface do domínio:

```java
// application/usecases/CreateVehicleUseCase.java
import domain.repositories.VehicleRepository; // ✅ interface do domínio

public class CreateVehicleUseCase {
    private final VehicleRepository repository; // ✅
}
```

---

## 5. Cada processo = 1 Use Case

* ❌ NÃO criar classes com múltiplas responsabilidades
* ❌ NÃO agrupar vários fluxos em um único use case

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

## 6. Use Case é orquestrador

Use case DEVE:

* Receber um **Command**
* Controlar transação
* Orquestrar chamadas ao domínio
* Persistir dados via interface de repositório

Use case NÃO DEVE:

* Conter regra de negócio complexa
* Validar regras de domínio
* Manipular DTO diretamente
* Depender de classes da camada infrastructure

---

## 7. Transações

* ✅ `@Transactional` DEVE ficar no Use Case
* ❌ NUNCA no domínio
* ❌ NUNCA na entidade

---

# 🗺️ MAPPERS – REGRAS OBRIGATÓRIAS

## Visão geral

Existem **dois tipos distintos de mapper**, cada um com responsabilidade e localização próprias.

| Tipo | Localização | Responsabilidade |
|---|---|---|
| `HttpMapper` | `infrastructure/http/mappers/` | Converter DTO de request → Command e Output → DTO de response |
| `RepositoryMapper` | `infrastructure/database/mappers/` | Converter Entity de domínio → Model de persistência e vice-versa |

> ❌ **Não existem mappers na camada `application/`.**
> ❌ **Não existem mappers na camada `domain/`.**

---

## HttpMapper (`infrastructure/http/mappers/`)

### Responsabilidades

* Converter **RequestDTO → Command** (entrada do use case)
* Converter **Output → ResponseDTO** (saída para o cliente HTTP)

### Convenção de nomenclatura

```
{Contexto}HttpMapper
```

Exemplo: `VehicleHttpMapper`, `ServiceOrderHttpMapper`

### Localização

```
infrastructure/
  http/
    mappers/
      VehicleHttpMapper.java
      ServiceOrderHttpMapper.java
```

### Onde é usado

O mapper HTTP DEVE ser chamado **exclusivamente no controller**.

```java
// infrastructure/http/controllers/VehicleController.java

@RestController
public class VehicleController {

    private final CreateVehicleUseCase createVehicleUseCase;
    private final VehicleHttpMapper mapper;

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleResponse> create(@RequestBody VehicleRequest request) {
        var command = mapper.toCommand(request);       // DTO → Command
        var output  = createVehicleUseCase.execute(command);
        var response = mapper.toResponse(output);      // Output → DTO
        return ResponseEntity.ok(response);
    }
}
```

### Regras

* ❌ NÃO chamar `HttpMapper` dentro de use cases
* ❌ NÃO chamar `HttpMapper` dentro de domain services
* ✅ `HttpMapper` NÃO contém lógica de negócio — apenas conversão de campos

---

## RepositoryMapper (`infrastructure/database/mappers/`)

### Responsabilidades

* Converter **Entity (domínio) → Model (JPA/ORM)**
* Converter **Model (JPA/ORM) → Entity (domínio)**

### Convenção de nomenclatura

```
{Contexto}RepositoryMapper
```

Exemplo: `VehicleRepositoryMapper`, `ServiceOrderRepositoryMapper`

### Localização

```
infrastructure/
  database/
    mappers/
      VehicleRepositoryMapper.java
      ServiceOrderRepositoryMapper.java
```

### Onde é usado

O mapper de repositório DEVE ser chamado **exclusivamente dentro da implementação do repositório**, na camada infrastructure.

```java
// infrastructure/database/repositories/VehicleRepositoryImpl.java

public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehicleJpaRepository jpaRepository;
    private final VehicleRepositoryMapper mapper;

    @Override
    public void save(Vehicle vehicle) {
        var model = mapper.toModel(vehicle);       // Entity → Model
        jpaRepository.save(model);
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(mapper::toEntity);                // Model → Entity
    }
}
```

### Regras

* ❌ NÃO chamar `RepositoryMapper` dentro de use cases
* ❌ NÃO chamar `RepositoryMapper` dentro de controllers
* ❌ NÃO chamar `RepositoryMapper` dentro do domínio
* ✅ `RepositoryMapper` NÃO contém lógica de negócio — apenas conversão de campos
* ✅ A entidade retornada pelo mapper DEVE ser a entidade pura do domínio (sem anotações JPA)

---

# 🔄 FLUXO PADRÃO (OBRIGATÓRIO)

```
[HTTP Request]
     ↓
Controller
  → HttpMapper.toCommand(request)
  → UseCase.execute(command)
  → HttpMapper.toResponse(output)
     ↓
[HTTP Response]

UseCase
  → Domain Entity / Domain Service
  → Repository (interface do domain)
     ↓
RepositoryImpl (infrastructure)
  → RepositoryMapper.toModel(entity)
  → JpaRepository.save(model)
  → RepositoryMapper.toEntity(model)
```

---

## 1. Controller

Responsabilidades:

* Receber request (DTO)
* Converter DTO → Command via `HttpMapper`
* Executar use case
* Converter Output → response via `HttpMapper`

```java
var command  = mapper.toCommand(request);
var output   = useCase.execute(command);
var response = mapper.toResponse(output);
return ResponseEntity.ok(response);
```

---

## 2. DTO (Interface Layer)

* Representa entrada/saída HTTP
* NÃO contém regra de negócio

---

## 3. Command (Application Layer)

* Representa entrada do Use Case
* É um objeto simples (record/class)
* NÃO contém validação complexa

---

## 4. Use Case (Application Layer)

Deve:

1. Converter dados primitivos do Command → Value Objects
2. Chamar Domain Services (se necessário)
3. Criar/alterar entidades
4. Persistir via interface de repositório (domain)
5. Retornar Output

NÃO deve:

* Importar ou instanciar classes de `infrastructure/`
* Chamar `HttpMapper` ou `RepositoryMapper`

---

## 5. Domain

### Entidades

* DEVEM conter comportamento
* DEVEM proteger invariantes
* DEVEM expor métodos com intenção

```java
order.finish();   // ✅
order.setStatus(FINISHED);  // ❌
```

---

### Value Objects (OBRIGATÓRIO quando aplicável)

Devem ser usados para:

* CPF/CNPJ (`Document`)
* Placa (`LicensePlate`)
* Valores monetários (`Budget`)

Devem:

* ser imutáveis
* validar dados na criação
* normalizar dados

---

### Domain Services

Devem ser usados quando:

* regra envolve repositório
* regra envolve múltiplas entidades

Exemplos:

* verificar duplicidade de cliente
* regras de estoque

---

## 6. Repository

* Interface DEVE ficar no `domain/repositories/`
* Implementação DEVE ficar em `infrastructure/database/repositories/`
* Use case depende APENAS da interface

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
* ✅ validação deve estar no domínio ou Value Object

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
* Mapper na camada application
* Mapper na camada domain
* HttpMapper chamado fora do controller
* RepositoryMapper chamado fora da implementação do repositório
* Use case importando classes de infrastructure
* Acesso direto ao banco fora de repository
* Lógica de negócio em Use Case
* Uso de framework no domínio

---

# 🧠 REGRA DE OURO

> Domínio decide
> Application orquestra
> Infrastructure executa
> Controller traduz (via HttpMapper)
> Repository traduz (via RepositoryMapper)

---

# ✅ CHECKLIST RÁPIDO

* [ ] Use case está na camada application
* [ ] Use case NÃO importa nenhuma classe de infrastructure
* [ ] Use case depende apenas de interfaces de repositório do domínio
* [ ] Controller não tem lógica de negócio
* [ ] Controller usa `HttpMapper` para converter request → command e output → response
* [ ] `HttpMapper` está em `infrastructure/http/mappers/`
* [ ] `RepositoryMapper` está em `infrastructure/database/mappers/`
* [ ] `RepositoryMapper` é chamado apenas dentro da implementação do repositório
* [ ] Não existe mapper em `application/`
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

* Mappers existentes em `application/mappers/` DEVEM ser migrados para `infrastructure/http/mappers/` ou `infrastructure/database/mappers/` conforme sua responsabilidade
* Cada função atual de use case DEVE virar uma nova classe
* Use cases que importam classes de infrastructure DEVEM ser refatorados para depender de interfaces do domínio
* Código antigo DEVE ser refatorado gradualmente
* Novas features DEVEM seguir este padrão obrigatoriamente

---

**Este documento é a referência oficial para arquitetura do projeto.**
