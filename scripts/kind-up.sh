#!/usr/bin/env bash
# Bootstrap local kind cluster for cloud-event-lab (APPROX vs managed K8s).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CLUSTER_NAME="cloud-event-lab"
IMAGE="cloud-event-lab-api:local"

echo "==> preflight (ports / docker)"
if [[ -x "$ROOT/scripts/preflight.sh" ]]; then
  "$ROOT/scripts/preflight.sh" || true
fi

command -v kind >/dev/null || { echo "kind not found — install https://kind.sigs.k8s.io/"; exit 1; }
command -v kubectl >/dev/null || { echo "kubectl not found"; exit 1; }
command -v docker >/dev/null || { echo "docker not found"; exit 1; }
command -v mvn >/dev/null || { echo "mvn not found"; exit 1; }

if ! kind get clusters 2>/dev/null | grep -qx "$CLUSTER_NAME"; then
  echo "==> create kind cluster $CLUSTER_NAME"
  kind create cluster --config "$ROOT/deploy/kind/cluster.yaml"
else
  echo "==> kind cluster $CLUSTER_NAME already exists"
fi

echo "==> build API jar + image"
(cd "$ROOT/apps/api" && mvn -B -DskipTests package)
docker build -t "$IMAGE" "$ROOT/apps/api"

echo "==> load image into kind"
kind load docker-image "$IMAGE" --name "$CLUSTER_NAME"

echo "==> apply manifests"
kubectl apply -k "$ROOT/deploy/kind"

echo "==> wait for rollout"
kubectl -n cloud-event-lab rollout status deployment/postgres --timeout=120s
kubectl -n cloud-event-lab rollout status deployment/api --timeout=180s

echo
echo "API NodePort → http://localhost:8080 (kind extraPortMappings)"
echo "Health: curl -s http://localhost:8080/actuator/health"
echo "Done. Tear down: kind delete cluster --name $CLUSTER_NAME"
