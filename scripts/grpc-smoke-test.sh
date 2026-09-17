#!/usr/bin/env bash
set -euo pipefail

GRPCURL_IMAGE="${GRPCURL_IMAGE:-fullstorydev/grpcurl:latest}"
PATIENT_ID="${PATIENT_ID:-123}"
GRPC_DOCKER_ARGS=()

if [[ -z "${GRPC_TARGET:-}" ]]; then
  ENTERPRISE_CONTAINER="$(docker compose ps -q enterprise-service 2>/dev/null || true)"
  if [[ -n "$ENTERPRISE_CONTAINER" ]]; then
    COMPOSE_NETWORK="$(docker inspect \
      --format '{{range $name, $_ := .NetworkSettings.Networks}}{{println $name}}{{end}}' \
      "$ENTERPRISE_CONTAINER" 2>/dev/null | head -n 1)"
    if [[ -n "$COMPOSE_NETWORK" ]]; then
      GRPC_TARGET="enterprise-service:9090"
      GRPC_DOCKER_ARGS=(--network "$COMPOSE_NETWORK")
    fi
  fi
fi

if [[ -z "${GRPC_TARGET:-}" ]]; then
  GRPC_TARGET="localhost:9090"
  GRPC_DOCKER_ARGS=(--network host)
fi

grpcurl() {
  docker run --rm "${GRPC_DOCKER_ARGS[@]}" "$GRPCURL_IMAGE" "$@"
}

echo "Checking gRPC reflection at ${GRPC_TARGET}..."
grpcurl -plaintext -max-time 10 "$GRPC_TARGET" list

echo "Checking gRPC health..."
grpcurl -plaintext -max-time 10 \
  -d '{"service":""}' \
  "$GRPC_TARGET" grpc.health.v1.Health/Check

echo "Calling EligibilityGrpcService/CheckEligibility for patient ${PATIENT_ID}..."
grpcurl -plaintext -max-time 10 \
  -d "{\"patient_id\":\"${PATIENT_ID}\"}" \
  "$GRPC_TARGET" EligibilityGrpcService/CheckEligibility

echo "gRPC smoke test passed."
