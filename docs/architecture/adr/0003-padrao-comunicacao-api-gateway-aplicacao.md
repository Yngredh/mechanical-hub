# ADR-0003: Padrão de Comunicação entre API Gateway e Aplicação Principal

## Status
Aceito. Ver o adendo de conectividade privada (VPC Link + NLB interno) ao final.

## Contexto
A Fase 3 introduz um API Gateway na frente da aplicação principal, hoje exposta via Service/LoadBalancer no EKS. É necessário definir o protocolo e o padrão de comunicação entre o Gateway e a aplicação.

## Decisão
A comunicação entre o API Gateway e a aplicação principal será via **HTTP (REST)**, mantendo o padrão já usado desde a Fase 1/2 nos endpoints da aplicação (Spring Boot, documentados via Swagger/OpenAPI).

## Justificativa
- A aplicação já expõe uma API REST completa e documentada; usar HTTP/REST evita reescrever contratos ou introduzir um protocolo adicional (ex.: gRPC) sem necessidade concreta para o escopo do desafio.
- O AWS API Gateway tem suporte nativo e maduro a integrações HTTP (HTTP API ou REST API), apontando para um NLB ou Ingress do cluster EKS.
- Mantém consistência de contrato: os mesmos endpoints e schemas OpenAPI já existentes continuam válidos, só passam a ser roteados pelo Gateway.

## Alternativas Consideradas
- **gRPC**: rejeitado por exigir mudança de stack de comunicação (contratos `.proto`, client/server stubs) sem benefício claro para o escopo do desafio.
- **Mensageria assíncrona (SQS/EventBridge)**: fora de escopo — os fluxos exigidos (autenticação, abertura de OS, aprovação/rejeição) são request-response síncronos por natureza.

## Consequências
- Contratos permanecem definidos via OpenAPI/Swagger, sem necessidade de gerar/manter arquivos `.proto`.
- O API Gateway precisa de uma integração HTTP apontando para o serviço da aplicação principal no cluster.
- Rotas protegidas exigem o JWT emitido pelo Lambda Authorizer (ver RFC-0003); rotas públicas do cliente final seguem sem essa exigência.

---

## Adendo: Conectividade Privada via VPC Link + NLB Interno

**Contexto do adendo.** A decisão original deixava em aberto *como* o Gateway alcançaria o cluster — "apontando para um NLB ou Ingress". Na prática, o Service nasceu `type: LoadBalancer` com IP público. Isso abriu uma falha real: o `GatewayAuthenticationFilter` da aplicação confia nos cabeçalhos `x-user-id`/`x-user-role`/`x-user-name` sem validar assinatura, partindo da premissa de que só o API Gateway alcança o Service. Com o Service público, qualquer um que descobrisse o hostname do ELB podia forjar esses cabeçalhos e se autenticar como administrador sem passar pelo Lambda Authorizer.

**Decisão.** O Service passa a `type: NodePort`, numa porta fixa (`30080`, contrato entre `mechanical-hub` e `mechanical-hub-infra`). O `mechanical-hub-infra` provisiona um **Network Load Balancer interno** (subnets privadas, sem IP público) apontando para essa porta, anexado via Auto Scaling Group ao node group do EKS. O `mechanical-hub-auth` cria um `aws_api_gateway_vpc_link` até esse NLB e migra as integrações `HTTP_PROXY` de acesso direto por URL para `connection_type = "VPC_LINK"`.

Cadeia final: `cliente → API Gateway → VPC Link → NLB interno → NodePort → pod`.

**Por que o NLB é provisionado em Terraform, e não pelo Kubernetes.** O caminho nativo seria o AWS Load Balancer Controller, que exige IRSA — uma IAM role nova vinculada a um provedor OIDC do cluster. O AWS Academy Lab nega explicitamente qualquer escrita no namespace `iam:*` (confirmado em campo: até `iam:GetPolicy`, uma leitura, retorna `AccessDenied` por um guardrail explícito da conta do laboratório). Criar essa role não é possível neste ambiente. O provisionamento in-tree por anotação (`aws-load-balancer-type: nlb`) evitaria o problema de IAM, mas depende de um caminho de código que a AWS vem descontinuando, sem garantia de sobreviver a versões futuras do EKS. Provisionando o NLB como recurso Terraform comum no `mechanical-hub-infra`, a criação roda sob a mesma identidade que já aplica VPC/EKS/ECR com sucesso — nenhuma permissão nova é necessária.

**Efeito colateral na resolução do `application_base_url`.** A URL da aplicação deixou de ser um secret preenchido à mão (`APPLICATION_BASE_URL`) porque o NLB agora é um recurso Terraform com outputs de contrato (`app_nlb_arn`, `app_backend_base_url`) — o mesmo padrão já usado para `vpc_id` ou `ecr_repository_url`. Isso elimina a ordem circular que existia antes *para o `plan`/`apply`*: o NLB nasce junto com o `mechanical-hub-infra`, antes até do banco, então `terraform apply` do `auth` não fica mais bloqueado esperando o deploy da aplicação.

Isso não elimina, porém, uma dependência diferente que já existia antes deste adendo: o **smoke test** embutido no `deploy.yml` do `auth` (rodado logo após o `apply`, no mesmo job) faz um login de verdade contra o banco, e só devolve 401 corretamente se a role `mechanical_hub_auth` já existir com privilégio sobre `users.document_number` — o que exige que as migrations Flyway V18–V20 do `mechanical-hub` já tenham rodado (criam a coluna) e que o job da role (`sql/auth-database-role.sql`, no `mechanical-hub-database`) já tenha sido executado. Nenhuma dessas duas coisas depende do NLB ou do VPC Link — o smoke test fala direto com a Lambda, sem passar pelo Service. A sequência que faz a validação ponta a ponta funcionar continua sendo `infra → database → mechanical-hub (deploy, Flyway roda) → job da role → auth (apply + smoke test)`, e não a ordem `infra → database → auth → mechanical-hub` que aparece como dependência de state na ADR-0002 e no README do `auth` — aquela descreve quem lê o output de quem, não a ordem que faz o pipeline do `auth` passar verde da primeira vez.

**Consequência de segurança.** A premissa documentada no Javadoc do `GatewayAuthenticationFilter` — "esta aplicação só pode ser alcançável através do API Gateway" — deixa de ser uma premissa não verificada e passa a ser garantida pela topologia de rede: o Service não tem rota de fora da VPC.
