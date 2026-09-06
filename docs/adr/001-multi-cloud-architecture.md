# ADR-001: Multi-cloud laboratory architecture

- **Status:** Accepted  
- **Date:** 2026-09-06  

## Context

O lab precisa demonstrar AWS, Azure e GCP na mesma aplicação, sem três produtos desconexos e sem fingir paridade total.

## Decision

Uma aplicação (Cloud Event Platform) com **profiles** e **adapters** para storage/messaging; labs locais com emuladores oficiais quando existirem; PostgreSQL como store portátil de metadados.

## Alternatives

1. Três apps cloud-specific — rejeitado (não prova portabilidade).  
2. Só Terraform multi-cloud sem app — rejeitado (fraco para Full Stack).  
3. Microservices por cloud — rejeitado (complexidade artificial).

## Consequences

+ Separação clara de domínio vs infra.  
− Emuladores ≠ produção; docs devem marcar limitações.  
− LocalStack auth/Hobby obrigatório a verificar na data de uso.
