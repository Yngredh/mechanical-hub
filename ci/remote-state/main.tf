# =============================================================================
# Leitura dos states remotos — somente leitura (ADR-0002)
# =============================================================================
#
# Este diretorio NAO provisiona nada. Ele existe para que o pipeline de
# `mechanical-hub` resolva, em um unico `terraform init` + `terraform output`,
# os valores que os repositorios de infraestrutura publicam como contrato:
#
#   mechanical-hub-infra     -> eks_cluster_name, ecr_repository_url
#   mechanical-hub-database  -> rds_endpoint, rds_port, rds_db_name, rds_username
#
# A aplicacao e consumidora pura: um push aqui nunca altera VPC, EKS, ECR ou
# RDS. Provisionar essas coisas e responsabilidade exclusiva dos repositorios
# `mechanical-hub-infra` e `mechanical-hub-database`, cada um com seu proprio
# state e seu proprio pipeline.
#
# Nao ha bloco `backend` proposital: o state deste diretorio e efemero e local
# ao runner. `terraform init` roda sem `-backend-config` e o `terraform.tfstate`
# gerado contem apenas os data sources — e descartado com o runner.
#
# Uso:
#   terraform init -input=false
#   terraform apply -auto-approve -input=false \
#     -var="state_bucket=mechanical-hub-tfstate-<conta>" \
#     -var="state_region=us-east-1"
#   terraform output -raw eks_cluster_name
#
# `apply` aqui e apenas a leitura dos data sources; nenhum recurso e criado.

terraform {
  required_version = ">= 1.7.0"
}

variable "state_bucket" {
  description = "Bucket S3 que hospeda os states dos repositorios de infraestrutura."
  type        = string

  validation {
    condition     = length(var.state_bucket) > 0
    error_message = "state_bucket e obrigatorio (ex.: mechanical-hub-tfstate-<conta>)."
  }
}

variable "state_region" {
  description = "Regiao do bucket de state."
  type        = string
  default     = "us-east-1"
}

variable "infra_state_key" {
  description = "Chave do state do mechanical-hub-infra dentro do bucket."
  type        = string
  default     = "mechanical-hub-infra/terraform.tfstate"
}

variable "database_state_key" {
  description = "Chave do state do mechanical-hub-database dentro do bucket."
  type        = string
  default     = "mechanical-hub-database/terraform.tfstate"
}

# ── States remotos ───────────────────────────────────────────────────────────

data "terraform_remote_state" "infra" {
  backend = "s3"

  config = {
    bucket = var.state_bucket
    key    = var.infra_state_key
    region = var.state_region
  }
}

data "terraform_remote_state" "database" {
  backend = "s3"

  config = {
    bucket = var.state_bucket
    key    = var.database_state_key
    region = var.state_region
  }
}

# ── Outputs consumidos pelo pipeline ─────────────────────────────────────────
#
# Os nomes abaixo espelham os outputs de origem. Se um deles sumir do
# repositorio produtor, o `terraform output` falha aqui com o nome exato do
# valor ausente — que e o comportamento desejado: o pipeline para antes de
# fazer deploy contra um contrato quebrado.

output "eks_cluster_name" {
  description = "Nome do cluster EKS. Alimenta `aws eks update-kubeconfig --name`."
  value       = data.terraform_remote_state.infra.outputs.eks_cluster_name
}

output "ecr_repository_url" {
  description = "URL completa do repositorio ECR, usada como destino do docker push."
  value       = data.terraform_remote_state.infra.outputs.ecr_repository_url
}

output "rds_endpoint" {
  description = "Hostname do RDS. Vira DB_HOST no ConfigMap da aplicacao."
  value       = data.terraform_remote_state.database.outputs.rds_endpoint
}

output "rds_port" {
  description = "Porta do RDS. Vira DB_PORT no ConfigMap da aplicacao."
  value       = data.terraform_remote_state.database.outputs.rds_port
}

output "rds_db_name" {
  description = "Nome do banco. Vira DB_NAME no ConfigMap da aplicacao."
  value       = data.terraform_remote_state.database.outputs.rds_db_name
}

# Vem do state em vez de um secret do GitHub de proposito: o usuario do banco
# nao e sigiloso (a senha e), e ele ja e publicado como contrato pelo
# mechanical-hub-database. Lendo daqui, o valor nao pode divergir do que foi
# provisionado nem chegar vazio sem o pipeline perceber — um secret ausente
# resolvia para string vazia, o driver JDBC caia no usuario do SO do container
# ("root") e o Flyway falhava com "password authentication failed for user
# root", um erro que nao aponta para a causa.
output "rds_username" {
  description = "Usuario master do RDS. Vira DB_USERNAME no ConfigMap da aplicacao."
  value       = data.terraform_remote_state.database.outputs.rds_username
}
