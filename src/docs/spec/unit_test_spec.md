# Unit Test Specification – mechanical-hub

## 1. Convenções Gerais

### 1.1 Estrutura obrigatória: Arrange – Act – Assert

Todo método de teste deve estar dividido em três blocos comentados e visualmente separados.

```java
@Test
void shouldTransitionToInDiagnosis_whenOrderIsReceived() {
    // Arrange
    ServiceOrder order = ServiceOrderMock.received();

    // Act
    order.startDiagnosis();

    // Assert
    assertThat(order.getStatus()).isEqualTo(OrderStatus.IN_DIAGNOSIS);
}
```

Nenhum teste deve misturar setup com assertions ou realizar múltiplos `act` no mesmo método.
Não incluir comentários

---

### 1.2 Nomenclatura dos métodos

Padrão obrigatório: `should[Resultado]_when[Contexto]`

| Elemento | Descrição |
|---|---|
| `should` | O que se espera que aconteça (comportamento ou exceção) |
| `when` | O estado ou condição de entrada que provoca o comportamento |

Exemplos válidos:

```
shouldThrowException_whenOrderHasStockPending
shouldMarkOrderAsInProgress_whenFirstServiceIsStarted
shouldRestoreStock_whenOrderIsRejected
shouldCalculateBudgetCorrectly_whenServicesAreAdded
shouldNotTransitionToInProgress_whenHasStockPendingIsTrue
```

Exemplos inválidos:

```
testFinishOrder          // sem contexto, sem should/when
orderShouldFinish        // ordem invertida
finishOrderTest          // sufixo genérico
```

---

### 1.3 Um comportamento por teste

Cada método testa exatamente um comportamento ou regra de negócio. Se um teste precisa de mais de um `assertThat` para verificar comportamentos distintos, deve ser dividido.

Exceção permitida: verificar múltiplos campos do **mesmo resultado** (ex.: validar dois campos de um objeto retornado).

---

## 2. Mocks e Objetos de Teste

### 2.1 Object Mocks

Para qualquer objeto instanciado em mais de um teste, deve existir uma classe `*Mock` correspondente no pacote `test/mocks/`.
Exemplo: ServiceOrder possui uma classe mock ServiceOrderMock
Manter a estrutura do projeto para as classes, ou seja, se ServiceOrder está em domain.entities, a classe ServiceOrderMock estará em test/mocks/domain/entities
Cada Mock expõe métodos de fábrica nomeados pelo estado do objeto:

```java
public class ServiceOrderMock {

    public static ServiceOrder received() { ... }

    public static ServiceOrder inDiagnosis() { ... }

    public static ServiceOrder waitingApproval() { ... }
}
```

```java
public class StockMock {

    public static Stock available(int quantity) { ... }

    public static Stock reserved(int quantity) { ... }

    public static Stock empty() { ... }

    public static Stock belowMinimum() { ... }
}
```

```java
public class MaterialMock {

    public static Material withSufficientStock() { ... }

    public static Material withInsufficientStock() { ... }

    public static Material withPrice(BigDecimal unitPrice) { ... }
}
```

### 2.2 Regras para Object Mocks

- Métodos devem retornar objetos em estado **válido e consistente** para o cenário descrito.
- Não devem conter lógica de negócio — apenas montagem de estado.
- Devem usar valores fixos e determinísticos (sem `UUID.randomUUID()` inline; usar constantes).
- Quando necessário parametrizar, usar sobrecarga de método, nunca builders genéricos dentro do teste.

### 2.3 Mocks de repositórios

Repositórios devem ser mockados via framework (Mockito). Não criar implementações in-memory nos testes unitários de domínio.

```java
// Correto
StockRepository stockRepository = mock(StockRepository.class);
when(stockRepository.findByMaterialId(MATERIAL_ID)).thenReturn(Optional.of(StockMock.available(10)));

// Incorreto — não criar fake implementations em testes unitários de domínio
StockRepository stockRepository = new InMemoryStockRepository();
```

---

## 3. Casos de Teste Obrigatórios

### 3.1 ServiceOrder – Máquina de Estados

Classe de teste: `ServiceOrderStatusTransitionTest`

| Método | Transição testada |
|---|---|
| `shouldTransitionToInDiagnosis_whenOrderIsReceived` | `RECEIVED → IN_DIAGNOSIS` |
| `shouldThrowException_whenTransitioningFromReceivedToApproved` | `RECEIVED → APPROVED` (inválida) |
| `shouldTransitionToWaitingApproval_whenOrderIsInDiagnosis` | `IN_DIAGNOSIS → WAITING_APPROVAL` |
| `shouldTransitionToApproved_whenOrderIsWaitingApproval` | `WAITING_APPROVAL → APPROVED` |
| `shouldTransitionToRejected_whenOrderIsWaitingApproval` | `WAITING_APPROVAL → REJECTED` |
| `shouldThrowException_whenTransitioningFromApprovedToFinished` | `APPROVED → FINISHED` (inválida) |
| `shouldTransitionToInProgress_whenOrderIsApprovedAndHasNoStockPending` | `APPROVED → IN_PROGRESS` (válida) |
| `shouldThrowException_whenTransitioningToInProgress_andHasStockPending` | `APPROVED → IN_PROGRESS` (bloqueada por estoque) |
| `shouldTransitionToFinished_whenAllTasksAreFinished` | `IN_PROGRESS → FINISHED` |
| `shouldThrowException_whenTransitioningToFinished_andHasUnfinishedTasks` | `IN_PROGRESS → FINISHED` (bloqueada) |
| `shouldTransitionToDelivered_whenOrderIsFinished` | `FINISHED → DELIVERED` |

---

### 3.2 ServiceOrder – Regras de Negócio

Classe de teste: `ServiceOrderBusinessRulesTest`

| Método | Regra coberta |
|---|---|
| `shouldMarkOrderAsInProgress_whenFirstServiceIsStarted` | Iniciar serviço atualiza status da OS |
| `shouldNotAllowStartingService_whenOrderIsNotApproved` | Serviço só pode ser iniciado em OS aprovada |
| `shouldSetHasStockPendingToTrue_whenMaterialIsUnavailable` | Flag de pendência ativada corretamente |
| `shouldSetHasStockPendingToFalse_whenAllPendenciesAreResolved` | Flag de pendência desativada após resolução |
| `shouldCalculateBudgetCorrectly_whenMultipleServicesAreAdded` | Orçamento = soma dos `total_price` dos serviços |
| `shouldNotAllowAddingService_whenOrderIsInProgress` | Serviço não pode ser adicionado após início da execução |

---

### 3.3 Estoque – Adição de Serviço

Classe de teste: `AddServiceToOrderTest`

| Método | Comportamento |
|---|---|
| `shouldDeductStock_whenMaterialIsAvailable` | Estoque suficiente → deduz e reserva |
| `shouldMarkPendingItem_whenStockIsInsufficient` | Estoque insuficiente → cria `StockPendingItem` |
| `shouldDeductStockForEachMaterial_whenServiceHasMultipleMaterials` | Todos os materiais são processados individualmente |
| `shouldMarkAllMaterialsAsPending_whenNoneHasSufficientStock` | Múltiplos itens pendentes criados corretamente |
| `shouldCreateStockMovementOfTypeReserva_whenStockIsDeducted` | Movimentação do tipo `reserva` registrada |

---

### 3.4 Estoque – Rejeição de OS

Classe de teste: `RejectOrderStockRestorationTest`

| Método | Comportamento |
|---|---|
| `shouldRestoreReservedStock_whenOrderIsRejected` | Estoque `reservado` → `disponivel` |
| `shouldCreateStockMovementOfTypeRetorno_whenOrderIsRejected` | Movimentação do tipo `retorno` registrada |
| `shouldResolvePendingItems_whenReplenishedMaterialMatchesPendingOrder` | Reposição resolve pendência na OS mais antiga |
| `shouldPrioritizeOldestPendingOrder_whenReplenishingStock` | Ordem de resolução por `created_at ASC` |

---

### 3.4 Tarefas (OrderTask)

Classe de teste: `OrderTaskTest`

| Método | Comportamento |
|---|---|
| `shouldStartTask_whenTaskIsNotStarted` | `NOT_STARTED → STARTED` |
| `shouldFinishTask_whenTaskIsStarted` | `STARTED → FINISHED` |
| `shouldThrowException_whenStartingAlreadyFinishedTask` | Tarefa finalizada não pode ser reiniciada |
| `shouldThrowException_whenFinishingTaskThatWasNotStarted` | Tarefa não iniciada não pode ser finalizada |
| `shouldRecordStartedAt_whenTaskStarts` | Timestamp `started_at` preenchido |
| `shouldRecordFinishedAt_whenTaskFinishes` | Timestamp `finished_at` preenchido |

---

### 3.6 Value Objects

#### `Document`
Classe de teste: `DocumentTest`

| Método | Comportamento |
|---|---|
| `shouldCreateValidCpf_whenFormatIsCorrect` | CPF válido aceito |
| `shouldCreateValidCnpj_whenFormatIsCorrect` | CNPJ válido aceito |
| `shouldThrowException_whenCpfIsInvalid` | CPF inválido rejeitado |
| `shouldThrowException_whenCnpjIsInvalid` | CNPJ inválido rejeitado |
| `shouldThrowException_whenDocumentNumberIsBlank` | Número em branco rejeitado |

#### `LicensePlate`
Classe de teste: `LicensePlateTest`

| Método | Comportamento |
|---|---|
| `shouldCreateValidLicensePlate_whenFormatIsOldStandard` | Formato antigo aceito (AAA-9999) |
| `shouldCreateValidLicensePlate_whenFormatIsMercosul` | Formato Mercosul aceito (AAA9A99) |
| `shouldThrowException_whenLicensePlateFormatIsInvalid` | Formato inválido rejeitado |
| `shouldThrowException_whenLicensePlateIsBlank` | Placa em branco rejeitada |

#### `Budget`
Classe de teste: `BudgetTest`

| Método | Comportamento |
|---|---|
| `shouldCalculateTotalCorrectly_whenServicesAreProvided` | Soma dos `total_price` dos serviços |
| `shouldReturnZero_whenNoServicesAreProvided` | Lista vazia → orçamento zero |
| `shouldThrowException_whenBudgetIsNegative` | Valor negativo rejeitado |

---

## 4. Boas Práticas

### 4.1 O que deve ser testado

- Comportamentos expostos pelas entidades (métodos de intenção)
- Invariantes dos agregados
- Validações de value objects
- Transições de estado e suas guardas
- Exceções de domínio e suas mensagens

### 4.2 O que não deve ser testado

- Getters e setters sem lógica
- Construtores sem validação
- Código de infraestrutura (persistência, HTTP, integrations)
- Frameworks externos

### 4.3 Isolamento

- Cada teste deve ser **completamente independente**: sem estado compartilhado entre métodos.
- Nunca usar campos de instância mutáveis sem anotação `@BeforeEach` de reinicialização.
- Evitar dependência de ordem de execução dos testes.

### 4.4 Exceções de domínio

Ao testar exceções, sempre verificar tipo **e** mensagem:

```java
@Test
void shouldThrowException_whenTransitioningToInProgress_andHasStockPending() {
    // Arrange
    ServiceOrder order = ServiceOrderMock.approvedWithStockPending();

    // Act & Assert
    assertThatThrownBy(order::startExecution)
        .isInstanceOf(InvalidOrderStateException.class)
        .hasMessageContaining("pending stock");
}
```

### 4.4 Dados de teste

- Usar constantes nomeadas para IDs e valores fixos, nunca literais inline espalhados.
- Agrupar constantes de teste em uma classe `TestConstants` ou dentro de cada Mock.

```java
// Correto
private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

// Incorreto
ServiceOrder order = new ServiceOrder(UUID.fromString("abc123-..."), ...);
```

### 4.6 Assertivas

- Preferir `assertThat` do AssertJ para maior expressividade.
- Nunca usar `assertTrue(x.equals(y))` — usar `assertThat(x).isEqualTo(y)`.
- Assertions devem ser diretas no valor verificado, sem lógica condicional.

---

## 5. Anti-patterns Proibidos

| Anti-pattern | Descrição |
|---|---|
| **Test com múltiplos `act`** | Um teste com mais de uma ação encadeada viola o princípio de único comportamento |
| **Setup dentro do `act`** | Preparação de dados misturada com a chamada sendo testada |
| **Magic numbers/strings** | Valores literais sem nome no corpo do teste |
| **Teste sem assertion** | Teste que não falha independentemente do resultado |
| **Dependência entre testes** | Teste que depende de outro ter sido executado antes |
| **Mock de entidade de domínio** | Entidades do domínio nunca devem ser mockadas; usar Mocks |
| **Teste de implementação** | Testar como o resultado foi obtido, e não o resultado em si |