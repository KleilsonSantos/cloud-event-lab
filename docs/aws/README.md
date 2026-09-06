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
lsof -nP -iTCP:4566 -sTCP:LISTEN

cd deploy/compose
docker compose --profile aws up -d
```

## App profile

```bash
cd apps/api
./mvnw spring-boot:run -Dspring-boot.run.profiles=aws,local
```

> Adapters AWS SDK ainda são **Phase 2/3** (placeholder no código). Profile YAML já declara endpoint `http://localhost:4566`.

## Limitações conhecidas

- Auth/account obrigatória no modelo LocalStack atual (2026).
- Features avançadas podem exigir planos pagos.
- Não deployar LocalStack como “produção AWS” em ambiente público.
