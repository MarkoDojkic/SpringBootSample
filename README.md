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
docker compose build
docker compose up
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
```

The report endpoint returns a JasperReports-generated PDF. Liquibase creates and seeds `integration_message` on startup. LDAP is enabled in Compose and can be disabled locally with `APP_LDAP_ENABLED=false`. Replace development secrets and externalize credentials before production use.

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

Alternatively install `grpcurl` on Windows with and use the forwarded host:

```powershell
winget install grpcurl.grpcurl
grpcurl -insecure turbo-tribble-rp6555q77jpfp5jq-9090.app.github.dev:443 list
```
