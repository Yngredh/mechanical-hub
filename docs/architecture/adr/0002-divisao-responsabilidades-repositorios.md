# ADR-0002: Divisão de Responsabilidades entre Repositórios

## Status
Aceito

## Contexto

A plataforma passou a ser organizada em quatro repositórios independentes, cada um com seu próprio pipeline de CI/CD e ciclo de vida de deploy. Essa separação isola o *blast radius* de cada mudança — um `terraform apply` para ajustar o banco não tem qualquer chance de afetar o cluster Kubernetes, e vice-versa — e permite que componentes com ritmos de evolução muito diferentes (a aplicação muda com frequência; a VPC quase nunca) evoluam sem bloquear uns aos outros.

Até então, todo o provisionamento vivia em um único diretório `infra/`, com os módulos acoplados diretamente entre si: o módulo `rds` recebia o security group do módulo `eks`, e ambos recebiam as subnets do módulo `vpc`. Ao separar os repositórios, essas dependências precisam ser resolvidas de forma explícita, sem acoplamento direto de código.

É necessário definir, sem ambiguidade, o que cada repositório provisiona e como eles compartilham informação.

## Decisão

### Responsabilidade de cada repositório

**`mechanical-hub-infra` — infraestrutura base e compartilhada**

- VPC: subnets públicas e privadas, route tables, internet/NAT gateway.
- Cluster Kubernetes (EKS): cluster e node group.
- Registro de imagens (ECR).

Racional: são recursos de plataforma, que não pertencem a nenhum domínio específico. O EKS é o ambiente de execução da aplicação, não parte dela. O ECR serve tanto à aplicação quanto, potencialmente, às Lambdas empacotadas como imagem.

**`mechanical-hub-database` — persistência**

- Instância RDS PostgreSQL, subnet group e security group do banco.
- RDS Proxy, caso venha a ser adotado, para gerenciamento de pool de conexões. **Não foi provisionado**: o AWS Academy Lab não libera os recursos de que ele depende (IAM dedicado e Secrets Manager), e as Lambdas conectam direto ao endpoint do banco — ver "Sem RDS Proxy" no README do `mechanical-hub-database`.
- Definição do usuário/role de banco com privilégio mínimo utilizado pela autenticação.

Racional: o banco tem ciclo de vida próprio e é o recurso de maior criticidade e menor tolerância a recriação acidental — isolá-lo em um repositório com pipeline próprio reduz o risco de uma mudança não relacionada afetá-lo.

**`mechanical-hub-auth` — autenticação**

- Código das funções serverless (Lambda Authorizer e Lambda de autenticação).
- Provisionamento das Lambdas: function, IAM role de execução, configuração de VPC (ENIs em subnets privadas) e security group próprio.
- API Gateway: rotas, integração HTTP com a aplicação principal e configuração do authorizer.

Racional: o API Gateway é indissociável do authorizer — configurar um sem o outro não produz um estado funcional. Mantê-los juntos evita coordenação de deploy entre dois repositórios para uma única mudança de comportamento de autenticação.

**`mechanical-hub` — aplicação principal**

- Código da API (Spring Boot).
- Migrations do banco de dados (Flyway).
- Manifests Kubernetes: Deployment, Service, HPA, ConfigMap e Secret.

Racional: as migrations acompanham o código que depende delas, garantindo que schema e aplicação evoluam em conjunto e sejam versionados no mesmo commit.

### Compartilhamento de informação entre repositórios

Cada repositório mantém seu próprio state remoto no S3 e **exporta outputs explícitos**. Repositórios consumidores leem esses valores via `terraform_remote_state`, nunca por hardcode de IDs ou endpoints.

Os outputs exportados passam a ser um **contrato entre repositórios**: alterá-los ou removê-los é uma mudança quebra-compatibilidade e exige coordenação, da mesma forma que o schema da tabela `USERS` é contrato entre a aplicação e a Lambda (ver RFC-0003).

Fluxo de consumo:

- `database` lê do state de `infra`: `vpc_id`, `private_subnet_ids`.
- `auth` lê do state de `infra`: `vpc_id`, `private_subnet_ids`; e do state de `database`: endpoint do banco.
- Pipeline de deploy de `mechanical-hub` lê do state de `infra`: nome do cluster EKS e URL do ECR; e do state de `database`: endpoint, porta e nome do banco.

### Ordem de provisionamento

Dependência de **state** (quem precisa ler o output de quem para o `terraform apply` rodar):

```
mechanical-hub-infra → mechanical-hub-database → mechanical-hub-auth → mechanical-hub (deploy)
```

Essa **não** é a ordem de execução dos pipelines num ambiente criado do zero. O `deploy.yml` do
`mechanical-hub-auth` roda um smoke test de login logo após o `apply`, e ele só responde 401 (em
vez de 500) depois que as migrations Flyway V18–V20 do `mechanical-hub` criaram
`users.document_number` e o job da role `mechanical_hub_auth` rodou no `mechanical-hub-database`.
É dependência de **dado**, não de state nem de rede. A sequência que passa de primeira é:

```
mechanical-hub-infra → mechanical-hub-database → mechanical-hub (deploy) → job da role → mechanical-hub-auth
```

Detalhe e justificativa no adendo da ADR-0003.

### Acesso ao banco: liberação por CIDR

O banco precisa aceitar conexões de dois consumidores: os pods da aplicação (no EKS) e as funções de autenticação. Como as Lambdas são criadas em `auth`, que é aplicado **depois** de `database`, referenciar o security group da Lambda a partir do banco criaria uma dependência circular entre repositórios.

Decidiu-se liberar o acesso ao RDS **pelo CIDR das subnets privadas**, em vez de por security group de origem. Todos os consumidores residem nessas subnets, e o banco não é exposto publicamente.

## Alternativas Consideradas

- **Manter tudo em um único repositório de infraestrutura**: rejeitado — não atende ao requisito de repositórios separados e mantém o acoplamento de blast radius entre banco e cluster.
- **Colocar o EKS no repositório da aplicação**: rejeitado — o cluster é plataforma compartilhada, e acoplá-lo ao ciclo de deploy da aplicação faria com que mudanças de código arriscassem afetar a infraestrutura de execução.
- **Colocar o API Gateway no repositório de infraestrutura base**: rejeitado — exigiria deploy coordenado entre dois repositórios a cada mudança de rota ou de configuração do authorizer.
- **Criar o security group da Lambda no repositório `infra`, para referenciá-lo no `database`**: alternativa válida e mais precisa em termos de menor privilégio, mas rejeitada por adicionar um recurso "órfão" em `infra` que pertence conceitualmente a `auth`, aumentando o acoplamento entre repositórios em troca de um ganho pequeno de segurança neste contexto (rede privada, banco não exposto publicamente).

## Consequências

- Cada repositório precisa manter `outputs.tf` explícito e documentado, tratado como interface pública e estável.
- Os pipelines de CI/CD precisam respeitar a ordem de provisionamento; um ambiente criado do zero não pode aplicar os repositórios em paralelo.
- Mudanças que atravessam fronteiras de repositório (ex.: um novo campo de banco consumido pela Lambda) exigem coordenação de PRs e deploys em mais de um repositório.
- A liberação por CIDR é menos granular do que por security group; caso a topologia de rede mude (ex.: cargas de trabalho não confiáveis passando a residir nas mesmas subnets privadas), essa decisão deve ser revisitada.
- O state remoto de cada repositório passa a ser dependência de leitura dos demais — permissões de acesso ao bucket S3 de state precisam contemplar isso.
