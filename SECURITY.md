# Security Policy

## Supported versions

| Version           | Supported |
| ----------------- | --------- |
| 0.x (pre-release) | ✅        |

## Reporting a vulnerability

**Do not open public issues for security vulnerabilities.**

Send details to: **kleilson@icloud.com**

Include description, reproduction steps, impact, and suggested mitigation (if any).

We aim to respond within 5 business days.

## Repository security posture

| Feature                         | Posture               | Notes                                              |
| ------------------------------- | --------------------- | -------------------------------------------------- |
| Dependabot **alerts**           | **On** (recommended)  | CVE visibility in Security tab                     |
| Dependabot **security updates** | **Off** (intentional) | Auto-PRs target default branch; version bumps → `sandbox` |
| Secret scanning + push protect  | On (recommended)      | Blocks accidental secret commits                   |
| Code scanning (CodeQL)          | On via CI             | Java + JavaScript/TypeScript                       |
| Gitleaks                        | On via CI             | Secrets in git history / diff                      |
| Trivy (fs)                      | On via CI             | CRITICAL/HIGH, ignore-unfixed                      |
| npm audit                       | Soft gate in CI       | `continue-on-error` until Phase 5 hardens          |

Owner checklist: repo **Settings → Code security** — keep alerts on; leave security-update PRs off unless you accept `main`-targeted Dependabot merges.

AppSec baseline for the lab itself: [ADR-006](docs/adr/006-security-strategy.md), [docs/security](docs/security/README.md).
