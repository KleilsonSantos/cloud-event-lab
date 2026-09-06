# ADR-002: Cloud abstraction strategy

- **Status:** Accepted  
- **Date:** 2026-09-06  

## Context

SDKs AWS/Azure/GCP acoplados ao domínio destroem portabilidade e testes.

## Decision

Ports apenas para **ObjectStorage**, **MessageBus** e **SecretStore**. Persistência de eventos em JPA/Postgres sem port cloud. Sem facade `CloudProvider` genérica.

## Alternatives

1. Abstrair tudo incluindo DB — rejeitado (over-engineering).  
2. Zero abstração (ifs por profile no use case) — rejeitado (domínio poluído).

## Consequences

+ Testes de contrato por port.  
− Features cloud-only (ex.: S3 Select) ficam fora do port ou em adapter específico documentado.
