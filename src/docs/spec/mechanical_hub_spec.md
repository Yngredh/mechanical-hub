# Mechanical Hub

Mechanical Hub automates customer serviceData and mechanical serviceData execution processes, eliminating the use of manual note-taking and isolated spreadsheets.

---

# Goal

The goal of the MVP is to cover the complete lifecycle of Service Orders, including inventory control of parts and supplies, automation of quotes, and generation of execution time metrics by serviceData type.

- **Service Order Management** — Complete cycle: opening, diagnosis, approval, execution, completion, and delivery.
- **Inventory Control** — Entry, automatic reservation per order, stock status tracking (`available` / `reserved`), pending item registry, notifications of minimum stock reached, and automatic resolution of pending items.
- **Automated Quotation** — Automatic calculation based on serviceData, parts, and labor included in the order.
- **Execution Metrics** — Report of average execution time by serviceData type.

---

# The Problem

The current process for vehicle serviceData, diagnosis, repair, and delivery is disorganized, relying on manual notes and spreadsheets, leading to the following problems:

- Errors in prioritizing serviceData requests
- Failures in parts and supplies control
- Difficulty in tracking serviceData status in real time
- Loss of customer and vehicle history
- Inefficiency in the flow of quotes and authorizations

---

# Linguagem Ubíqua

- **Cliente** — Pessoa responsável pelo veículo e que solicita serviços na oficina.
- **Veículo** — Automóvel do cliente que será submetido a manutenção.
- **Ordem de Serviço (OS)** — Registro que representa um contrato de serviço que a oficina prestará ao cliente.
- **Mecânico** — Pessoa especializada responsável por executar as ordens de serviço.
- **Insumos** — Material consumido durante a execução do serviço (óleo, fluido de freio, etc.).
- **Peças** — Material utilizado na execução de um serviço.
- **Diagnóstico** — Análise técnica realizada pelo Mecânico para identificar defeitos ou necessidades de manutenção no veículo.
- **Orçamento** — Levantamento inicial do custo do trabalho solicitado pelo cliente.
- **Item Disponível** — Peça ou insumo em estoque sem alocação, podendo ser utilizado em novas ordens.
- **Item Reservado** — Peça ou insumo em estoque já alocado para uma ordem de serviço específica.
- **Pendência de Estoque** — Situação em que um item necessário para uma OS não possui quantidade disponível no estoque no momento da adição do serviço.
- **Materiais** — Se trata dos itens necessários para realizar um serviço, isto é peças e insumos.

---

# Perfis & Controle de Acesso

**RN-PERFIS | Sistema**
O sistema possui dois perfis: **Mecânico** e **Administrador**. A autenticação utiliza `JWT`.

**RN-RESP-MECÂNICO | Mecânico**
- Iniciar diagnóstico de uma ordem
- Adicionar serviços durante o diagnóstico
- Iniciar execução de serviços
- Finalizar serviços e ordens
- Registrar entrega do veículo

**RN-RESP-ADMIN | Administrador**
- Gerenciamento completo de clientes
- Criação de ordens de serviço
- Gerenciamento de veículos
- Gerenciamento de serviços
- Gerenciamento de peças e insumos
- Gerenciamento de estoque

---

# Ordens de Serviço

## Fluxo de Status

```
Recebida → Em diagnóstico → Aguardando aprovação → Aprovada → Em execução → Finalizada → Entregue
                                                  ↘ Recusada
```

## Descrição dos Status

| Status | Descrição |
|---|---|
| **Recebida** | Ordem recém criada no sistema |
| **Em diagnóstico** | Mecânico iniciou a avaliação do veículo |
| **Aguardando aprovação** | Orçamento enviado ao cliente via WhatsApp |
| **Aprovada** | Cliente aprovou a ordem e o orçamento |
| **Recusada** | Cliente recusou — itens reservados no estoque são reposicionados para disponível |
| **Em execução** | Mecânico iniciou ao menos um serviço |
| **Finalizada** | Todos os serviços marcados como Finalizado |
| **Entregue** | Veículo entregue ao cliente |

---

# Regras de Negócio

## Ordens de Serviço

**RN-OS-NUMERAÇÃO | Auto**
Cada ordem recebe um identificador único no formato `OS-YYYYMM-NNNN`.
Ex: `OS-202604-0001`, `OS-202604-0042`

**RN-OS-CADASTRO | Administrador**
Para criar uma OS é necessário: dados do cliente (nome, CPF/CNPJ, endereço, telefone, email), dados do veículo (placa, marca, modelo, ano, cor) e `request_description` (até 255 caracteres). Caso o cliente ou veículo não existam, são cadastrados automaticamente.

**RN-OS-EXECUÇÃO-PERFIL | Mecânico**
Apenas usuários com perfil Mecânico ou superior podem iniciar o diagnóstico de uma OS.

**RN-OS-FINALIZAÇÃO | Validação**
O status da OS só pode ser atualizado para **Finalizada** quando todos os serviços vinculados estiverem com status **Finalizado**.

**RN-OS-PENDÊNCIA-ESTOQUE | Validação**
Ordens com `has_stock_pending = true` não podem ter status atualizado para **Em execução**. A flag é ativada automaticamente ao adicionar serviços quando faltam itens disponíveis em estoque.

**RN-OS-ORÇAMENTO | Cálculo**
O cálculo é executado ao adicionar serviços na ordem.
**Fórmula:** `budget = Σ(total_price de todos os serviços incluídos na ordem)`

**RN-OS-APROVAÇÃO-WHATSAPP | Integração**
O sistema envia o resumo do orçamento ao cliente via WhatsApp (mockado no MVP). A aprovação ou recusa atualiza o status automaticamente para **Aprovada** ou **Recusada**.

**RN-OS-RECUSA-ESTOQUE | Estoque**
Quando uma OS for recusada, todos os itens com status `reservado` alocados para essa ordem devem ter seu status revertido para `disponivel` automaticamente. Após o retorno dos itens, o sistema deve acionar a validação de pendências para cada material retornado (ver `RN-EST-RESOLVER-PENDÊNCIAS`).

---

## Serviços

**RN-SVC-CADASTRO | Administrador**
Campos para cadastro: Nome, Descrição, Custo de mão de obra, Materiais utilizados (item + quantidade).
Campos calculados automaticamente: Preço base, Preço total.
**Fórmula:** `total_price = labor_cost + Σ(unit_price dos materiais)`

**RN-SVC-STATUS | Mecânico**
Status possíveis de um serviço em uma OS: **Não iniciado** (padrão ao adicionar), **Iniciado**, **Finalizado**.
Ao marcar como _Iniciado_, o status da OS é automaticamente atualizado para **Em execução**.

**RN-SVC-RELATÓRIO | Administrador**
O sistema disponibiliza relatório de tempo médio de execução por tipo de serviço.
**Cálculo:** Agrupar registros de `order_services` por `service_id`, somar `(finished_at - started_at)` e dividir pelo total de registros do grupo.

---

## Estoque

**RN-EST-STATUS-ITEM | Modelo**
Cada item em estoque possui um status individual que representa sua disponibilidade:

| Status | Descrição |
|---|---|
| `disponivel` | Item está em estoque e pode ser alocado para novas ordens |
| `reservado` | Item está em estoque, porém já foi alocado para uma ordem de serviço específica |

Ao cadastrar um novo material, seu registro inicial em `stock` é criado com `status = disponivel` e `quantity = 0`.

**RN-EST-ENTRADA | Administrador**
O sistema permite o registro de entrada de novos itens no controle de estoque. Os itens inseridos recebem `status = disponivel`. A entrada gera uma movimentação em `stock_movements` com `movement_type = 'entrada'`. Após registrar a entrada, o sistema aciona automaticamente a validação de pendências para o material inserido (ver `RN-EST-RESOLVER-PENDÊNCIAS`).

**RN-EST-DEDUÇÃO | Automático**
Ao adicionar serviços em uma OS, o sistema executa para cada material necessário:

1. Verifica a quantidade com `status = disponivel` em `stock`
2. **Se houver quantidade suficiente disponível:**
   - Atualiza o status dos itens de `disponivel` para `reservado` em `stock`
   - Registra movimentação em `stock_movements` com `movement_type = 'reserva'`
3. **Se faltar algum item:**
   - Cria um registro em `stock_pending_items` para cada item em falta, contendo `service_order_id`, `material_id` e `created_at`
   - Ativa `has_stock_pending = true` na OS
   - Envia notificação por e-mail aos administradores sobre a falta do item

**RN-EST-MÍNIMO | Notificação**
Após qualquer dedução (reserva de itens), o sistema verifica se a quantidade com `status = disponivel` restante é inferior ao `min_stock_quantity` do material. Se sim, notifica os administradores por e-mail.

**RN-EST-PENDÊNCIAS-REGISTRO | Modelo**
A tabela `stock_pending_items` registra individualmente cada item em falta por OS. A ordenação por `created_at ASC` garante que pendências mais antigas sejam resolvidas com prioridade. A ausência de registros nessa tabela para uma OS indica que a pendência foi totalmente resolvida.

**RN-EST-RESOLVER-PENDÊNCIAS | Automático**
Acionada nos eventos de entrada de estoque e retorno de itens por recusa de OS. Para cada material envolvido no evento:

1. Busca registros em `stock_pending_items` para o material, **ordenados por `created_at` ASC** (prioridade para pendências mais antigas)
2. Para cada registro pendente, enquanto houver quantidade `disponivel` suficiente em `stock`:
   - Atualiza o status dos itens de `disponivel` para `reservado` em `stock`
   - Registra movimentação em `stock_movements` com `movement_type = 'reserva'`
   - Exclui o registro de `stock_pending_items`
3. Para cada `service_order_id` afetado, verifica se ainda existem registros em `stock_pending_items`:
   - Se não existir nenhum registro restante: atualiza `has_stock_pending = false` na OS

**RN-EST-RETORNO-RECUSA | Automático**
Quando uma OS for recusada, o sistema:

1. Busca todas as movimentações em `stock_movements` com `service_order_id` da OS e `movement_type = 'reserva'`
2. Reverte o status dos itens de `reservado` para `disponivel` em `stock`
3. Registra novas movimentações em `stock_movements` com `movement_type = 'retorno'`
4. Aciona `RN-EST-RESOLVER-PENDÊNCIAS` para cada material retornado

---

## Clientes

**RN-CLI-CADASTRO | Administrador**
Campos obrigatórios: Nome, CPF ou CNPJ, Endereço completo, Telefone, E-mail.

**RN-CLI-VALIDAÇÃO | Validação**
CPF e CNPJ devem ser validados (formato e dígitos verificadores) antes da criação. Não pode existir mais de um cliente com o mesmo CPF/CNPJ.

**RN-CLI-HISTÓRICO | Rastreio**
O sistema mantém o histórico completo de todas as ordens de serviço associadas ao cliente.

---

## Veículos

**RN-VEI-CADASTRO | Administrador**
Campos obrigatórios: Cliente vinculado, Placa, Marca, Modelo, Ano, Cor.
Cada veículo é identificado de forma única pela placa.

**RN-VEI-PLACA | Validação**
O sistema valida o formato da placa (Mercosul `AAA9A99` e padrão antigo `AAA9999`) para assegurar que o valor inserido é válido.

---

## Peças e Insumos

**RN-MAT-CADASTRO | Administrador**
Campos obrigatórios: Nome, Descrição, Preço unitário, Quantidade mínima em estoque (`min_stock_quantity`).
Ao cadastrar um material, um registro inicial é criado em `stock` com `quantity = 0` e `status = disponivel`.

---

# Stack do Sistema

| Camada | Tecnologia |
|---|---|
| Backend | Java / Spring Boot |
| Banco de Dados | PostgreSQL |
| Autenticação | JWT |
| ORM | Spring Data JPA |
| Notificações | E-mail + WhatsApp (mockados no MVP) |

---

# Modelo de Dados — Tabelas Principais

| Tabela | Descrição |
|---|---|
| `customers` | Dados cadastrais dos clientes |
| `customer_addresses` | Endereços vinculados ao cliente |
| `vehicles` | Veículos com vínculo ao cliente |
| `users` | Usuários do sistema |
| `profiles` | Perfis de acesso: Mecânico e Administrador |
| `order_status` | Enum de status das ordens de serviço |
| `service_status` | Enum de status dos serviços dentro de uma OS |
| `service_orders` | Ordens de serviço |
| `serviceData` | Catálogo de serviços disponíveis |
| `order_services` | Relacionamento OS ↔ Serviço (status e timestamps) |
| `service_materials` | Relacionamento Serviço ↔ Material (quantidade) |
| `materials` | Peças e insumos |
| `stock` | Quantidade e status (`disponivel` \| `reservado`) por material |
| `stock_movements` | Histórico de movimentações (`entrada`, `reserva`, `retorno`) |
| `stock_pending_items` | Registro de itens pendentes por OS e material |

---

# Endpoints Principais

## Ordens de Serviço
| Método | Rota | Descrição | Perfil |
|---|---|---|---|
| `POST` | `/serviceData-orders` | Criar OS (cria cliente/veículo se necessário) | Admin |
| `GET` | `/serviceData-orders` | Listar OS com filtros | Admin |
| `GET` | `/serviceData-orders/:id` | Detalhar OS | Admin |
| `PATCH` | `/serviceData-orders/:id/status` | Atualizar status da OS | Mecânico |
| `POST` | `/serviceData-orders/:id/serviceData` | Adicionar serviços à OS | Mecânico |
| `POST` | `/serviceData-orders/:id/approve` | Aprovar OS (cliente) | — |
| `POST` | `/serviceData-orders/:id/reject` | Recusar OS (cliente) | — |

## Serviços
| Método | Rota | Descrição | Perfil |
|---|---|---|---|
| `POST` | `/serviceData` | Cadastrar serviço | Admin |
| `GET` | `/serviceData` | Listar serviços | Admin |
| `PUT` | `/serviceData/:id` | Editar serviço | Admin |
| `DELETE` | `/serviceData/:id` | Remover serviço | Admin |
| `PATCH` | `/order-serviceData/:id/status` | Atualizar status do serviço na OS | Mecânico |
| `GET` | `/serviceData/report/execution-time` | Relatório de tempo médio de execução | Admin |

## Clientes e Veículos
| Método | Rota | Descrição | Perfil |
|---|---|---|---|
| `POST` | `/customers` | Cadastrar cliente | Admin |
| `GET` | `/customers` | Listar clientes | Admin |
| `GET` | `/customers/:id` | Detalhar cliente | Admin |
| `PUT` | `/customers/:id` | Editar cliente | Admin |
| `DELETE` | `/customers/:id` | Remover cliente | Admin |
| `GET` | `/customers/:id/orders` | Histórico de ordens do cliente | Admin |
| `POST` | `/vehicles` | Cadastrar veículo | Admin |
| `GET` | `/vehicles` | Listar veículos | Admin |
| `PUT` | `/vehicles/:id` | Editar veículo | Admin |
| `DELETE` | `/vehicles/:id` | Remover veículo | Admin |

## Peças, Insumos e Estoque
| Método | Rota | Descrição | Perfil |
|---|---|---|---|
| `POST` | `/materials` | Cadastrar peça/insumo | Admin |
| `GET` | `/materials` | Listar peças/insumos | Admin |
| `PUT` | `/materials/:id` | Editar peça/insumo | Admin |
| `DELETE` | `/materials/:id` | Remover peça/insumo | Admin |
| `POST` | `/stock/entry` | Registrar entrada em estoque | Admin |
| `GET` | `/stock` | Consultar estoque (disponível e reservado) | Admin |
| `GET` | `/stock/:material_id` | Detalhar estoque de um material | Admin |
