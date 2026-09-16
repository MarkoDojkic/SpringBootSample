# Spring Boot Enterprise Integration Stack

Reference application built for **Java 17** and Spring Boot 3.5.16. It combines the capabilities demonstrated by this project and `SOAP_Learning-main` without requiring the Kotlin application.

## Included stack

- Spring Web, Validation, Security, AOP, Actuator, JDBC, Quartz, and JPA-compatible datasource configuration
- JPA entity persistence with generated primary keys, Spring Data auditing, and Hibernate Envers revision history
- Criteria API repository search and Jasypt encryption converters for strings, dates, and byte arrays
- Ehcache-backed Spring caching for Drools rule evaluations
- HikariCP (default) and C3P0 profiles
- Spring Cloud Eureka client/server and OpenFeign
- RabbitMQ and Apache Kafka
- Jackson, JJWT, Jasypt, Bouncy Castle, and OAuth2 resource-server dependencies
- Drools/KIE rules
- HAPI FHIR R4
- Apache CXF SOAP
- gRPC with protobuf-generated service stubs
- Spring Cloud Config Server
- Oracle Database with Liquibase migrations and seed data
- Optional LDAP authentication
- AspectJ compile-time weaving (CTW)
- SpringDoc OpenAPI and Micrometer Prometheus metrics
- JasperReports **7.0.8** PDF reporting
- JUnit 5, Mockito, and Spring Security test support

## Requirements

- JDK 17
- Maven 3.9+ or Docker
- Docker Compose for RabbitMQ, Kafka, Eureka, Config Server, Oracle, and LDAP
- Keycloak is also started by Compose for OAuth2 experiments (`admin` / `admin`)

## Run with Docker Compose

Build all JARs locally first:

```bash
mvn clean package -DskipTests
mvn -f discovery-server/pom.xml package -DskipTests
mvn -f config-server/pom.xml package -DskipTests
```

Then build the runtime images and start the stack:

```bash
docker compose build --no-cache enterprise-service
docker compose up -d
```

Compose does not run Maven. The Dockerfiles only copy the existing JARs from
`target/`, `discovery-server/target/`, and `config-server/target/`.

Services and endpoints:

| Service | URL |
|---|---|
| Enterprise application | http://localhost:8080 |
| gRPC endpoint | localhost:9090 |
| Config Server | http://localhost:8888 |
| Health | http://localhost:8080/actuator/health |
| Prometheus metrics | http://localhost:8080/actuator/prometheus |
| OpenAPI UI | http://localhost:8080/swagger-ui.html |
| SOAP WSDL | http://localhost:8080/services/eligibility?wsdl |
| Eureka | http://localhost:8761 |
| RabbitMQ management | http://localhost:15672 (`guest` / `guest`) |
| Keycloak | http://localhost:8081 |
| Oracle | localhost:1521/FREEPDB1 (`enterprise` / `enterprise`) |
| LDAP | ldap://localhost:10389 (`cn=admin,dc=example,dc=org` / `admin`) |

## Run locally

Start Eureka and RabbitMQ/Kafka, then run:

```bash
mvn test
mvn spring-boot:run
```

The default datasource profile is HikariCP. To use C3P0:

```bash
# PowerShell
$env:JASYPT_ENCRYPTOR_PASSWORD="change-this-local-development-password"
mvn spring-boot:run -Dspring-boot.run.profiles=c3p0
```

## API examples

```text
POST /api/auth/token?username=marko
POST /api/fhir/patient?familyName=Doe&givenName=Jane
GET  /api/rules/evaluate?age=42
GET  /api/reports/eligibility?patientId=123
GET  /api/messages
GET  /api/messages/search?text=hello
POST /api/messages        {"message":"hello","secretText":"private","secretDate":"2026-01-01T00:00:00Z","secretBytes":"c2Vuc2l0aXZl"}
PUT  /api/messages/{id}   {"message":"updated"}
POST /api/integrations/events {"message":"hello from both brokers"}
```

The report endpoint returns a JasperReports-generated PDF. Liquibase creates and seeds `integration_message` on startup. LDAP is enabled in Compose and can be disabled locally with `APP_LDAP_ENABLED=false`. Replace development secrets and externalize credentials before production use.

## Integration verification

The following commands assume `docker compose up -d` has completed. On Windows use
`curl.exe` (PowerShell's `curl` alias is different).

```powershell
# Actuator and Prometheus
curl.exe http://localhost:8080/actuator/health
curl.exe http://localhost:8080/actuator/prometheus

# OAuth2 resource server using the Compose Keycloak master realm
$keycloakPassword = "admin"
$token = (curl.exe -s -X POST http://localhost:8081/realms/master/protocol/openid-connect/token `
  -H "Content-Type: application/x-www-form-urlencoded" `
  -d "client_id=admin-cli" -d "username=admin" -d ("password={0}" -f $keycloakPassword) `
  -d "grant_type=password" | ConvertFrom-Json).access_token
$authHeader = "Authorization: Bearer {0}" -f $token
curl.exe -H $authHeader http://localhost:8080/api/messages

# LDAP authentication (seeded by ldap/bootstrap.ldif)
curl.exe -u demo-user:demo-password http://localhost:8080/api/messages

# RabbitMQ and Kafka (the response reports which publishers are enabled)
curl.exe -X POST http://localhost:8080/api/integrations/events `
  -H $authHeader -H "Content-Type: application/json" `
  -d '{"message":"integration smoke test"}'
docker compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh `
  --bootstrap-server kafka:9092 --topic enterprise.events --from-beginning --max-messages 1

# Eureka and Config Server
curl.exe http://localhost:8761/eureka/apps/enterprise-service
curl.exe http://localhost:8888/enterprise-service/hikari

# JasperReports PDF
curl.exe -o eligibility.pdf -H $authHeader `
  "http://localhost:8080/api/reports/eligibility?patientId=123"

# Native gRPC reflection, health, and eligibility RPC
grpcurl.exe -plaintext localhost:9090 list
grpcurl.exe -plaintext -d '{"patientId":"123"}' localhost:9090 `
  EligibilityGrpcService/CheckEligibility

# Quartz executions are logged every 30 seconds by ExampleQuartzJob.
docker compose logs --since=1m enterprise-service | Select-String "Quartz job executed"
```

The Compose enterprise container uses the Codespaces host gateway
(`host.docker.internal`) for published dependency ports because Docker bridge
traffic is not reliable in this environment. For local execution, start the
dependencies and enable Kafka explicitly:

```powershell
$env:APP_MESSAGING_KAFKA_ENABLED="true"
$env:APP_LDAP_ENABLED="true"
$env:JASYPT_ENCRYPTOR_PASSWORD="change-this-local-development-password"
mvn spring-boot:run
```

Creating or updating a message through `/api/messages` produces an Envers
revision in `integration_message_aud`; the `created_at` and `updated_at`
columns demonstrate Spring Data auditing. The application exposes native
gRPC reflection, health, and `EligibilityGrpcService/CheckEligibility` on
port `9090`. SAML and gRPC-Web are intentionally not included.

### Jasypt encryption key

Compose requires `JASYPT_ENCRYPTOR_PASSWORD`; it will refuse to start without one. Create `.env` from `.env.example` and set a strong value:

```bash
cp .env.example .env
```

The `secretText`, `secretDate`, and `secretBytes` entity columns are encrypted before persistence using Jasypt AES-256 with a random IV. Their plaintext values are only returned after Jasypt decrypts them in the application. Keep the password unchanged for existing data; changing it makes previously encrypted values unreadable.

## Postman and gRPC

Import `postman/SpringBootSample.postman_collection.json` into Postman Desktop. Run **Get development JWT** first; its test script stores the token for the protected requests. Update the collection `baseUrl` variable when Codespaces assigns a different forwarded application URL.

Postman Desktop supports gRPC. The collection includes a request using the forwarded `9090` host. Use server reflection and select `EligibilityGrpcService/CheckEligibility`, then send:

```json
{
  "patientId": "123"
}
```

Alternatively, install `grpcurl` on Windows and use the forwarded host:

```powershell
winget install grpcurl.grpcurl
grpcurl -insecure turbo-tribble-rp6555q77jpfp5jq-9090.app.github.dev:443 list
```

### Docker gRPC smoke test

In GitHub Codespaces, with the enterprise service running and port `9090` published:

```bash
chmod +x scripts/grpc-smoke-test.sh
./scripts/grpc-smoke-test.sh
```

The script uses the `fullstorydev/grpcurl` Docker image and verifies reflection,
the standard gRPC health service, and `EligibilityGrpcService/CheckEligibility`.
To test a different target or patient:

```bash
GRPC_TARGET=localhost:9090 PATIENT_ID=456 ./scripts/grpc-smoke-test.sh
```

### Complete integration smoke test

Run the full HTTP, security, messaging, SOAP, reporting, service discovery,
configuration, and native gRPC flow:

```bash
chmod +x scripts/integration-smoke-test.sh
./scripts/integration-smoke-test.sh
```

The script prints styled `[ OK ]` or `[FAIL]` results and writes detailed
command output, including generated PDF files, under `logs/`.

Run the same flow with C3P0 by rebuilding the application with the alternate
profile:

```bash
SPRING_PROFILES_ACTIVE=c3p0 docker compose up -d --build --force-recreate enterprise-service
POOL_PROFILE=c3p0 ./scripts/integration-smoke-test.sh
```

The C3P0 profile uses the same Compose Oracle datasource variables as HikariCP.
