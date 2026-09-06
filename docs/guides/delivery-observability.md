# Delivery observability — GitHub pipeline events

Indexes **GitHub Actions check conclusions** after each `ci` workflow completes.
This is delivery telemetry for pipelines (PR/push), not application OpenTelemetry.

## Event shape (`kind: delivery.ci`)

| Field | Notes |
| --- | --- |
| `check` | Job/check name (`api`, `security`, `commit-attribution`, …) |
| `conclusion` | `success`, `failure`, `skipped`, `cancelled`, … |
| `baseBranch` | PR base (`sandbox`, `main`) or fallback branch |
| `headBranch` | Branch that ran CI |
| `pr`, `runId`, `commit`, `url` | Human-readable detail |

## Ingest

**Local** (after PR checks finish):

```bash
node scripts/record-delivery-ci.mjs --pr 13
```

Appends to `.cache/delivery/events.jsonl` (gitignored).

**GitHub Actions:** workflow `delivery-observability` on `ci` `workflow_run` completed → artifact `delivery-ci-events-<run_id>` (30-day retention).

Concurrency: `delivery-ci-<branch>` with `cancel-in-progress: true` so burst pushes keep only the latest ingest per branch.

## What this is not

- Not a second CI / not a merge gate (unless you later make it required)
- Not app metrics (Micrometer / OTel) — see ADR-005
- Does not install Prometheus/Grafana by default
