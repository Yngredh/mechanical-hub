#!/usr/bin/env bash
#
# mock-service-orders.sh
#
# Gera trafego real contra a API do Mechanical Hub (via API Gateway) para
# popular a stack de observabilidade (RFC-0004) com metrica, log e trace
# genuinos. Nao fabrica numero nenhum: cada chamada percorre o caminho de
# codigo de verdade — Lambda Authorizer -> API Gateway -> aplicacao -> banco.
#
# Sem dependencia de ferramenta de carga (k6/Locust) nem de `jq`: apenas curl e
# utilitarios de shell presentes em qualquer Linux/macOS/WSL.
#
# ---------------------------------------------------------------------------
# O fluxo que este script percorre — e por que ele e assim
# ---------------------------------------------------------------------------
#
# O ciclo de vida de uma ordem de servico no dominio tem pre-condicoes que nao
# dao para pular:
#
#   1. `POST /service-orders/open` exige cliente, veiculo e ao menos um servico
#      ja existentes. E ele quem coloca a ordem em EM_DIAGNOSTICO e calcula o
#      orcamento a partir dos servicos.
#
#   2. AGUARDANDO_APROVACAO exige orcamento diferente de zero
#      (`submitForApproval` recusa sem ele). Por isso a ordem PRECISA nascer
#      pelo /open com servicos — `POST /service-orders`, que cria a ordem
#      "seca", nunca chega a este estagio.
#
#   3. EM_EXECUCAO nao e um status que se peca: nao existe no mapa de
#      transicoes do dominio. Ele acontece como efeito de iniciar a primeira
#      tarefa da ordem. E `startExecution` recusa a ordem se houver pendencia
#      de estoque — dai a etapa de reposicao antes do laco.
#
#   4. FINALIZADO exige TODAS as tarefas da ordem finalizadas.
#
# ---------------------------------------------------------------------------
# Uso
# ---------------------------------------------------------------------------
#
#   BASE_URL=https://xxxx.execute-api.us-east-1.amazonaws.com/production \
#   LOGIN_CPF="52998224725" \
#   LOGIN_PASSWORD="senha-do-funcionario" \
#   ./mock-service-orders.sh
#
# BASE_URL: saida `api_base_url` do Terraform do mechanical-hub-auth
#           (`terraform output api_base_url` em infra/terraform).
# LOGIN_CPF / LOGIN_PASSWORD: credenciais de um funcionario ADMINISTRATOR — o
#           perfil MECHANICAL nao tem acesso a /customers, /vehicles e /stock.
#
# Variaveis opcionais:
#   ORDERS_COUNT=15         Quantidade de ordens a percorrer.
#   SLEEP_BETWEEN_STEPS=2   Pausa entre transicoes. E o que da variacao ao
#                           painel de "tempo medio por status" — com 0, todas
#                           as duracoes ficam proximas de zero.
#   APPROVAL_RATE=80        % de ordens aprovadas pelo cliente (o resto e
#                           recusado, alimentando o funil de recusa).
#   ERROR_RATE=15           % de ordens em que uma transicao invalida e
#                           disparada de proposito.
#   BAD_LOGIN_EVERY=7       A cada N ordens, um login com senha errada.
#   STOCK_TOPUP=50          Unidades repostas por material antes do laco.
#
# ---------------------------------------------------------------------------

set -uo pipefail

BASE_URL="${BASE_URL:?defina BASE_URL, ex: https://xxxx.execute-api.us-east-1.amazonaws.com/production}"
LOGIN_CPF="${LOGIN_CPF:?defina LOGIN_CPF com o CPF de um funcionario ADMINISTRATOR}"
LOGIN_PASSWORD="${LOGIN_PASSWORD:?defina LOGIN_PASSWORD}"
ORDERS_COUNT="${ORDERS_COUNT:-15}"
SLEEP_BETWEEN_STEPS="${SLEEP_BETWEEN_STEPS:-2}"
APPROVAL_RATE="${APPROVAL_RATE:-80}"
ERROR_RATE="${ERROR_RATE:-15}"
BAD_LOGIN_EVERY="${BAD_LOGIN_EVERY:-7}"
STOCK_TOPUP="${STOCK_TOPUP:-50}"

BASE_URL="${BASE_URL%/}"

RESP_BODY=""
RESP_CODE=""
TMP_FILE="$(mktemp)"
trap 'rm -f "$TMP_FILE"' EXIT

log()  { printf '%s\n' "$*"; }
step() { printf '    %-46s %s\n' "$1" "$2"; }

# http METHOD PATH [BODY] [BEARER_TOKEN]
# Preenche RESP_CODE e RESP_BODY como efeito colateral.
http() {
  local method="$1" path="$2" body="${3:-}" token="${4:-}"
  local -a args=(-s -o "$TMP_FILE" -w '%{http_code}' -X "$method" "${BASE_URL}${path}"
                 -H 'Content-Type: application/json')
  [ -n "$token" ] && args+=(-H "Authorization: Bearer ${token}")
  [ -n "$body" ] && args+=(-d "$body")
  RESP_CODE="$(curl "${args[@]}")"
  RESP_BODY="$(cat "$TMP_FILE")"
}

# Extrai o primeiro valor de um campo string de um JSON raso. Nao e um parser
# JSON — e suficiente para os DTOs desta API, em que o campo procurado sempre
# aparece antes de qualquer objeto aninhado.
#
# O espaco opcional depois dos dois-pontos nao e capricho: o Jackson serializa
# compacto (`"id":"x"`), mas basta um proxy ou um serializador diferente no
# caminho para virar `"id": "x"` — e ai a extracao devolveria vazio sem
# reclamar de nada, e o script pareceria funcionar enquanto nao faz nada.
json_field() {
  printf '%s' "$1" \
    | grep -o "\"$2\"[[:space:]]*:[[:space:]]*\"[^\"]*\"" \
    | head -1 \
    | sed -E "s/.*:[[:space:]]*\"(.*)\"/\1/"
}

# Extrai todos os valores de "id" de uma resposta em lista.
#
# So e seguro porque nenhum DTO aninhado desta API usa a chave "id":
# ServiceMaterialResponse usa "materialId", StockSummaryResponse tambem. Se um
# DTO aninhado ganhar um "id", esta funcao passa a devolver lixo junto.
json_ids() {
  printf '%s' "$1" \
    | grep -o "\"id\"[[:space:]]*:[[:space:]]*\"[^\"]*\"" \
    | sed -E "s/.*:[[:space:]]*\"(.*)\"/\1/"
}

# Gera um CPF com digitos verificadores validos. A aplicacao valida o CPF do
# cliente (DocumentValidator), entao numero aleatorio seria recusado com 400.
gera_cpf() {
  local n=() i soma d1 d2
  for i in 0 1 2 3 4 5 6 7 8; do n[i]=$((RANDOM % 10)); done

  soma=0
  for i in 0 1 2 3 4 5 6 7 8; do soma=$(( soma + n[i] * (10 - i) )); done
  d1=$(( 11 - soma % 11 )); [ "$d1" -ge 10 ] && d1=0
  n[9]=$d1

  soma=0
  for i in 0 1 2 3 4 5 6 7 8 9; do soma=$(( soma + n[i] * (11 - i) )); done
  d2=$(( 11 - soma % 11 )); [ "$d2" -ge 10 ] && d2=0
  n[10]=$d2

  printf '%s' "${n[0]}${n[1]}${n[2]}${n[3]}${n[4]}${n[5]}${n[6]}${n[7]}${n[8]}${n[9]}${n[10]}"
}

# Placa no padrao Mercosul (LLLNLNN), o formato aceito pelo LicensePlateValidator.
gera_placa() {
  local letras=(A B C D E F G H I J K L M N O P Q R S T U V W X Y Z)
  printf '%s%s%s%d%s%02d' \
    "${letras[$((RANDOM % 26))]}" "${letras[$((RANDOM % 26))]}" "${letras[$((RANDOM % 26))]}" \
    "$((RANDOM % 10))" "${letras[$((RANDOM % 26))]}" "$((RANDOM % 100))"
}

NOMES=(Ana Bruno Carla Diego Elaine Fabio Gabriela Heitor Isabela Joao
       Karina Lucas Mariana Nelson Olivia Paulo Queila Rafael Sandra Thiago)
SOBRENOMES=(Silva Souza Oliveira Santos Pereira Costa Almeida Ribeiro Carvalho Gomes)
MARCAS=(Fiat Chevrolet Volkswagen Toyota Honda Hyundai Renault Ford)
MODELOS=(Onix Uno Gol Corolla Civic HB20 Kwid Ka)
CORES=(Prata Branco Preto Vermelho Azul Cinza)

# ── Autenticacao ─────────────────────────────────────────────────────────────

log "== Mechanical Hub — mock de observabilidade =="
log "Alvo: ${BASE_URL}"
log ""
log "[1/3] Autenticando funcionario..."

http POST "/auth/login" "{\"cpf\":\"${LOGIN_CPF}\",\"password\":\"${LOGIN_PASSWORD}\"}"
if [ "$RESP_CODE" != "200" ]; then
  log "  Falha no login (HTTP ${RESP_CODE}): ${RESP_BODY}"
  log "  Confira BASE_URL, LOGIN_CPF e LOGIN_PASSWORD."
  exit 1
fi
TOKEN="$(json_field "$RESP_BODY" accessToken)"
log "  Login OK."

# ── Reposicao de estoque ─────────────────────────────────────────────────────
#
# Sem estoque, a reserva de material cria pendencia na ordem e `startExecution`
# passa a recusar a transicao para EM_EXECUCAO — metade do ciclo de vida
# deixaria de acontecer. A base semeada ja traz materiais com quantidade zero,
# entao isto nao e precaucao teorica.

log ""
log "[2/3] Repondo estoque dos materiais..."

http GET "/materials" "" "$TOKEN"
if [ "$RESP_CODE" != "200" ]; then
  log "  Nao foi possivel listar materiais (HTTP ${RESP_CODE}): ${RESP_BODY}"
  exit 1
fi

REPOSTOS=0
FALHAS_ESTOQUE=0
while read -r material_id; do
  [ -z "$material_id" ] && continue
  http POST "/stock/entry" "{\"materialId\":\"${material_id}\",\"quantity\":${STOCK_TOPUP}}" "$TOKEN"
  case "$RESP_CODE" in
    2*) REPOSTOS=$((REPOSTOS + 1)) ;;
    # Material sem linha de estoque ainda: o dominio devolve 404. Nao e motivo
    # para parar — os demais materiais continuam servindo.
    *)  FALHAS_ESTOQUE=$((FALHAS_ESTOQUE + 1)) ;;
  esac
done <<< "$(json_ids "$RESP_BODY")"

log "  ${REPOSTOS} materiais repostos (+${STOCK_TOPUP} un.), ${FALHAS_ESTOQUE} sem linha de estoque."

# ── Servicos disponiveis ─────────────────────────────────────────────────────

log ""
log "[3/3] Descobrindo servicos cadastrados..."

http GET "/services" "" "$TOKEN"
if [ "$RESP_CODE" != "200" ]; then
  log "  Nao foi possivel listar servicos (HTTP ${RESP_CODE}): ${RESP_BODY}"
  exit 1
fi

SERVICOS=()
while read -r servico_id; do
  [ -n "$servico_id" ] && SERVICOS+=("$servico_id")
done <<< "$(json_ids "$RESP_BODY")"

if [ "${#SERVICOS[@]}" -eq 0 ]; then
  log "  Nenhum servico cadastrado. A ordem precisa de ao menos um servico para"
  log "  ter orcamento — sem isso ela trava em EM_DIAGNOSTICO."
  log "  Rode as migrations (a V16 semeia servicos) ou cadastre um em POST /services."
  exit 1
fi

log "  ${#SERVICOS[@]} servicos disponiveis."
log ""
log "== Percorrendo ${ORDERS_COUNT} ordens de servico =="

CRIADAS=0; APROVADAS=0; RECUSADAS=0; ENTREGUES=0
ERROS_FORCADOS=0; LOGINS_INVALIDOS=0; FALHAS=0

for i in $(seq 1 "$ORDERS_COUNT"); do
  nome="${NOMES[$((RANDOM % ${#NOMES[@]}))]} ${SOBRENOMES[$((RANDOM % ${#SOBRENOMES[@]}))]}"
  cpf="$(gera_cpf)"
  placa="$(gera_placa)"
  marca="${MARCAS[$((RANDOM % ${#MARCAS[@]}))]}"
  modelo="${MODELOS[$((RANDOM % ${#MODELOS[@]}))]}"
  cor="${CORES[$((RANDOM % ${#CORES[@]}))]}"
  ano=$((2015 + RANDOM % 10))
  # Round-robin entre os servicos, para que o painel nao fique concentrado num so.
  servico_id="${SERVICOS[$(( (i - 1) % ${#SERVICOS[@]} ))]}"

  log ""
  log "[${i}/${ORDERS_COUNT}] ${nome} — ${marca} ${modelo} (${placa})"

  # 1. Cliente
  http POST "/customers" \
    "{\"name\":\"${nome}\",\"documentType\":\"CPF\",\"documentNumber\":\"${cpf}\",\"telephone\":\"11987654321\",\"email\":\"cliente${i}.$$@example.com\",\"address\":\"Rua Exemplo, ${i}\"}" \
    "$TOKEN"
  if [ "$RESP_CODE" != "201" ]; then
    step "cliente" "FALHOU (HTTP ${RESP_CODE}) ${RESP_BODY}"
    FALHAS=$((FALHAS + 1)); continue
  fi
  cliente_id="$(json_field "$RESP_BODY" id)"
  step "cliente criado" "$cliente_id"

  # 2. Veiculo
  http POST "/vehicles?customer_id=${cliente_id}" \
    "{\"licensePlate\":\"${placa}\",\"brand\":\"${marca}\",\"model\":\"${modelo}\",\"year\":${ano},\"color\":\"${cor}\"}" \
    "$TOKEN"
  if [ "$RESP_CODE" != "201" ]; then
    step "veiculo" "FALHOU (HTTP ${RESP_CODE}) ${RESP_BODY}"
    FALHAS=$((FALHAS + 1)); continue
  fi
  veiculo_id="$(json_field "$RESP_BODY" id)"
  step "veiculo criado" "$veiculo_id"

  # 3. Abertura da ordem — ja nasce em EM_DIAGNOSTICO e com orcamento calculado
  #    a partir dos servicos. E o unico caminho que leva a ordem adiante.
  http POST "/service-orders/open" \
    "{\"customerId\":\"${cliente_id}\",\"vehicleId\":\"${veiculo_id}\",\"serviceIds\":[\"${servico_id}\"],\"requestDescription\":\"Revisao gerada por script de mock (#${i})\"}" \
    "$TOKEN"
  if [ "$RESP_CODE" != "201" ]; then
    step "abertura da OS" "FALHOU (HTTP ${RESP_CODE}) ${RESP_BODY}"
    FALHAS=$((FALHAS + 1)); continue
  fi
  ordem_id="$(json_field "$RESP_BODY" id)"
  ordem_numero="$(json_field "$RESP_BODY" orderNumber)"
  CRIADAS=$((CRIADAS + 1))
  step "OS aberta (EM_DIAGNOSTICO)" "$ordem_numero"

  sleep "$SLEEP_BETWEEN_STEPS"

  # Erro proposital: EM_EXECUCAO nao existe no mapa de transicoes do dominio, e
  # o pedido e recusado. E o cenario que alimenta o painel de transicoes
  # recusadas e o alerta MechanicalHubFalhaProcessamentoOS — sem ele, esses
  # paineis ficam sempre zerados e nao ha o que mostrar na demonstracao.
  if [ $((RANDOM % 100)) -lt "$ERROR_RATE" ]; then
    http PATCH "/service-orders/${ordem_id}/status" '{"status":"EM_EXECUCAO"}' "$TOKEN"
    step "transicao invalida (proposital)" "HTTP ${RESP_CODE}"
    ERROS_FORCADOS=$((ERROS_FORCADOS + 1))
  fi

  # 4. Envia para aprovacao do cliente.
  http PATCH "/service-orders/${ordem_id}/status" '{"status":"AGUARDANDO_APROVACAO"}' "$TOKEN"
  if [ "$RESP_CODE" != "200" ]; then
    step "aguardando aprovacao" "FALHOU (HTTP ${RESP_CODE}) ${RESP_BODY}"
    FALHAS=$((FALHAS + 1)); continue
  fi
  step "aguardando aprovacao" "OK"

  sleep "$SLEEP_BETWEEN_STEPS"

  # 5. Decisao do cliente final — rota publica, sem token. E assim que o fluxo
  #    real funciona: o cliente nunca autentica na plataforma (RFC-0003).
  if [ $((RANDOM % 100)) -lt "$APPROVAL_RATE" ]; then
    http POST "/mechanical-hub/service-orders/${ordem_id}/approve" ""
    if [ "$RESP_CODE" != "204" ]; then
      step "aprovacao do cliente" "FALHOU (HTTP ${RESP_CODE}) ${RESP_BODY}"
      FALHAS=$((FALHAS + 1)); continue
    fi
    APROVADAS=$((APROVADAS + 1))
    step "aprovada pelo cliente" "OK"

    sleep "$SLEEP_BETWEEN_STEPS"

    # 6. Iniciar a tarefa e o que leva a ordem a EM_EXECUCAO.
    #
    #    ATENCAO: o identificador na URL e o do SERVICO, nao o da OrderTask.
    #    `ServiceOrder.findTask` procura por `orderTask.serviceData.id` — usar o
    #    id da tarefa devolve 400 "Tarefa nao encontrada".
    http PATCH "/service-orders/${ordem_id}/services/${servico_id}/status" \
      '{"status":"INICIADO"}' "$TOKEN"
    if [ "$RESP_CODE" != "204" ]; then
      step "iniciar tarefa (-> EM_EXECUCAO)" "FALHOU (HTTP ${RESP_CODE}) ${RESP_BODY}"
      FALHAS=$((FALHAS + 1)); continue
    fi
    step "tarefa iniciada (EM_EXECUCAO)" "OK"

    sleep "$SLEEP_BETWEEN_STEPS"

    http PATCH "/service-orders/${ordem_id}/services/${servico_id}/status" \
      '{"status":"FINALIZADO"}' "$TOKEN"
    step "tarefa finalizada" "HTTP ${RESP_CODE}"

    sleep "$SLEEP_BETWEEN_STEPS"

    # 7. FINALIZADO exige todas as tarefas finalizadas — garantido acima.
    http PATCH "/service-orders/${ordem_id}/status" '{"status":"FINALIZADO"}' "$TOKEN"
    step "OS finalizada" "HTTP ${RESP_CODE}"

    sleep "$SLEEP_BETWEEN_STEPS"

    http PATCH "/service-orders/${ordem_id}/status" '{"status":"ENTREGUE"}' "$TOKEN"
    if [ "$RESP_CODE" = "200" ]; then
      ENTREGUES=$((ENTREGUES + 1))
      step "OS entregue" "ciclo completo"
    else
      step "OS entregue" "FALHOU (HTTP ${RESP_CODE}) ${RESP_BODY}"
      FALHAS=$((FALHAS + 1))
    fi
  else
    http POST "/mechanical-hub/service-orders/${ordem_id}/reject" ""
    RECUSADAS=$((RECUSADAS + 1))
    step "recusada pelo cliente" "HTTP ${RESP_CODE}"
  fi

  # 8. Consulta publica por numero — o outro caminho sem autenticacao.
  http GET "/mechanical-hub/service-orders/${ordem_numero}" ""
  step "consulta publica por numero" "HTTP ${RESP_CODE}"

  # 9. Tentativa de login invalida, de tempos em tempos.
  if [ $((i % BAD_LOGIN_EVERY)) -eq 0 ]; then
    http POST "/auth/login" "{\"cpf\":\"${LOGIN_CPF}\",\"password\":\"senha-errada-de-proposito\"}"
    step "login invalido (proposital)" "HTTP ${RESP_CODE}"
    LOGINS_INVALIDOS=$((LOGINS_INVALIDOS + 1))
  fi
done

log ""
log "== Resumo =="
log "Ordens abertas:            ${CRIADAS}/${ORDERS_COUNT}"
log "Aprovadas pelo cliente:    ${APROVADAS}"
log "Recusadas pelo cliente:    ${RECUSADAS}"
log "Ciclo completo (entregue): ${ENTREGUES}"
log "Transicoes invalidas:      ${ERROS_FORCADOS}  (propositais)"
log "Logins invalidos:          ${LOGINS_INVALIDOS}  (propositais)"
log "Falhas inesperadas:        ${FALHAS}"

if [ "$FALHAS" -gt 0 ]; then
  log ""
  log "Houve falhas nao planejadas. As mensagens de cada uma estao acima, junto"
  log "da etapa em que aconteceram."
  exit 1
fi
