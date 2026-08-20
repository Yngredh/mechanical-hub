# `ci/remote-state`

Configuração Terraform **somente leitura** usada pelo job `resolve-infra` do
pipeline. Não provisiona nada.

## Por quê

A ADR-0002 divide a plataforma em quatro repositórios e coloca o
`mechanical-hub` como **consumidor puro** de infraestrutura: ele lê o que os
outros repositórios publicam e faz deploy da aplicação. Provisionar VPC, EKS,
ECR ou RDS a partir de um push na aplicação é exatamente o blast radius que a
divisão existe para eliminar.

Este diretório é o ponto único onde essa leitura acontece.

## Contrato

| Output | Origem | Uso no pipeline |
| --- | --- | --- |
| `eks_cluster_name` | `mechanical-hub-infra` | `aws eks update-kubeconfig --name` |
| `ecr_repository_url` | `mechanical-hub-infra` | destino do `docker push` e imagem do Deployment |
| `rds_endpoint` | `mechanical-hub-database` | `DB_HOST` no ConfigMap |
| `rds_port` | `mechanical-hub-database` | `DB_PORT` no ConfigMap |
| `rds_db_name` | `mechanical-hub-database` | `DB_NAME` no ConfigMap |

Se um desses outputs sumir do repositório produtor, o `terraform output` falha
com o nome do valor ausente e o pipeline para **antes** de tentar um deploy
contra um contrato quebrado.

## Uso local

```bash
cd ci/remote-state
terraform init -input=false
terraform apply -auto-approve -input=false \
  -var="state_bucket=mechanical-hub-tfstate-<conta>" \
  -var="state_region=us-east-1"

terraform output -raw eks_cluster_name
terraform output -raw rds_endpoint
```

O `apply` apenas resolve os dois `data "terraform_remote_state"` — não há
recurso gerenciado nesta configuração, então nada é criado, alterado ou
destruído. O state gerado é local e efêmero (descartado com o runner), por isso
não há bloco `backend` aqui.

Requer credenciais AWS com permissão de leitura (`s3:GetObject`) no bucket de
state.
