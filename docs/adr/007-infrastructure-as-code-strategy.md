# ADR-007: Infrastructure as Code strategy

- **Status:** Accepted  
- **Date:** 2026-09-06  

## Context

Multi-cloud IaC com semânticas diferentes por provider.

## Decision

**OpenTofu** (ou Terraform OSS) com árvores separadas `infra/aws`, `infra/azure`, `infra/gcp`, `infra/kubernetes`. Modules só para pedaços realmente compartilháveis (tags, naming). Não um único módulo “multi-cloud” forçado.

## Alternatives

1. Só CDK/Bicep/Cloud Deployment Manager — rejeitado como **única** ferramenta (lock-in de demonstração).  
2. Só scripts shell — rejeitado (não cobre o escopo IaC do lab).

## Consequences

+ Clareza por cloud.  
− Alguma duplicação intencional entre pastas.
