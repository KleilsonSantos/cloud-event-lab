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
# Check ports first
./scripts/preflight.sh
# or: lsof -nP -iTCP:4566 -sTCP:LISTEN

cd deploy/compose
docker compose --profile aws up -d
```

## Auth / endpoint (LocalStack)

| Knob | Lab default | Notes |
| --- | --- | --- |
| Endpoint | `http://localhost:4566` (`lab.aws.endpoint`) | Path-style S3 forced in adapter |
| Region | `us-east-1` | Arbitrary for LocalStack |
| Credentials | `test` / `test` (`lab.aws.access-key-id` / `secret-access-key`) | Static creds for emulator; set `lab.aws.use-static-credentials=false` only for REAL_CLOUD |
| Bucket | `cloud-event-lab` | Created on adapter startup if missing |
| Hobby token | `LOCALSTACK_AUTH_TOKEN` in env (optional) | Required by some LocalStack Hobby builds — **never commit** |

## App profile

Object storage uses the `aws` profile (`S3ObjectStorageAdapter`). Keep `local` active until Phase 3 so the in-memory message bus / worker still load:

```bash
cd apps/api
./mvnw spring-boot:run -Dspring-boot.run.profiles=aws,local
```

`lab.cloud.provider=aws` selects S3 over the filesystem adapter even when `local` is also active.

## Limitações conhecidas

- Auth/account obrigatória no modelo LocalStack atual (2026).
- Features avançadas podem exigir planos pagos.
- Não deployar LocalStack como “produção AWS” em ambiente público.
