# ADR-006: Security strategy

- **Status:** Accepted  
- **Date:** 2026-09-06  

## Context

Lab precisa de AppSec/DevSecOps baseline (SCA/SAST, secrets, least privilege) sem secrets no Git.

## Decision

- Validação de input na API; auth baseline (API key ou JWT local no MVP).  
- Secrets só env / secret store; `.env` gitignored.  
- CI: Gitleaks + Trivy + CodeQL (+ Semgrep opcional); Dependency scan no build Java (endurecer na Phase 5).  
- Containers: non-root quando viável; scans de imagem.

## Alternatives

1. Sem auth no MVP — rejeitado (postura insegura para um lab público).  
2. Entra ID/Cognito completo no dia 1 — adiado (complexidade).

## Consequences

+ Postura segura demonstrável.  
− IdP cloud completo fica Phase posterior.
