# ADR-0001: Estratégia de Escalabilidade (HPA)

## Status
Aceito

## Contexto
Com o aumento da demanda e a expansão da oficina para novas unidades, surgiu a necessidade de garantir alta disponibilidade do sistema. O sistema passou a apresentar momentos pontuais de pico de acesso, o que motivou a implantação de escalabilidade horizontal via Horizontal Pod Autoscaler (HPA) no cluster Kubernetes.

Esse volume de pico, no entanto, é controlado: a quantidade de pessoas atendidas em uma oficina não cresce com muita frequência nem muito rapidamente, de unidade para unidade. Não se trata de um sistema sujeito a picos súbitos e imprevisíveis de tráfego (como um e-commerce em campanha), e sim de uma variação moderada e previsível ao longo do dia e entre unidades.

## Decisão
Implantar escalabilidade via HPA na aplicação principal, escalando de 2 a 4 réplicas com base em CPU (70%) e memória (300Mi), sobre um node group elástico.

> **Dimensionamento do node group (atualizado na RFC-0004).** Os valores em vigor no `mechanical-hub-infra` são `desired: 3`, `min: 1`, `max: 5` (`t3.medium`). Originalmente eram `desired: 2` / `max: 4`; a stack de observabilidade (Prometheus, Loki, Tempo, Grafana e o coletor) soma cerca de 0,6 vCPU e 1,8 GiB só em *requests* e passaria a disputar espaço com a aplicação escalada pelo HPA, deixando pods em `Pending`. A faixa do HPA (2 a 4 réplicas) não mudou.

## Justificativa
- A necessidade real é absorver picos moderados de acesso e garantir alta disponibilidade — não sustentar crescimento explosivo ou imprevisível de tráfego.
- Escalar por CPU/memória é suficiente e proporcional ao problema: o gargalo observado é consumo de recursos em horários de maior movimento, não fila de mensagens, latência de terceiros ou outro gatilho que justificasse métricas customizadas.
- Uma faixa de escalonamento modesta (2 a 4 réplicas) reflete o padrão real de demanda da oficina: picos existem, mas dentro de uma amplitude conhecida e limitada, não um crescimento acelerado do número de unidades ou de clientes.
- Manter a configuração simples (HPA nativo do Kubernetes) reduz complexidade operacional desnecessária para um padrão de carga que já é bem compreendido.

## Alternativas Consideradas
- **Escalonamento por métricas customizadas (KEDA)**: descartado — adicionaria complexidade sem um gatilho de negócio real que a justifique, já que o volume de acesso não varia de forma abrupta ou imprevisível o suficiente para exigir métricas além de CPU/memória.
- **Faixa de escalonamento mais ampla (ex.: min maior que 2 ou max muito acima de 4)**: descartada por não corresponder ao padrão real de crescimento da oficina, que é gradual e limitado — dimensionar para um pico muito maior do que o observado geraria custo de infraestrutura ocioso na maior parte do tempo.

## Consequências
- A infraestrutura de EKS/HPA deve ser revisada periodicamente caso o padrão de demanda mude (ex.: expansão mais acelerada para novas unidades do que a observada até aqui).
- Necessário validar, após a introdução do API Gateway na frente da aplicação, se os thresholds atuais de CPU/memória continuam adequados, já que o hop adicional do Gateway pode impactar a latência agregada percebida.
