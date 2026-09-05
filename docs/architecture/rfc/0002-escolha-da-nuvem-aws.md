# RFC-0002: Escolha da Nuvem

## Status
Aceito

## Contexto

### O Problema
Após a implantação do sistema inicial para gestão de ordens de serviço, veículos, clientes e controle de peças, a oficina mecânica conquistou maior eficiência no atendimento. Porém, com o aumento da demanda, a expansão para novas unidades e a necessidade de garantir alta disponibilidade
do sistema, surgiu a necessidade de evoluir o sistema para automatizar o provisionamento e o deploy do ambiente.

### Objetivo
Evoluir a aplicação para garantir qualidade, resiliência e escalabilidade, incorporando práticas modernas de infraestrutura e automação — o que exige, como primeiro passo, escolher um provedor de nuvem.

## Proposta
Adotar a AWS como provedor de nuvem.

## Justificativa

- **Resolve diretamente os problemas relatados pela oficina.** A AWS oferece serviços gerenciados com escalabilidade nativa (Kubernetes via EKS com autoscaling, banco de dados gerenciado com opção de alta disponibilidade), suporte maduro a Infraestrutura como Código (Terraform) e integração natural com pipelines de CI/CD — endereçando de forma direta a necessidade de reduzir risco operacional, automatizar provisionamento/deploy e suportar picos de demanda com escalabilidade dinâmica.
- **Ampla adoção de mercado.** A AWS é o provedor de nuvem mais utilizado no mercado, o que se traduz em documentação extensa, comunidade grande, maior disponibilidade de exemplos e integrações de referência — reduzindo o tempo para transformar a automação de infraestrutura em ganho real de velocidade de entrega.
- **Experiência prévia do time.** O time de desenvolvimento já possui experiência acumulada com a AWS, reduzindo a curva de aprendizado e o risco de decisões arquiteturais equivocadas por desconhecimento da plataforma — importante quando o próprio problema a resolver é a lentidão na entrega.

## Alternativas Consideradas
- **Azure / GCP**: também endereçariam os problemas de escalabilidade e automação, mas foram descartadas por não haver, no time, o mesmo nível de experiência prévia disponível para a AWS, o que aumentaria o tempo de adaptação e o risco de atrasos — o oposto do que se busca ao resolver a lentidão na entrega de novas funcionalidades.

## Consequências
- Toda a infraestrutura passa a ser provisionada como código com o provider `aws` no Terraform.
- A Function Serverless será implementada como AWS Lambda.
- O API Gateway será o AWS API Gateway (não Kong/Traefik).
- O cluster Kubernetes roda em Amazon EKS, e o banco de dados gerenciado em Amazon RDS.
