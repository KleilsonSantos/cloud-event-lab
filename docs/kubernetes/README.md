# Kubernetes (kind)

## Classification

| Item | Type |
| --- | --- |
| kind cluster | **OPEN SOURCE ALTERNATIVE** / local K8s — **APPROX** vs EKS/AKS/GKE |
| Manifests under `deploy/kind/` | Local demo only — not managed cloud |

Do **not** claim EKS/AKS/GKE parity.

## Goal

Demonstrate Deployment, Service, ConfigMap, Secret, liveness/readiness probes, and resource requests/limits for the API + Postgres.

## Prerequisites

```bash
kind version
kubectl version --client
docker version
./scripts/preflight.sh
```

## Bootstrap

```bash
./scripts/kind-up.sh
```

This will:

1. Create (or reuse) kind cluster `cloud-event-lab` (host `8080` → NodePort `30080`)
2. Build `cloud-event-lab-api:local` and `kind load` it
3. Apply kustomize under `deploy/kind/`
4. Wait for `postgres` and `api` rollouts

Smoke:

```bash
curl -s http://localhost:8080/actuator/health
curl -s -u lab:lab-change-me http://localhost:8080/api/lab/status
```

Tear down:

```bash
kind delete cluster --name cloud-event-lab
```

## Layout

| File | Role |
| --- | --- |
| `cluster.yaml` | kind cluster + port map |
| `namespace.yaml` | `cloud-event-lab` |
| `api-config.yaml` | ConfigMap + Secret (lab defaults) |
| `postgres.yaml` | Postgres Deployment/Service |
| `api.yaml` | API Deployment/Service (NodePort) |
| `kustomization.yaml` | apply-all |

## Observability note

Tracing sampling defaults to `0` in-cluster. For OTel export, run compose profile `otel` and point the API at the collector (see `docs/observability/`). In-cluster collector sidecar is optional later — not required for Phase 4 kind smoke.
