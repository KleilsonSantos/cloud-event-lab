# Security

- No secrets in Git (`.env` gitignored).
- API: HTTP Basic for local lab only (not production IdP).
- CI (see `.github/workflows/ci.yml`): Gitleaks, Trivy fs, CodeQL, ShellCheck; soft `npm audit` on web.
- Dependabot version PRs → `sandbox` (see `SECURITY.md`).
- ADR-006 (AppSec baseline) · ADR-008 (AI-assisted boundaries).
