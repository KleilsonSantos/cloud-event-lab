# Kubernetes (kind)

## Classification

| Item | Type |
| --- | --- |
| kind cluster | **OPEN SOURCE ALTERNATIVE** / local K8s — **APPROX** vs EKS/AKS/GKE |

## Goal

Demonstrate Deployment, Service, ConfigMap, Secret, liveness/readiness, resource limits.

Manifests will live under `deploy/kind/` (Phase 4). Do not claim managed cloud K8s until REAL_CLOUD.

## Prerequisite check

```bash
kind version
kubectl version --client
```
