# Runbook: PR sandbox flow (v1)

Cadence: `next` = propose · `ok` = implement into sandbox · `green` = promote sandbox → main.

1. After `ok`: branch from `sandbox` (`feature/*` or `fix/*`).
2. Open PR → `sandbox` using `.github/pull_request_template.md` (`Refs #N`).
3. After `green`: open PR `sandbox` → `main` (`Closes #N`).
4. Conventional commits by domain scope; commit only when authorized.
5. Author/committer: `Kleilson Santos <kdsdesign1@gmail.com>` only — never `Co-authored-by: Cursor`.
6. Dependabot PRs land on `sandbox` — promote deliberately on `green`.

SSOT: `AGENTS.md` (Owner Cadence), `docs/guides/git-workflow.md`, `SECURITY.md`, `.githooks/commit-msg`.
