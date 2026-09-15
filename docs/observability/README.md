# Observability

Strategy: OpenTelemetry as the portable layer ([ADR-005](../adr/005-observability-strategy.md)). Cloud backends (CloudWatch / Application Insights / Cloud Trace) = optional **REAL_CLOUD**, not this lab’s default.

## Classification

| Item | Type |
| --- | --- |
| Micrometer Tracing → OpenTelemetry bridge | Portable instrumentation |
| OTLP exporter | Standard protocol |
| `otel/opentelemetry-collector-contrib` (compose profile `otel`) | **OPEN SOURCE** / local pipeline |
| debug exporter on collector | Local visibility only — not a cloud APM |

## Compose profile `otel`

```bash
./scripts/preflight.sh
cd deploy/compose
docker compose --profile otel up -d
```

Collector listens on:

| Port | Protocol |
| --- | --- |
| `4317` | OTLP gRPC |
| `4318` | OTLP HTTP |

## API profile

```bash
cd apps/api
mvn spring-boot:run -Dspring-boot.run.profiles=local,otel
```

`application-otel.yml` sets sampling to `1.0` and exports HTTP OTLP to  
`${OTEL_EXPORTER_OTLP_ENDPOINT:http://localhost:4318/v1/traces}`.

Without profile `otel`, sampling stays `0.0` so everyday local runs do not spam a missing collector.

## Honesty

- Traces in the collector debug log prove the portable pipeline.
- Do **not** claim CloudWatch / App Insights / Cloud Logging parity until a REAL_CLOUD exporter is wired and documented.
