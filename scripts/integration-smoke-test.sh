#!/usr/bin/env bash
set -uo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8081}"
POOL_PROFILE="${POOL_PROFILE:-hikari}"
LOG_DIR="${LOG_DIR:-logs}"
LOG_FILE="${LOG_DIR}/integration-smoke-$(date +%Y%m%d-%H%M%S).log"
PDF_FILE="${LOG_DIR}/eligibility-$(date +%Y%m%d-%H%M%S).pdf"
RUN_ID="$(date +%Y%m%d%H%M%S)-$$"
MESSAGE_ID=""
TOKEN=""
FAILED=0

if [[ -t 1 ]]; then
  GREEN=$'\033[0;32m'
  RED=$'\033[0;31m'
  YELLOW=$'\033[1;33m'
  RESET=$'\033[0m'
else
  GREEN=""
  RED=""
  YELLOW=""
  RESET=""
fi

mkdir -p "$LOG_DIR"
touch "$LOG_FILE"

log() {
  printf '[%s] %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$*" | tee -a "$LOG_FILE"
}

ok() {
  printf '%b[ OK ]%b %s\n' "$GREEN" "$RESET" "$1"
  printf '[ OK ] %s\n' "$1" >> "$LOG_FILE"
}

fail() {
  printf '%b[FAIL]%b %s\n' "$RED" "$RESET" "$1"
  printf '[FAIL] %s\n' "$1" >> "$LOG_FILE"
  FAILED=1
}

skip() {
  printf '%b[SKIP]%b %s\n' "$YELLOW" "$RESET" "$1"
  printf '[SKIP] %s\n' "$1" >> "$LOG_FILE"
}

run_check() {
  local name="$1"
  shift
  if "$@" >> "$LOG_FILE" 2>&1; then
    ok "$name"
  else
    fail "$name (see $LOG_FILE)"
  fi
}

http_check() {
  local name="$1"
  local url="$2"
  shift 2
  if curl -fsS "$@" "$url" >> "$LOG_FILE" 2>&1; then
    ok "$name"
  else
    fail "$name (see $LOG_FILE)"
  fi
}

log "Starting enterprise integration smoke test"
log "Datasource profile: $POOL_PROFILE"
log "Log file: $LOG_FILE"

run_check "Compose services are running" docker compose ps
http_check "Application health" "$BASE_URL/actuator/health"
http_check "Prometheus metrics" "$BASE_URL/actuator/prometheus"

if TOKEN_RESPONSE="$(curl -fsS -X POST \
  "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=admin-cli" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" 2>>"$LOG_FILE")"; then
  TOKEN="$(printf '%s' "$TOKEN_RESPONSE" | jq -r '.access_token // empty' 2>>"$LOG_FILE")"
fi

if [[ -n "$TOKEN" ]]; then
  ok "Keycloak OAuth2 token"
else
  fail "Keycloak OAuth2 token (see $LOG_FILE)"
fi

if [[ -n "$TOKEN" ]]; then
  AUTH_HEADER="Authorization: Bearer $TOKEN"

  http_check "Keycloak-secured API" "$BASE_URL/api/messages" \
    -H "$AUTH_HEADER"
  http_check "Drools rule evaluation" "$BASE_URL/api/rules/evaluate?age=42" \
    -H "$AUTH_HEADER"
  http_check "FHIR patient creation" \
    "$BASE_URL/api/fhir/patient?familyName=Smoke${RUN_ID}&givenName=Integration" \
    -H "$AUTH_HEADER"

  MESSAGE_RESPONSE="$(curl -fsS -X POST "$BASE_URL/api/messages" \
    -H "$AUTH_HEADER" \
    -H "Content-Type: application/json" \
    --data "{\"message\":\"integration-smoke-${RUN_ID}\",\"secretText\":\"smoke-${RUN_ID}\",\"secretDate\":\"2026-01-01T00:00:00Z\",\"secretBytes\":\"c21va2U=\"}" \
    2>>"$LOG_FILE")" || MESSAGE_RESPONSE=""
  printf '%s\n' "$MESSAGE_RESPONSE" >> "$LOG_FILE"
  MESSAGE_ID="$(printf '%s' "$MESSAGE_RESPONSE" | jq -r '.id // empty' 2>>"$LOG_FILE")"
  if [[ -n "$MESSAGE_ID" ]]; then
    ok "Create integration message (id ${MESSAGE_ID})"
  else
    fail "Create integration message (see $LOG_FILE)"
  fi

  if [[ -n "$MESSAGE_ID" ]]; then
    http_check "Update integration message" \
      "$BASE_URL/api/messages/$MESSAGE_ID" \
      -X PUT \
      -H "$AUTH_HEADER" \
      -H "Content-Type: application/json" \
      --data "{\"message\":\"integration-smoke-updated-${RUN_ID}\"}"
    http_check "Search integration messages" \
      "$BASE_URL/api/messages/search?text=integration-smoke-updated-${RUN_ID}" \
      -H "$AUTH_HEADER"
  fi

  http_check "JasperReports PDF endpoint" \
    "$BASE_URL/api/reports/eligibility?patientId=smoke-test" \
    -H "$AUTH_HEADER" \
    -o "$PDF_FILE"

  if file "$PDF_FILE" >> "$LOG_FILE" 2>&1 && grep -aq "%PDF" "$PDF_FILE"; then
    ok "Generated file is a PDF"
  else
    fail "Generated file is a PDF (see $LOG_FILE)"
  fi

  EVENT_RESPONSE="$(curl -fsS -X POST "$BASE_URL/api/integrations/events" \
    -H "$AUTH_HEADER" \
    -H "Content-Type: application/json" \
    --data "{\"message\":\"broker-smoke-${RUN_ID}\"}" \
    2>>"$LOG_FILE")" || EVENT_RESPONSE=""
  printf '%s\n' "$EVENT_RESPONSE" >> "$LOG_FILE"
  if printf '%s' "$EVENT_RESPONSE" | jq -e '.rabbitMq == true and .kafka == true' \
      >>"$LOG_FILE" 2>&1; then
    ok "RabbitMQ and Kafka publish endpoint"
  else
    fail "RabbitMQ and Kafka publish endpoint (see $LOG_FILE)"
  fi
  sleep 2
  BROKER_LOGS="$(docker compose logs --since=10s enterprise-service 2>>"$LOG_FILE" || true)"
  printf '%s\n' "$BROKER_LOGS" >> "$LOG_FILE"
  if printf '%s' "$BROKER_LOGS" | grep -q "RabbitMQ event received: broker-smoke-${RUN_ID}" &&
     printf '%s' "$BROKER_LOGS" | grep -q "Kafka event received: broker-smoke-${RUN_ID}"; then
    ok "RabbitMQ and Kafka consumers received the event"
  else
    fail "RabbitMQ and Kafka consumers received the event (see $LOG_FILE)"
  fi
else
  skip "Authenticated API, message CRUD, JasperReports, and broker endpoint"
fi

http_check "LDAP authentication" "$BASE_URL/api/messages" \
  -u demo-user:demo-password
http_check "Eureka registration" \
  "http://localhost:8761/eureka/apps/enterprise-service"
http_check "Config Server profile" \
  "http://localhost:8888/enterprise-service/hikari"
http_check "OpenAPI JSON" "$BASE_URL/v3/api-docs"
http_check "SOAP WSDL" "$BASE_URL/services/eligibility?wsdl"
http_check "SOAP eligibility operation" "$BASE_URL/services/eligibility" \
  -X POST \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H 'SOAPAction: ""' \
  --data "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.markodojkic.dev/\"><soapenv:Header/><soapenv:Body><soap:checkEligibility><arg0>${RUN_ID}</arg0></soap:checkEligibility></soapenv:Body></soapenv:Envelope>"
run_check "Native gRPC reflection, health, and RPC" \
  bash ./scripts/grpc-smoke-test.sh

log "Completed. Detailed output: $LOG_FILE"
if [[ "$FAILED" -eq 0 ]]; then
  printf '%b\nALL INTEGRATION CHECKS PASSED%b\n' "$GREEN" "$RESET"
else
  printf '%b\nONE OR MORE INTEGRATION CHECKS FAILED%b\n' "$RED" "$RESET"
fi

exit "$FAILED"
