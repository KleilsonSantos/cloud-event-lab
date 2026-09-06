# ADR-008: AI-assisted development for this lab

- **Status:** Accepted  
- **Date:** 2026-09-06  

## Context

Coding agents (IDE chat, GitHub agents, future MCP) will edit this repository. The lab needs clear boundaries so AI help improves delivery without inventing product scope, fake multi-cloud claims, or couplings to other repos.

## Decision

1. **`AGENTS.md`** is the agent entry point (source order, hard constraints, **Owner Cadence** `next` / `ok` / `green`).  
2. **GitHub agent personas** live under `.github/agents/` for review/planning helpers scoped to this lab.  
3. **Optional runbooks** live under `docs/prompts/` (versioned markdown); they are docs-as-code, not runtime product SSOT.  
4. AI must follow ADRs 001–007 for architecture; AI must not add an “AI product” surface to the Event Platform unless a future ADR explicitly decides that.  
5. External project references or copy-paste identity from other repos are **opt-in by human request only**.  
6. Cadence: `next` = proposal only; `ok` / `prossegue` = implement into sandbox flow; `green` = promote sandbox → main.

## Alternatives

1. No agent docs — rejeitado (agents invent scope and claim parity).  
2. Full AI governance runtime inside this lab — rejeitado (fora do domínio Cloud Event Platform).  
3. Prompt-only instructions without ADRs — rejeitado (não rastreável).

## Consequences

+ Agents have a stable map of truth for this repo.  
+ CI/git/security conventions stay human-owned SSOT.  
− Agent personas and prompts need occasional upkeep when phases advance.
