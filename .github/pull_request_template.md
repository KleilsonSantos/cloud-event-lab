## Summary

<!-- What changed and why -->

## Issue

- Refs #<N> <!-- recommended for work PRs → sandbox -->

<!-- On promote sandbox → main, prefer: Closes #<N> -->

## Change type

- [ ] feat
- [ ] fix
- [ ] docs
- [ ] refactor
- [ ] ci
- [ ] chore
- [ ] test
- [ ] security

## Multi-cloud honesty

- [ ] Does **not** claim AWS/Azure/GCP parity beyond what adapters + tests prove
- [ ] Emulators labeled correctly (LOCAL EMULATOR / APPROX / REAL CLOUD) if docs touch them
- [ ] No secrets committed (`.env` stays local)

## Checklist

- [ ] Branch from `sandbox` (`feature/*` or `fix/*`)
- [ ] Target: work → `sandbox`; promote → `main`
- [ ] Docs/ADR updated if architecture or ops changed
- [ ] CI-relevant paths still build (`apps/api` tests / `apps/web` build)

## Test plan

<!-- How to validate locally or in CI -->
