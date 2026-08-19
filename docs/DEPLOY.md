# 🚀 Guia de Deploy — Mechanical Hub

Este documento cobre o deploy manual da aplicação no Kubernetes e a leitura dos
outputs de infraestrutura publicados pelos repositórios de IaC.

> Para o fluxo automatizado via CI/CD, consulte o [fluxo de deploy na documentação de arquitetura](ARCHITECTURE.md#fluxo-de-deploy).

> **Este repositório não provisiona infraestrutura** (ADR-0002). VPC, EKS e ECR
> vêm do `mechanical-hub-infra`; o RDS, do `mechanical-hub-database`. Ordem de
> provisionamento: `infra → database → auth → aplicação`.

---

## ☸️ Deploy em Kubernetes

> Este fluxo pressupõe que `mechanical-hub-infra` e `mechanical-hub-database` já
> foram aplicados e que o `kubectl` está configurado para o cluster EKS.

### 1. Resolver os outputs de infraestrutura

```bash
cd ci/remote-state
terraform init -input=false
terraform apply -auto-approve -input=false \
  -var="state_bucket=mechanical-hub-tfstate-<account-id>" \
  -var="state_region=us-east-1"
```

Nada é criado por esse `apply`: a configuração contém apenas dois
`data "terraform_remote_state"`. Detalhes em [`ci/remote-state/README.md`](../ci/remote-state/README.md).

### 2. Configurar o kubectl para o cluster

```bash
aws eks update-kubeconfig \
  --name "$(terraform -chdir=ci/remote-state output -raw eks_cluster_name)" \
  --region us-east-1
```

### 3. Exportar variáveis necessárias

```bash
TF="terraform -chdir=ci/remote-state output -raw"

# Vindos dos states remotos
export ECR_REPOSITORY_URL="$($TF ecr_repository_url)"
export DB_HOST="$($TF rds_endpoint)"
export DB_PORT="$($TF rds_port)"
export DB_NAME="$($TF rds_db_name)"

# Locais / secrets
export IMAGE_TAG="<sha-da-imagem>"       # ex.: a1b2c3d4
export DB_USERNAME="mechanical_hub"
export DB_PASSWORD="<senha-do-banco>"
export CORS_ALLOWED_ORIGINS="*"
```

> `JWT_SECRET` não é mais necessário: a assinatura do token passou a ser
> responsabilidade da função serverless (`mechanical-hub-auth`).

### 4. Aplicar os manifests

```bash
MANIFEST_DIR=src/main/resources/k8s

kubectl apply -f $MANIFEST_DIR/namespace.yaml

for manifest in secret.yaml configmap-app.yaml deployment-app.yaml service-app.yaml hpa-app.yaml; do
  envsubst < "$MANIFEST_DIR/$manifest" | kubectl apply -f -
done
```

### 5. Acompanhar o rollout

```bash
kubectl rollout status deployment/mechanical-hub-api \
  --namespace production \
  --timeout=600s
```

### 6. Confirmar que o Service está saudável

O Service é `NodePort` (item 47 do plano) — não tem IP público, então não há
hostname de LoadBalancer para consultar. O acesso externo é via API Gateway,
através do VPC Link do `mechanical-hub-auth` até o NLB interno provisionado
pelo `mechanical-hub-infra`.

```bash
kubectl get endpoints mechanical-hub-svc --namespace production
```

Confirma que o Service tem pelo menos um pod pronto atrás do NodePort `30080`.

### Comandos úteis de diagnóstico

```bash
# Listar pods
kubectl get pods -n production -o wide

# Logs de um pod
kubectl logs -n production -l app=mechanical-hub-api --tail=100

# Descrever deployment
kubectl describe deployment mechanical-hub-api -n production

# Estado do HPA
kubectl get hpa -n production
```

---

## 🌩️ Infraestrutura — onde ela mora agora

A ADR-0002 dividiu a plataforma em quatro repositórios. Este aqui é
**consumidor puro**: lê o que os outros publicam e faz deploy da aplicação.
Provisionar a partir daqui é o que a divisão existe para impedir.

| Recurso | Repositório | State (key no bucket) |
|---|---|---|
| VPC, subnets, EKS, ECR | `mechanical-hub-infra` | `mechanical-hub-infra/terraform.tfstate` |
| RDS PostgreSQL 16 | `mechanical-hub-database` | `mechanical-hub-database/terraform.tfstate` |
| API Gateway, Lambdas | `mechanical-hub-auth` | `mechanical-hub-auth/terraform.tfstate` |
| Leitura dos states (`ci/remote-state/`) | **este repositório** | local e efêmero |

Para provisionar, `plan`, `apply`, `destroy` ou rodar os testes Terraform de um
módulo, trabalhe no repositório dono dele — os `terraform.tfvars.example`,
variáveis e `tests/` correspondentes foram migrados junto.

### Pré-requisitos para o `ci/remote-state`

```bash
# Verificar versão (requerida ≥ 1.7)
terraform version

# Configurar credenciais AWS (AWS Academy)
export AWS_ACCESS_KEY_ID="..."
export AWS_SECRET_ACCESS_KEY="..."
export AWS_SESSION_TOKEN="..."
export AWS_DEFAULT_REGION="us-east-1"
```

Basta permissão de leitura (`s3:GetObject`) no bucket de state.

### Variáveis do `ci/remote-state`

| Variável | Padrão | Descrição |
|---|---|---|
| `state_bucket` | _(obrigatório)_ | Bucket dos states, ex.: `mechanical-hub-tfstate-<account-id>` |
| `state_region` | `us-east-1` | Região do bucket |
| `infra_state_key` | `mechanical-hub-infra/terraform.tfstate` | State do `mechanical-hub-infra` |
| `database_state_key` | `mechanical-hub-database/terraform.tfstate` | State do `mechanical-hub-database` |

### Secrets exigidos pelo pipeline

| Secret | Uso |
|---|---|
| `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN` | Credenciais do AWS Academy |
| `AWS_ACCOUNT_ID` | Compor o nome do bucket de state |
| `DB_USERNAME`, `DB_PASSWORD` | Credenciais da aplicação no banco |
| `CORS_ALLOWED_ORIGINS` | Origens liberadas no ConfigMap |

Endpoint, porta e nome do banco **não** são secrets: vêm do state do
`mechanical-hub-database`.

---

← [Voltar ao README](../README.md)
