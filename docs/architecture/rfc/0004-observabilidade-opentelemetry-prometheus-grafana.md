# RFC-0004: Observabilidade com OpenTelemetry, Prometheus, Loki, Tempo e Grafana

## Status
Aceito

## Contexto

A Fase 3 exige observabilidade cobrindo: latência de APIs, uso de CPU/memória do Kubernetes, healthchecks, alertas de falha no processamento de ordens de serviço, logs estruturados em JSON com correlação entre requisições, e dashboards (volume diário de OS, tempo médio de execução por status, erros/falhas de integração).

## Proposta

Adotar uma stack de observabilidade self-hosted, instalada dentro do próprio EKS via Terraform/Helm, seguindo o mesmo padrão já usado para o `metrics-server` no `mechanical-hub-infra`:

- **OpenTelemetry** (Collector + SDKs) como pipeline único de coleta de métricas, logs e traces — agente/instrumentação Java na aplicação principal, SDK Node/TypeScript nas Lambdas do `mechanical-hub-auth`.
- **Prometheus** para métricas, via `kube-prometheus-stack` (traz junto node-exporter, kube-state-metrics e Alertmanager).
- **Loki** para logs estruturados, correlacionados por `trace_id`/`span_id`.
- **Tempo** para traces distribuídos.
- **Grafana** como camada única de visualização, dashboards e alertas sobre os três backends.

Decisões de implementação que fazem parte desta proposta, não apenas detalhe:

- Armazenamento em filesystem/PVC (modo single-binary/monolítico do Loki e do Tempo), não em S3 — evita depender de uma IAM role nova via IRSA, pelo mesmo motivo que já levou ao descarte do AWS Load Balancer Controller.
- Dashboards e regras de alerta provisionados como código (ConfigMap via sidecar do Helm chart do Grafana), nunca criados manualmente pela UI — para sobreviver a um reset completo do ambiente do Lab, do mesmo jeito que o restante da infraestrutura já é reprovisionável do zero.

## Justificativa

- Atende a todos os requisitos funcionais da seção de observabilidade sem custo e sem depender de conta/API key de um provedor externo.
- Reaproveita o padrão de infraestrutura como código já estabelecido no `mechanical-hub-infra` (Terraform + `helm_release`), em vez de introduzir um mecanismo de instalação novo.
- Evita repetir o problema de guardrail de IAM já enfrentado no projeto, ao descartar desde já qualquer variante que dependa de IRSA.
- Dashboards e alertas como código sobrevivem a um reprovisionamento completo do cluster.

### Limitações assumidas conscientemente

- O node group (`t3.medium`, 2-4 nodes) precisa acomodar a stack de observabilidade ao lado da aplicação; a configuração inicial usa réplica única e retenção curta para caber no espaço disponível, podendo exigir redimensionar o node group.
- Diferente de uma solução SaaS, os dados de observabilidade residem no mesmo ambiente que pode ser destruído num reset completo do Lab: a configuração (dashboards, alertas, provisionamento) é código versionado e recuperável, mas o histórico acumulado de métricas/logs/traces não sobrevive a um reset sem backup externo — aceitável para os fins do desafio.
- O AWS API Gateway (REST v1) não propaga automaticamente contexto de trace W3C (`traceparent`) nas integrações `HTTP_PROXY`/`AWS_PROXY` — usa seu próprio mecanismo (X-Ray), desacoplado do OpenTelemetry. Na prática, o trace distribuído nasce na Lambda Authorizer ou na aplicação principal, não no próprio Gateway. Essa fronteira é assumida e documentada, não resolvida nesta fase.
- A instrumentação da Lambda usa o SDK mínimo do OTel (API + exporter OTLP, inicializado fora do handler, sem o pacote completo de auto-instrumentação) para reduzir o overhead de cold start no caminho de login.
- O canal de notificação dos alertas (Alertmanager/Grafana — e-mail, webhook, Slack) fica para a fase de implementação, não é definido por esta RFC.

## Alternativas Consideradas

**A) Datadog (tier gratuito).** Rejeitada para este projeto: o tier gratuito de infraestrutura limita a 5 hosts e a apenas 1 dia de retenção de métricas — insuficiente para o dashboard de volume diário de OS exigido pela Fase 3, que precisa de mais de um dia de histórico para mostrar tendência. Um plano pago não se justifica para um projeto acadêmico.

**B) New Relic (tier gratuito).** Atenderia tecnicamente: 100GB de ingestão gratuita por mês, 1 usuário completo + usuários básicos ilimitados, retenção padrão de 8 dias — daria conta dos requisitos funcionais sem custo. Rejeitada não por insuficiência técnica, mas por preferência de manter controle total sobre a stack, evitar mais uma credencial de terceiro para gerenciar como secret nos 4 repositórios, e pelo valor de aprendizado de instrumentar a stack do zero — mais alinhado ao caráter do desafio.

**C) Grafana Cloud (tier gratuito, gerenciado).** Oferece essencialmente a mesma stack (métricas + Loki + Tempo + Grafana) hospedada: tier gratuito de 10 mil séries ativas, 50GB de logs/mês, 50GB de traces/mês, retenção de 14 dias, 3 usuários. Rejeitada pelo mesmo motivo de (B) — dependência de conta externa e menos controle/aprendizado sobre o provisionamento —, mesmo sendo uma opção viável e possivelmente mais simples de operar do que o self-hosted completo.

**D) AWS CloudWatch Container Insights / X-Ray.** Nativo da conta AWS Academy Lab já em uso, sem instalação adicional. Rejeitada porque normalmente exige permissões de IAM adicionais para os agentes (mesmo risco de guardrail já enfrentado com o ALB Controller), gera custo por métrica/log ingerido mesmo dentro da conta de laboratório, e prende a solução ao provedor de nuvem — reduzindo o valor didático de uma stack portátil baseada em OpenTelemetry.

## Consequências

- `mechanical-hub-infra` passa a provisionar `kube-prometheus-stack`, Loki, Tempo e o OpenTelemetry Collector via `helm_release`, seguindo o padrão já usado para `metrics-server`; o node group pode precisar ser redimensionado.
- `mechanical-hub` (aplicação) adiciona `micrometer-registry-prometheus` (ou o agente Java do OTel) e passa a emitir logs em JSON estruturado com `trace_id`/`span_id` via MDC.
- `mechanical-hub-auth` (Lambdas) adiciona instrumentação OTel mínima (SDK Node) para métricas, traces e logs, exportando via OTLP para o Collector.
- Dashboards do Grafana (volume diário de OS, tempo médio por status, erros de integração) e regras de alerta passam a ser definidos como código, versionados no `mechanical-hub-infra`.
