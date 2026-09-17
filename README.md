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
- Reusable Spring Boot auto-configuration modules under `base/` for datasource pools,
  cross-cutting JPA auditing, Spring AOP audit logging, and Drools session creation
- SpringDoc OpenAPI and Micrometer Prometheus metrics
- JasperReports **7.0.8** PDF reporting
- JUnit 5, Mockito, and Spring Security test support

## Requirements

- JDK 17
- Maven 3.9+ or Docker
- Docker Compose for RabbitMQ, Kafka, Eureka, Config Server, Oracle, and LDAP
- Keycloak is also started by Compose for OAuth2 experiments (`admin` / `admin`)

## Run with Docker Compose

Build the reusable base modules first, then the enterprise application and
independent services:

```bash
mvn -f base/pom.xml clean install
mvn clean test
mvn package -DskipTests
mvn -f discovery-server/pom.xml package -DskipTests
mvn -f config-server/pom.xml package -DskipTests
mvn -f rules-service/pom.xml package -DskipTests
mvn -f gateway/pom.xml package -DskipTests
```

The same commands work from Windows PowerShell when Maven and Docker Desktop
are on `PATH`.

Then build the runtime images and start the stack:

```bash
docker compose build --no-cache
docker compose up -d
```

Compose does not run Maven. The Dockerfiles only copy the existing JARs from
`target/`, `discovery-server/target/`, `config-server/target/`,
`rules-service/target/`, and `gateway/target/`.

Create `.env` before starting Compose and set the required encryption password:

```bash
cp -n .env.example .env
# Edit .env and set JASYPT_ENCRYPTOR_PASSWORD
```

On Windows PowerShell:

```powershell
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
# Edit .env and set JASYPT_ENCRYPTOR_PASSWORD
docker compose build --no-cache
docker compose up -d
```

Services and endpoints:

| Service | URL |
|---|---|
| Enterprise application | http://localhost:8080 |
| Spring Cloud Gateway | http://localhost:8088 |
| Rules service | http://localhost:8090 |
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

Docker Compose is the recommended way to run the full stack because it starts
the application, gateway, service discovery, Config Server, Oracle, LDAP,
Keycloak, RabbitMQ, and Kafka together.

For local application-only development, start the Compose dependencies first,
then run:

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

The enterprise application, rules service, and Spring Cloud Gateway register with
Eureka. The enterprise application resolves `rules-service` through Eureka
using OpenFeign, and the gateway routes `/api/rules/**` to the discovered rules
service and other application routes to `enterprise-service`.

## Integration verification

The following commands assume `docker compose up -d` has completed.

On Windows, the easiest full verification flow is the batch wrapper:

```powershell
.\scripts\integration-smoke-test.bat
```

The wrapper uses Git for Windows Bash when available, keeps the terminal open
after completion, and writes detailed logs under `logs/`.

To run individual checks on Windows, use `curl.exe` because PowerShell's `curl`
alias is different.

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

Import `postman/SpringBootSample.postman_collection.json` into Postman Desktop. All HTTP requests use the Spring Cloud Gateway proxy on port `8088`. Run **Get development JWT** first; its test script stores the token for the protected requests. Update the collection `baseUrl` and `gatewayUrl` variables when Codespaces assigns a different forwarded gateway URL.

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

On Windows PowerShell, use the batch wrapper:

```powershell
.\scripts\grpc-smoke-test.bat
```

The Windows wrapper runs the Bash script through Git Bash when available. The
script automatically targets `host.docker.internal:9090` from the `grpcurl`
container so Docker Desktop can reach the published gRPC port.

### Complete integration smoke test

Run the full HTTP, security, messaging, SOAP, reporting, service discovery,
configuration, document-library, and native gRPC flow:

```bash
chmod +x scripts/integration-smoke-test.sh
POOL_PROFILE=hikari ./scripts/integration-smoke-test.sh
```

The script prints styled `[ OK ]` or `[FAIL]` results and writes detailed
command output, including generated PDF, QR, and log files, under `logs/`.

The smoke test covers OAuth2, secured APIs, Drools, FHIR, JasperReports, SOAP,
RabbitMQ, Kafka, service discovery, Prometheus, gateway health, and the Tika,
PDFBox, iText, Apache POI, and ZXing demo endpoints.

On Windows PowerShell, run the same full flow with:

```powershell
.\scripts\integration-smoke-test.bat
```

If the batch file reports that `docker` is unavailable from WSL, install Git for
Windows or enable Docker Desktop's WSL integration for that distro. The checked
in wrapper prefers Git Bash to avoid that WSL-specific Docker visibility issue.

Run the same flow with C3P0 by recreating the enterprise container with the
alternate profile:

```bash
SPRING_PROFILES_ACTIVE=c3p0 docker compose up -d --build --force-recreate enterprise-service
POOL_PROFILE=c3p0 ./scripts/integration-smoke-test.sh
```

On Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="c3p0"
docker compose up -d --build --force-recreate enterprise-service
$env:POOL_PROFILE="c3p0"
.\scripts\integration-smoke-test.bat
```

`SPRING_PROFILES_ACTIVE` selects the runtime datasource and mapper profile.
`POOL_PROFILE` labels the smoke-test output; it does not configure Spring.

| Runtime profile | Datasource | DTO mapper |
|---|---|---|
| `hikari` | HikariCP | MapStruct |
| `c3p0` | C3P0 | Dozer |

Run gRPC validation separately:

```bash
chmod +x scripts/grpc-smoke-test.sh
GRPC_TARGET=localhost:9090 PATIENT_ID=123 ./scripts/grpc-smoke-test.sh
```

Switch back to Hikari when finished:

```bash
SPRING_PROFILES_ACTIVE=hikari docker compose up -d --build --force-recreate enterprise-service
POOL_PROFILE=hikari ./scripts/integration-smoke-test.sh
```

On Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="hikari"
docker compose up -d --build --force-recreate enterprise-service
$env:POOL_PROFILE="hikari"
.\scripts\integration-smoke-test.bat
```

### Profile-specific local runs

The main application defaults to Hikari. Run it locally with either profile:

```bash
JASYPT_ENCRYPTOR_PASSWORD=change-this-local-development-password \
SPRING_PROFILES_ACTIVE=hikari \
mvn spring-boot:run
```

```bash
JASYPT_ENCRYPTOR_PASSWORD=change-this-local-development-password \
SPRING_PROFILES_ACTIVE=c3p0 \
mvn spring-boot:run
```

The rules service also supports both profiles:

```bash
SPRING_PROFILES_ACTIVE=hikari \
mvn -f rules-service/pom.xml spring-boot:run
```

```bash
SPRING_PROFILES_ACTIVE=c3p0 \
mvn -f rules-service/pom.xml spring-boot:run
```

Verify the rules mapper in either profile:

```bash
curl -fsS "http://localhost:8090/api/rules/evaluate?age=42"
```

Expected response:

```json
{"age":42,"category":"ADULT"}
```

The main application message endpoints use the same profile-selected mapper:

```text
GET  /api/messages
GET  /api/messages/search?text=hello
POST /api/messages
PUT  /api/messages/{id}
```

### Build troubleshooting

The base modules must be installed before compiling `rules-service`:

```bash
mvn -f base/pom.xml clean install
mvn -f rules-service/pom.xml clean test
```

If generated sources or compiler output are stale after changing Lombok,
MapStruct, Dozer, or AspectJ configuration:

```bash
rm -rf target rules-service/target
mvn clean test
mvn -f rules-service/pom.xml clean test
```

Lombok and MapStruct are processed by Maven Compiler. AspectJ then weaves the
compiled classes in `target/classes`; it does not recompile the Java sources.
This ordering is required because `ajc` does not provide Lombok annotation
processing.

## Reusable base modules

`base/` is a Maven parent/aggregator containing:

- `base-audit`: enables Spring Data JPA auditing when JPA is present.
- `base-datasource`: creates HikariCP by default or C3P0 when
  `app.datasource.pool=c3p0`.
- `base-aspect`: provides the existing audit timing aspect through Spring AOP.
- `base-drools`: provides the shared KIE session factory used by both applications.

Envers annotations and the `RevInfo` revision entity remain in the enterprise
application because they are coupled to its entity/revision table mappings.
`base-audit` therefore supplies cross-cutting Spring Data auditing only. The
audit aspect uses runtime Spring AOP. The AspectJ Maven plugin is configured to
weave compiled classes after Maven Compiler processing.
