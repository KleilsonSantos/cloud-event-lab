# ADR-005: Observability strategy

- **Status:** Accepted  
- **Date:** 2026-09-06  

## Context

Backends CloudWatch / App Insights / Cloud Logging diferem; o lab precisa de telemetria portátil.

## Decision

Instrumentar a aplicação com **OpenTelemetry** (traces/metrics/logs). Export local para collector. Integrações cloud-native de backend = opcional REAL_CLOUD, não MVP.

## Alternatives

1. Um agent por cloud — rejeitado (não portátil).  
2. Só logging stdout — insuficiente para o escopo de observability deste lab.

## Consequences

+ Uma pipeline de telemetria.  
− Dashboards cloud-native ficam como extensão documentada.
