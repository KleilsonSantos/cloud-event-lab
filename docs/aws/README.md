# AWS lab (LocalStack)

## Classification

| Item | Type |
| --- | --- |
| LocalStack S3 / SQS / SNS / DynamoDB | **LOCAL EMULATOR** |
| Real AWS account | **REAL CLOUD** |
| Parity with production AWS | **Never assumed** |

## Official references

- Services: https://docs.localstack.cloud/aws/services/
- Pricing / Hobby: https://localstack.cloud/pricing (Hobby free **non-commercial** — revalidate on use date)

## Compose

```bash
./scripts/preflight.sh
cd deploy/compose
docker compose --profile aws up -d
```

## Auth / endpoint (LocalStack)

| Knob | Lab default | Notes |
| --- | --- | --- |
| Endpoint | `http://localhost:4566` (`lab.aws.endpoint`) | Path-style S3; SQS same endpoint |
| Region | `us-east-1` | Arbitrary for LocalStack |
| Credentials | `test` / `test` | Static for emulator; `lab.aws.use-static-credentials=false` only for REAL_CLOUD |
| S3 bucket | `cloud-event-lab` | Created on adapter startup if missing |
| SQS queue | `cloud-events` (app constant) | Created on first publish/subscribe |
| Hobby token | `LOCALSTACK_AUTH_TOKEN` in env (optional) | **never commit** |

## Messaging honesty (SQS)

| Behavior | Classification |
| --- | --- |
| Publish / long-poll receive / delete on success | Exercised against LocalStack |
| Visibility-timeout retry when handler fails | Supported (message not deleted) |
| SQS redrive policy → DLQ | **APPROX** — not wired in this lab; document only |

## App profile

```bash
cd apps/api
mvn spring-boot:run -Dspring-boot.run.profiles=aws
```

`lab.cloud.provider=aws` selects S3 + SQS. Profile `local` is no longer required for the worker.

## Limitações conhecidas

- Auth/account obrigatória no modelo LocalStack atual (2026).
- Não deployar LocalStack como “produção AWS” em ambiente público.
