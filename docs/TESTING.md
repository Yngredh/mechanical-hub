# 🧪 Guia de Testes — Mechanical Hub

Este documento cobre o fluxo completo de teste da API, os cenários pré-carregados via seed e como executar os testes automatizados.

Use o Swagger (`http://localhost:8080/swagger-ui/index.html`) ou um cliente HTTP como Insomnia / Postman.

---

## Fluxo completo passo a passo

O fluxo abaixo cobre o ciclo completo de uma Ordem de Serviço desde a criação até a entrega do veículo.

### Passo 1 — Autenticar

```
POST /auth/login
```
```json
{
  "login": "admin@mechanicalhub.com",
  "password": "<senha-no-pdf>"
}
```
Copie o `token` da resposta e use-o em todos os próximos requests no header:
```
Authorization: Bearer <token>
```

---

### Passo 2 — Cadastrar material

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

### Passo 3 — Dar entrada no estoque

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

### Passo 4 — Cadastrar um serviço

```
POST /services
```
```json
{
  "name": "Troca de Filtro de Óleo",
  "description": "Substituição do filtro de óleo",
  "basePrice": 85.00,
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

### Passo 5 — Criar a Ordem de Serviço

```
POST /service-orders
```
```json
{
  "customer": {
    "name": "Maria Souza",
    "documentType": "CPF",
    "documentNumber": "935.411.347-80",
    "telephone": "551198765432",
    "email": "maria@email.com",
    "address": "Av. Paulista, 1000 - São Paulo/SP"
  },
  "vehicle": {
    "licensePlate": "DCA2E23",
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

### Passo 6 — Iniciar diagnóstico

```
PATCH /service-orders/<order-id>/status
```
```json
{ "status": "EM_DIAGNOSTICO" }
```

---

### Passo 7 — Adicionar serviços à OS

```
POST /service-orders/<id>/services
```
```json
{ "serviceIds": ["<id-do-servico>"] }
```
O sistema calcula o orçamento e reserva os materiais do estoque.

---

### Passo 8 — Enviar orçamento para aprovação

```
PATCH /service-orders/<id>/status
```
```json
{ "status": "AGUARDANDO_APROVACAO" }
```
Neste ponto o sistema enviaria o orçamento ao cliente via WhatsApp (mockado).

---

### Passo 9 — Cliente aprova o orçamento (endpoint público)

```
POST /mechanical-hub/service-orders/<id>/approve
```
> Sem token — endpoint público.

---

### Passo 10 — Iniciar execução do serviço

```
PATCH /service-orders/<id>/services/<taskId>/status
```
```json
{ "status": "INICIADO" }
```
A OS muda automaticamente para `EM_EXECUCAO`.

---

### Passo 11 — Finalizar o serviço

```
PATCH /service-orders/<id>/services/<taskId>/status
```
```json
{ "status": "FINALIZADO" }
```

---

### Passo 12 — Finalizar a OS

```
PATCH /service-orders/<id>/status
```
```json
{ "status": "FINALIZADO" }
```
> Só é possível quando **todos** os serviços da OS estiverem `FINALIZADO`.

---

### Passo 13 — Registrar entrega do veículo

```
PATCH /service-orders/<id>/status
```
```json
{ "status": "ENTREGUE" }
```

---

### Consultar OS pelo número (visão do cliente — sem token)

```
GET /mechanical-hub/service-orders/<order-number>
```

---

## ❌ Cenário: Recusa de orçamento e retorno de estoque

> Os dados abaixo já estão carregados pelo seed **V16** — não é necessário criar nada do zero.

A OS **`OS-202605-0035`** (`eeff3333-3333-3333-3333-000000000001`) já está em `AGUARDANDO_APROVACAO` com o serviço **Revisão do Sistema de Ignição** adicionado (materiais reservados: Vela de Ignição, Cabo de Vela e Bobina de Ignição).

**1. (Opcional) Consulte a OS antes de recusar:**
```
GET /service-orders/eeff3333-3333-3333-3333-000000000001
```

**2. Recuse o orçamento (endpoint público — sem token):**
```
POST /mechanical-hub/service-orders/eeff3333-3333-3333-3333-000000000001/reject
```

**3. Verifique que os materiais voltaram ao estoque:**

```
GET /stock/eeff1111-1111-1111-1111-000000000001
GET /stock/eeff1111-1111-1111-1111-000000000002
GET /stock/eeff1111-1111-1111-1111-000000000003
```

| Material | ID | `quantityReserved` esperado | `quantityAvailable` esperado |
|---|---|---|---|
| Vela de Ignição | `eeff1111-1111-1111-1111-000000000001` | `0` | `6` |
| Cabo de Vela de Ignição | `eeff1111-1111-1111-1111-000000000002` | `0` | `4` |
| Bobina de Ignição | `eeff1111-1111-1111-1111-000000000003` | `0` | `3` |

> Em `movements`, um registro com `movementType: "RETORNO"` confirma a devolução. A OS deverá ter status `RECUSADO`.

---

## ⚠️ Cenário: Pendência de estoque

> Os dados abaixo já estão carregados pelo seed **V16** — não é necessário criar nada do zero.

A OS **`OS-202604-0001`** (`5cce96e4-d0b2-42c1-a2b9-62fb6e97786e`) já está em `RECEBIDO` com o serviço **Manutenção de Sistema de Freios** adicionado e `hasStockPending = true`, pois os materiais desse serviço têm estoque 0:

| Material | ID | Estoque |
|---|---|---|
| Pastilha de Freio Dianteira | `99370d9f-1cbc-468d-8043-94588108b7c2` | 0 |
| Fluido de Freio DOT 4 | `a7f744e8-2a29-41ba-85f4-6eb13317af07` | 0 |

**1. Consulte a OS e confirme `hasStockPending = true`:**
```
GET /service-orders/5cce96e4-d0b2-42c1-a2b9-62fb6e97786e
```

**2. Dê entrada no estoque dos materiais pendentes:**
```
POST /stock/entry
```
```json
{ "materialId": "99370d9f-1cbc-468d-8043-94588108b7c2", "quantity": 5 }
```
```json
{ "materialId": "a7f744e8-2a29-41ba-85f4-6eb13317af07", "quantity": 5 }
```

**3. Verifique que `hasStockPending` foi automaticamente resolvido para `false`:**
```
GET /service-orders/5cce96e4-d0b2-42c1-a2b9-62fb6e97786e
```

---

## 🤖 Testes automatizados

```bash
# Rodar todos os testes
mvn test

# Rodar um teste específico
mvn test -Dtest=CustomerUseCaseTest

# Rodar com relatório detalhado
mvn test -Dsurefire.useFile=false
```

---

← [Voltar ao README](../README.md)
