# RFC-0001: Escolha do Banco de Dados Gerenciado

## Status
Aceito

## Contexto

Após o levantamento dos requisitos do sistema da oficina, é preciso definir o banco de dados e a modelagem de dado que será utilizado no desenvolvimento.

## Proposta

Utilizar PostgreSQL via Amazon RDS como banco de dados gerenciado.

## Justificativa

### Por que relacional

1. **Integridade dos dados é uma exigência do domínio.** O domínio opera sobre dados fortemente relacionados. Uma ordem de serviço não existe sem um cliente e um veículo. Um item de estoque não existe sem um material. Uma movimentação de estoque não existe sem uma ordem. Essas dependências não são opcionais — são regras de negócio. Bancos relacionais impõem essas restrições nativamente via foreign keys e constraints, garantindo que nenhuma operação deixe o banco em estado inconsistente. Em um banco não relacional, essa responsabilidade cairia inteiramente sobre a aplicação, aumentando a superfície de bugs.
2. **Transações ACID são necessárias nas operações críticas.** As operações mais sensíveis do sistema envolvem múltiplas tabelas em uma única unidade de trabalho. Ao adicionar serviços a uma ordem, por exemplo, o sistema precisa simultaneamente deduzir o estoque disponível, criar o registro reservado, registrar a movimentação e atualizar a flag de pendência da ordem — tudo isso de forma atômica. Se qualquer etapa falhar, nenhuma deve persistir. Esse comportamento é garantido pelo modelo ACID dos bancos relacionais. Em um banco eventual ou sem suporte a transações distribuídas, implementar essa atomicidade exigiria mecanismos externos de compensação, aumentando significativamente a complexidade do sistema.
3. **O modelo de dados é naturalmente tabular e bem definido.** O domínio da oficina tem entidades claras, atributos bem definidos e relacionamentos estáveis. Não há necessidade de esquemas flexíveis ou documentos aninhados — pelo contrário, a rigidez do schema relacional é uma vantagem, pois impede que dados malformados sejam persistidos e facilita a evolução controlada do banco via migrations.
4. **Rastreabilidade e auditoria por design.** O sistema mantém histórico de todas as movimentações de estoque, timestamps de cada transição de status e registros de pendências. Consultas de auditoria e relatórios — como o tempo médio de execução por serviço — são expressas naturalmente em SQL com agregações, joins e filtros, sem necessidade de ferramentas adicionais. Esse ponto se reforça na Fase 3, que exige dashboards de tempo médio de execução por status e volume diário de ordens de serviço.
5. O PostgreSQL oferece recursos que se encaixam diretamente nas necessidades do sistema: suporte nativo a `UUID` como tipo de dado, índices compostos para otimizar as queries de pendências de estoque ordenadas por `created_at`, e `CHECK constraints` para validar enums diretamente no banco — como o status do estoque (`disponivel`, `reservado`).

## Alternativas Consideradas

- **Banco não relacional (NoSQL/documento)**: descartado pelos motivos estruturais acima — o domínio exige integridade referencial forte, transações atômicas multi-tabela e schema rígido, que um modelo de documentos não garante nativamente.
- **MySQL / SQL Server**: descartados por não agregarem vantagem técnica ao caso de uso atual.

## Consequências

- O Lambda Authorizer conecta-se ao mesmo RDS, com role de banco dedicada e privilégio mínimo (`mechanical_hub_auth`, só SELECT em `profiles` e em 6 colunas de `users`).
- O RDS Proxy foi considerado para gerenciar o pool de conexões, mas **não foi adotado**: ele exige uma IAM role própria e o Secrets Manager, ambos bloqueados pelos guardrails do AWS Academy Lab. A conexão é direta, com o pool da Lambda limitado a 2 conexões por instância fazendo o papel de contenção. Fora do laboratório o Proxy continua sendo a opção recomendada, e adotá-lo não quebra contrato: o output `rds_endpoint` passaria a apontar para ele. Justificativa completa em "Sem RDS Proxy" no README do `mechanical-hub-database`.