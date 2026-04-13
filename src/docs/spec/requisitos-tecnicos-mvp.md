# Requisitos Técnicos – mechanical-hub MVP

> Documento de referência para geração de tarefas técnicas do projeto.  
> Baseado nos requisitos funcionais, modelagem de dados e descrição técnica do sistema.

---

## Stack e Infraestrutura

- **Backend:** API REST
- **Autenticação:** JWT
- **Banco de Dados:** Relacional (conforme modelagem ER anexa)
- **Integrações externas mockadas:** WhatsApp (envio de orçamento), E-mail (notificações de estoque)

---

## Módulo 1 – Autenticação e Perfis

### Tarefas técnicas

**1.1 – Seed de perfis**
- Declarar os perfis válidos no sistema: `Mecânico` e `Administrador`
- Popular a tabela `profiles` com os dois registros no seed inicial

**1.2 – Autenticação JWT**
- Implementar endpoint de login (`POST /auth/login`)
  - Recebe: `email`, `password`
  - Retorna: token JWT com `user_id` e `profile_id` no payload
- Implementar middleware de autenticação para validar o token em todas as rotas protegidas
- Implementar middleware de autorização por perfil (ex.: `requireProfile('Administrador')`)

---

## Módulo 2 – Clientes

### Regras de negócio
- CPF e CNPJ devem ser validados antes de qualquer criação de Ordem de Serviço
- Não pode existir mais de um cliente com o mesmo `document_number`
- Caso o cliente não exista ao criar uma OS, deve ser cadastrado automaticamente

### Tarefas técnicas

**2.1 – CRUD de Clientes** *(perfil: Administrador)*
- `POST /customers` — cadastro (campos obrigatórios: `name`, `document_type`, `document_number`, `telephone`, `email`, `address`)
- `GET /customers` — listagem
- `GET /customers/:id` — detalhe + histórico de ordens de serviço vinculadas
- `PUT /customers/:id` — edição
- `DELETE /customers/:id` — remoção

**2.2 – Validações**
- Validar formato e dígitos verificadores de CPF e CNPJ
- Retornar erro tratado em caso de documento inválido ou duplicado

**2.3 – CRUD de Endereços**
- Tabela `customer_addresses` vinculada ao cliente
- Suportar múltiplos endereços por cliente

---

## Módulo 3 – Veículos

### Regras de negócio
- Veículo é identificado unicamente pela placa
- Caso o veículo não exista ao criar uma OS, deve ser cadastrado automaticamente
- A placa deve ser validada (formato Mercosul e antigo)

### Tarefas técnicas

**3.1 – CRUD de Veículos** *(perfil: Administrador)*
- `POST /vehicles` — cadastro (campos: `customer_id`, `license_plate`, `brand`, `model`, `year`, `color`)
- `GET /vehicles` — listagem
- `GET /vehicles/:id` — detalhe
- `PUT /vehicles/:id` — edição
- `DELETE /vehicles/:id` — remoção

**3.2 – Validação de Placa**
- Implementar validação de formato de placa brasileira (padrão antigo `AAA-9999` e Mercosul `AAA9A99`)
- Retornar erro tratado para placas inválidas ou duplicadas

---

## Módulo 4 – Ordens de Serviço

### Regras de negócio
- Status inicial ao criar: `Recebida`
- `order_number` gerado automaticamente no formato `OS-YYYYMM-NNNN` (ex.: `OS-202604-0001`)
- Apenas Mecânico ou superior podem iniciar o diagnóstico
- Status `Finalizada` só é permitido quando todos os serviços da ordem estiverem `Finalizado`
- Status `Em execução` é bloqueado se `has_stock_pending = true`
- Ao criar a OS: cadastrar cliente e/ou veículo automaticamente se não existirem

### Status possíveis (tabela `order_status`)

| Status | Gatilho |
|---|---|
| Recebida | Criação da OS |
| Em diagnóstico | Mecânico inicia diagnóstico |
| Aguardando aprovação | Diagnóstico finalizado, orçamento enviado |
| Aprovado | Cliente aprova via WhatsApp |
| Recusada | Cliente recusa via WhatsApp |
| Em execução | Mecânico inicia serviço (automático via serviço) |
| Finalizada | Todos os serviços finalizados |
| Entregue | Mecânico registra entrega |

### Tarefas técnicas

**4.1 – Criação de Ordem de Serviço** *(perfil: Administrador)*
- `POST /service-orders`
  - Recebe: dados do cliente, dados do veículo, `request_description` (max 255 chars)
  - Criar cliente/veículo automaticamente se não existirem
  - Gerar `order_number` sequencial por mês/ano
  - Definir status inicial: `Recebida`
  - Registrar `created_by_user_id`

**4.2 – Listagem e Detalhe**
- `GET /service-orders` — listagem com filtros (status, cliente, data)
- `GET /service-orders/:id` — detalhe completo (serviços, cliente, veículo, status)

**4.3 – Atualização de Status**
- `PATCH /service-orders/:id/status`
  - Validar perfil do usuário para `Em diagnóstico`
  - Validar `has_stock_pending` antes de transitar para `Em execução`
  - Validar se todos os serviços estão `Finalizado` antes de transitar para `Finalizada`
  - Registrar timestamps: `opened_at`, `completed_at`, `delivered_at` conforme status

**4.4 – Adicionar Serviços na Ordem**
- `POST /service-orders/:id/services`
  - Disponível apenas enquanto a ordem está `Em diagnóstico`
  - Recebe: array de `service_id`
  - Para cada serviço adicionado:
    1. Registrar relacionamento em `order_services` com status `Não iniciado`
    2. Buscar materiais necessários via `service_materials`
    3. Para cada material necessário, verificar a quantidade com status `disponivel` em `stock`
    4. **Se houver quantidade suficiente disponível:**
       - Deduzir a quantidade de `stock` (atualizar registros com status `disponivel` para `reservado`)
       - Registrar movimentação em `stock_movements` com `movement_type = 'reserva'`
    5. **Se faltar algum item:**
       - Criar um registro em `stock_pending_items` para cada item em falta (`service_order_id`, `material_id`, `created_at`)
       - Marcar `has_stock_pending = true` na OS
    6. Após deduções: verificar se `stock.quantity <= materials.min_stock_quantity` → disparar notificação e-mail (mockado)
  - Recalcular e atualizar `budget` da OS após adição dos serviços

**4.5 – Cálculo de Orçamento**
- Lógica interna (chamada ao adicionar/remover serviços):
  - `budget = Σ total_price` de todos os serviços da OS

**4.6 – Envio de Orçamento ao Cliente (mockado)**
- Ao finalizar o diagnóstico, acionar `WhatsAppService.sendBudget(order_id)` (mock)
- O método deve estar preparado para integração futura real
- Atualizar status para `Aguardando aprovação`

**4.7 – Webhook de Aprovação/Recusa**
- `POST /service-orders/:id/approve` — atualiza status para `Aprovado`
- `POST /service-orders/:id/reject`
  - Atualiza status para `Recusada`
  - Para cada item com status `reservado` vinculado à OS via `stock_movements`:
    1. Reverter o status dos registros de `reservado` para `disponivel` em `stock`
    2. Registrar movimentação em `stock_movements` com `movement_type = 'retorno'`
  - Acionar a **validação de pendências** para os materiais retornados (ver tarefa 7.5)

---

## Módulo 5 – Serviços

### Regras de negócio
- Serviços são pré-cadastrados e reutilizáveis entre ordens
- `total_price = labor_cost + Σ unit_price dos materiais vinculados`
- Status do serviço dentro de uma OS: `Não iniciado` → `Iniciado` → `Finalizado`
- Quando serviço muda para `Iniciado` → OS atualizada automaticamente para `Em execução`

### Tarefas técnicas

**5.1 – CRUD de Serviços** *(perfil: Administrador)*
- `POST /services` — cadastro
  - Recebe: `name`, `description`, `labor_cost`, `materials[]` (`material_id`, `quantity`)
  - Calcular e persistir `total_price` automaticamente
- `GET /services` — listagem
- `GET /services/:id` — detalhe
- `PUT /services/:id` — edição (recalcular `total_price`)
- `DELETE /services/:id` — remoção

**5.2 – Atualização de Status do Serviço na OS**
- `PATCH /service-orders/:id/services/:service_id/status`
  - Recebe: novo status
  - Se `Iniciado` → acionar atualização automática da OS para `Em execução`
  - Registrar `started_at` e `finished_at` conforme transição

**5.3 – Relatório de Tempo Médio de Execução**
- `GET /reports/services/avg-execution-time`
  - Agrupar registros de `order_services` por `service_id`
  - Calcular: `avg_time = Σ (finished_at - started_at) / count`
  - Retornar: lista de serviços com nome e tempo médio em minutos/horas

---

## Módulo 6 – Peças e Insumos (Materials)

### Tarefas técnicas

**6.1 – CRUD de Materiais** *(perfil: Administrador)*
- `POST /materials` — cadastro (campos: `name`, `description`, `unit_price`, `min_stock_quantity`)
- `GET /materials` — listagem
- `GET /materials/:id` — detalhe
- `PUT /materials/:id` — edição
- `DELETE /materials/:id` — remoção
- Ao cadastrar material, criar registro inicial em `stock` com `quantity = 0` e `status = 'disponivel'`

---

## Módulo 7 – Estoque

### Regras de negócio
- Cada unidade de um material em `stock` possui um status individual: `disponivel` ou `reservado`
- Ao cadastrar um novo material, o registro em `stock` é criado com status `disponivel` e `quantity = 0`
- Ao adicionar serviços em uma OS: itens disponíveis são marcados como `reservado`; itens em falta geram registros em `stock_pending_items`
- Ao recusar uma OS: itens `reservado` da ordem retornam para `disponivel` e disparam a validação de pendências
- Ao inserir novos itens no estoque: disparar a validação de pendências para o material inserido
- A validação de pendências resolve automaticamente `stock_pending_items` e atualiza `has_stock_pending` da OS quando não restar nenhum registro pendente

### Tarefas técnicas

**7.1 – Entrada de Estoque** *(perfil: Administrador)*
- `POST /stock/entries`
  - Recebe: array de `{ material_id, quantity }`
  - Incrementar `stock.quantity` e manter status `disponivel` nos registros adicionados
  - Registrar movimentação em `stock_movements` com `movement_type = 'entrada'`
  - Após cada entrada: acionar a **validação de pendências** para o `material_id` inserido (ver tarefa 7.5)

**7.2 – Consulta de Estoque**
- `GET /stock` — listagem de todos os materiais com quantidade total, quantidade disponível e quantidade reservada
- `GET /stock/:material_id` — detalhe do material com breakdown por status e histórico de movimentações

**7.3 – Notificação de Estoque Mínimo (mockado)**
- Após qualquer dedução, verificar se a quantidade com status `disponivel` ≤ `materials.min_stock_quantity`
- Se sim: acionar `EmailService.sendLowStockAlert(material_id)` (mock)
- O serviço deve estar preparado para integração futura real

**7.4 – Retorno de Itens ao Estoque (ao recusar OS)**
- Disparado automaticamente ao rejeitar uma OS
- Buscar todos os `stock_movements` com `service_order_id` e `movement_type = 'reserva'`
- Reverter status dos itens de `reservado` para `disponivel` em `stock`
- Registrar movimentação em `stock_movements` com `movement_type = 'retorno'`
- Acionar a **validação de pendências** para cada `material_id` retornado (ver tarefa 7.5)

**7.5 – Validação e Resolução de Pendências**
- Função interna acionada nos eventos: entrada de estoque (7.1) e retorno de itens (7.4)
- Recebe: `material_id`
- Lógica de execução:
  1. Buscar registros em `stock_pending_items` para o `material_id`, **ordenados por `created_at` ASC** (pendências mais antigas têm prioridade)
  2. Para cada registro pendente (enquanto houver quantidade `disponivel` suficiente em `stock`):
     - Deduzir a quantidade necessária de `stock` (marcar como `reservado`)
     - Registrar movimentação em `stock_movements` com `movement_type = 'reserva'`
     - Excluir o registro de `stock_pending_items`
  3. Após processar todos os registros possíveis, para cada `service_order_id` afetado:
     - Verificar se ainda existem registros em `stock_pending_items` vinculados à OS
     - Se não houver nenhum registro restante: atualizar `has_stock_pending = false` na OS

---

## Módulo 8 – Modelagem de Dados (referência)

As tabelas abaixo devem ser criadas conforme a modelagem ER do projeto:

| Tabela | Descrição |
|---|---|
| `customers` | Dados cadastrais do cliente |
| `customer_addresses` | Endereços vinculados ao cliente |
| `vehicles` | Veículos com vínculo ao cliente |
| `users` | Usuários do sistema |
| `profiles` | Perfis de acesso (Mecânico, Administrador) |
| `order_status` | Enum de status das ordens |
| `service_status` | Enum de status dos serviços dentro da OS |
| `service_orders` | Ordens de serviço |
| `services` | Catálogo de serviços disponíveis |
| `order_services` | Relacionamento OS ↔ Serviço (com status e timestamps) |
| `service_materials` | Relacionamento Serviço ↔ Material (com quantidade) |
| `materials` | Peças e insumos |
| `stock` | Quantidade atual em estoque por material, com coluna `status` (`disponivel` \| `reservado`) |
| `stock_movements` | Histórico de movimentações de estoque (`entrada`, `reserva`, `retorno`) |
| `stock_pending_items` | Registro de itens pendentes por OS: `id`, `service_order_id` FK, `material_id` FK, `created_at` |

### Detalhamento das alterações no modelo

**Tabela `stock` — nova coluna:**

| Coluna | Tipo | Descrição |
|---|---|---|
| `status` | `enum` | Estado do item no estoque: `disponivel` ou `reservado` |

- Valor padrão ao criar: `disponivel`
- Ao reservar para uma OS: `reservado`
- Ao retornar de uma OS recusada: `disponivel`

**Tabela `stock_pending_items` — nova tabela:**

| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | `uuid` PK | Identificador único |
| `service_order_id` | `uuid` FK | Referência à OS com pendência |
| `material_id` | `uuid` FK | Material em falta no estoque |
| `created_at` | `timestamp` | Data de criação (usada para priorizar pendências mais antigas) |

- Um registro por item em falta por OS
- Excluído automaticamente ao ser resolvido pela validação de pendências
- A ausência de registros para uma OS indica que `has_stock_pending` pode ser atualizado para `false`

---

## Observações Gerais

- **Todos os IDs** devem ser `UUID v4`
- **Timestamps** em UTC: `created_at`, `updated_at`, `opened_at`, `completed_at`, `delivered_at`, `started_at`, `finished_at`
- **Erros** devem retornar mensagens tratadas com códigos HTTP apropriados (400, 401, 403, 404, 409, 422)
- **Integrações externas** (WhatsApp e E-mail) devem ser implementadas como serviços com interface definida, retornando `true` no mock para facilitar substituição futura
- **Enums** de status (`order_status`, `service_status`) devem ser populados via seed e referenciados por FK nas tabelas correspondentes
