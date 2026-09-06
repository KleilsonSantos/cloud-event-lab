# ADR-004: Kubernetes strategy

- **Status:** Accepted  
- **Date:** 2026-09-06  

## Context

Precisamos demonstrar workloads K8s sem cluster gerenciado pago.

## Decision

**kind** para cluster local; manifests com Deployment, Service, ConfigMap, Secret, liveness/readiness, resources. Autoscaling/Ingress só se agregarem valor demonstrável.

Não usar LocalStack-on-K8s Enterprise como requisito do lab.

## Alternatives

1. k3d / minikube — válidos; kind escolhido por simplicidade e docs CNCF-friendly.  
2. Só Compose — insuficiente para o escopo Kubernetes deste lab.

## Consequences

+ Demo K8s local.  
− Não prova EKS/AKS/GKE managed control plane.
