# User Registration API

A Spring Boot REST API for user registration with validation, business rules enforcement, and comprehensive testing.

## Table of Contents

- [Features](#features)
- [Technical Stack](#technical-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [H2 Database Console](#h2-database-console)
- [Testing](#testing)
- [Architecture](#architecture)
- [Business Rules](#business-rules)

## Features

- User registration with validation
- Get user details by username
- Age verification (18+ required)
- Country validation (France only)
- Username uniqueness check
- Error handling with appropriate HTTP status codes
- Automatic logging via AOP (inputs, outputs, execution time)
- Embedded H2 database
- Unit and integration tests

## Technical Stack

- **Java**: 17
- **Spring Boot**: 4.0.0
- **Spring Data JPA**: Data persistence
- **Spring Validation**: Jakarta Bean Validation
- **Spring AOP**: Aspect-oriented logging
- **H2 Database**: In-memory database
- **JUnit 5 & Mockito**: Testing
- **JaCoCo**: Code coverage
- **Maven**: Build tool

## Prerequisites

- Java 17+
- Maven 3.6+ (or use the Maven wrapper)

## Getting Started

### Clone and build

```bash
git clone https://github.com/AmineOzil/user-registration
cd registration
./mvnw clean install
```

Windows:
```cmd
mvnw.cmd clean install
```

### Run the app

```bash
./mvnw spring-boot:run
```

Or on Windows:
```cmd
mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8080`

### Run tests and JaCoCo coverage

```bash
./mvnw test
```

Coverage report: `target/site/jacoco/index.html`

## API Documentation

### Base URL

```
http://localhost:8080/api/users
```

### Endpoints

#### 1. Register a New User

**POST** `/api/users`

**Request Headers:**
- `Content-Type: application/json`
- `X-Correlation-Id: <optional-correlation-id>` (auto-generated if not provided)

**Request Body:**
```json
{
  "username": "amine.bou",
  "birthdate": "2000-01-15",
  "countryOfResidence": "France",
  "phoneNumber": "0612345678",
  "gender": "MALE"
}
```

**Field Validations:**
- `username`: Required, 3-50 characters, alphanumeric with dots/underscores
- `birthdate`: Required, must be 18+ years old
- `countryOfResidence`: Required, must be "France" (case-insensitive)
- `phoneNumber`: Optional, minimum 10 digits, can start with + (e.g., +33612345678 or 0612345678)
- `gender`: Optional, values: MALE, FEMALE, OTHER (case-insensitive)

**Success Response (201 Created):**
```json
{
  "id": 1,
  "username": "amine.bou",
  "birthdate": "2000-01-15",
  "countryOfResidence": "France",
  "phoneNumber": "0612345678",
  "gender": "MALE"
}
```

**Error Responses:**

- **400 Bad Request** - Validation errors:
```json
{
    "status": 400,
    "error": "Bad Request",
    "message": "Input validation failed",
    "path": "/api/users",
    "correlationId": "test-123",
    "errorCode": "ERR_VALIDATION",
    "timestamp": "2025-12-24T10:08:32.9902309",
    "validationErrors": {
        "birthdate": "Birthdate is required",
        "username": "Username is required"
    }
}
```

- **422 Unprocessable Entity** - Business rule violation:
```json
{
    "status": 422,
    "error": "Unprocessable Content",
    "message": "User must be at least 18 years old",
    "path": "/api/users",
    "correlationId": "test-123",
    "errorCode": "ERR_RULE_AGE_MIN",
    "timestamp": "2025-12-24T10:10:36.0220208",
    "validationErrors": null
}
```

- **409 Conflict** - Username already exists:
```json
{
    "status": 409,
    "error": "Conflict",
    "message": "User with username 'amine.bou' already exists",
    "path": "/api/users",
    "correlationId": "0f99d74b-cb06-469f-873f-b6a2dc2493f9",
    "errorCode": "ERR_USER_ALREADY_EXISTS",
    "timestamp": "2025-12-24T10:15:58.3416983",
    "validationErrors": null
}
```

#### 2. Get User Details

**GET** `/api/users/{username}`

**Path Parameters:**
- `username`: The username to retrieve

**Success Response (200 OK):**
```json
{
  "id": 1,
  "username": "amine.bou",
  "birthdate": "2000-01-15",
  "countryOfResidence": "France",
  "phoneNumber": "0612345678",
  "gender": "MALE"
}
```

**Error Response (404 Not Found):**
```json
{
    "status": 404,
    "error": "Not Found",
    "message": "User with username 'nonexistent.user' not found",
    "path": "/api/users/nonexistent.user",
    "correlationId": "d30fe30c-9f96-4ebd-b14e-2c80c46ddbe4",
    "errorCode": "ERR_USER_NOT_FOUND",
    "timestamp": "2025-12-24T10:17:06.8778867",
    "validationErrors": null
}
```

### Example cURL Commands

**Register a user:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: test-123" \
  -d '{
    "username": "amine.bou",
    "birthdate": "2000-01-15",
    "countryOfResidence": "France",
    "phoneNumber": "0612345678",
    "gender": "MALE"
  }'
```

**Get user details:**
```bash
curl -X GET http://localhost:8080/api/users/amine.bou
```

## H2 Database Console

The H2 web console is enabled but it does not work on browser with Springboot 4 (check : https://medium.com/@raushan1156/h2-console-not-working-in-spring-boot-4-0-0-7873e20c82d5). You can also inspect the database by using any SQL client (DBeaver, IntelliJ Database Tool, etc.) with these connection settings:

**Connection Details:**
- **JDBC URL**: `jdbc:h2:mem:userdb`
- **Driver**: H2 (org.h2.Driver)
- **Username**: `sa`
- **Password**: (leave empty)

**Note**: The database is in-memory and will be recreated on each application restart.

## Testing

### Run tests

```bash
./mvnw test
```

### Coverage

The project has complete test coverage:
- **Unit tests**: Services, business logic, validators
- **Integration tests**: Full workflows with MockMvc
- **Coverage**: 88%+ (excluding DTOs and main class)

View report:
```bash
./mvnw test
# Open target/site/jacoco/index.html
```

### Test Structure

```
src/test/java/com/userapi/registration/
├── controller/          # REST API integration tests
├── service/            # Business logic unit tests
├── domain/policy/      # Domain rules unit tests
├── validation/         # Custom validators tests
├── aspect/             # AOP logging tests
├── entity/             # Entity logic tests
└── RegistrationApplicationTests.java
```

**Note**: `GlobalExceptionHandler` is covered through integration tests in `UserControllerIntegrationTest`, only the generic 500 handler is untested.

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│   Controller Layer (REST API)       │  ← HTTP endpoints
├─────────────────────────────────────┤
│   Service Layer (Business Logic)    │  ← Business rules
├─────────────────────────────────────┤
│   Repository Layer (Data Access)    │  ← JPA repositories
├─────────────────────────────────────┤
│   Entity Layer (Domain Model)       │  ← JPA entities
└─────────────────────────────────────┘

Cross-cutting concerns:
- AOP Logging (ApiLoggingAspect, LoggingAspect)
- Global Exception Handling (@RestControllerAdvice)
- Correlation ID Filter
```

### Key Components

- **Controllers**: Handle HTTP requests, delegate to services
- **Services**: Business logic and orchestration
- **Repositories**: Data access via Spring Data JPA
- **DTOs**: Transfer objects for API contracts
- **Entities**: JPA entities mapped to database tables
- **Aspects**: Cross-cutting concerns (logging, monitoring)
- **Validators**: Custom validation logic
- **Exception Handlers**: Centralized error handling

## Business Rules

1. **Age**: Must be 18+ years old
2. **Country**: Only France residents can register
3. **Username**: Must be unique
4. **Phone**: Optional, minimum 10 digits (any country format)
5. **Gender**: Optional, MALE/FEMALE/OTHER (case-insensitive)

## Logging & Monitoring

### AOP Logging

All API calls are logged automatically:
- HTTP method and URI
- Request parameters
- Response status
- Execution time
- Correlation ID

**Log levels:**
- `INFO`: Operation results (API success, execution time)
- `WARN`: Service exceptions (business rule violations)
- `DEBUG`: Detailed payloads (enable with `logging.level.com.userapi.registration.aspect=DEBUG`)
- `ERROR`: Unexpected errors with stacktraces

### Correlation ID

Each request gets a unique ID:
- Provided via `X-Correlation-Id` header, or auto-generated
- Included in all logs
- Returned in error responses

## Configuration

Main configuration in `application.properties`:

```properties
spring.application.name=registration

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:userdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=false

# Logging Configuration
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{correlationId}] %-5level [%thread] %logger{36} - %msg%n
logging.level.com.userapi.registration=DEBUG
logging.level.com.userapi.registration.aspect=DEBUG
```

## Error Handling Strategy

| HTTP Status | Error Code | Description |
|------------|------------|-------------|
| 400 | ERR_VALIDATION | DTO validation failure (malformed request) |
| 400 | ERR_JSON_PARSE | Invalid JSON format |
| 404 | ERR_USER_NOT_FOUND | Requested user doesn't exist |
| 409 | ERR_USER_ALREADY_EXISTS | Username conflict |
| 422 | ERR_RULE_AGE_MIN | User under 18 years old |
| 422 | ERR_RULE_COUNTRY_FR | User not resident of France |
| 500 | ERR_INTERNAL | Unexpected server error |

---