#!/usr/bin/env bash
set -euo pipefail

GRPC_TARGET="${GRPC_TARGET:-localhost:9090}"
GRPCURL_IMAGE="${GRPCURL_IMAGE:-fullstorydev/grpcurl:latest}"
PATIENT_ID="${PATIENT_ID:-123}"

grpcurl() {
  docker run --rm --network host "$GRPCURL_IMAGE" "$@"
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
