# Backend — Tendanz Pricing Engine

> A Spring Boot 3 REST API executing dynamic insurance pricing logic via configurable base rates, age-category multipliers, and geographic zone factors. Persists full quote histories and generates downloadable PDF representations.

---

## Table of Contents

1. [Tech Stack](#tech-stack)
2. [Prerequisites](#prerequisites)
3. [Installation & Local Setup](#installation--local-setup)
4. [Environment Configuration](#environment-configuration)
5. [Folder Structure](#folder-structure)
6. [Architecture Overview](#architecture-overview)
7. [API Reference](#api-reference)
   - [Products API](#products-api)
   - [Quotes API](#quotes-api)
8. [Pricing Calculation Logic](#pricing-calculation-logic)
9. [Database](#database)
10. [Entities & DTOs](#entities--dtos)
11. [Exception Handling](#exception-handling)
12. [Testing](#testing)
13. [Build & Scripts](#build--scripts)

---

## Tech Stack

| Category | Technology | Version | Purpose |
|---|---|---|---|
| Framework | Spring Boot | 3.x | Application runtime, auto-configuration |
| Web Layer | Spring Web (Spring MVC) | 3.x | REST controllers, request mapping |
| Validation | Spring Boot Starter Validation | 3.x | Bean validation via `@Valid`, constraint annotations |
| Data Access | Spring Data JPA | 3.x | Repository abstraction over Hibernate ORM |
| ORM | Hibernate | 6.x | JPA implementation, schema generation |
| Database | H2 Database Engine | 2.x | In-memory relational DB, auto-seeded on startup |
| PDF Generation | OpenPDF / iText | — | Quote PDF rendering via `PdfService` |
| Testing | JUnit 5 | 5.x | Unit and integration test runner |
| Testing | Mockito | 5.x | Mock injection for service-layer tests |
| Build Tool | Maven | 3.8+ | Dependency management, lifecycle |
| Language | Java | 17+ | Application language |

---

## Prerequisites

| Tool | Minimum Version | Check Command |
|---|---|---|
| Java (JDK) | 17 | `java -version` |
| Maven | 3.8 | `mvn -version` |
| IDE (optional) | IntelliJ IDEA / VS Code | — |

No external database installation is required. H2 runs fully in-memory and is seeded automatically on startup via `schema.sql` and `data.sql`.

---

## Installation & Local Setup

```bash
# 1. Navigate to the backend directory
cd backend

# 2. Build the project (skip tests for fast startup)
mvn clean install -DskipTests

# 3. Run the application
mvn spring-boot:run

# The API will be available at:
http://localhost:8080/api
```

### H2 Console (Development Only)

The embedded H2 web console is available during development to inspect the in-memory database:

```
URL:      http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:tendanzdb
Username: sa
Password: (leave blank)
```

> The H2 console is disabled in production profile.

---

## Environment Configuration

All configuration is centralized in `src/main/resources/application.yml`.

```yaml
spring:
  application:
    name: tendanz-pricing-engine

  datasource:
    url: jdbc:h2:mem:tendanzdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: none            # Schema managed manually via schema.sql
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  h2:
    console:
      enabled: true
      path: /h2-console

  sql:
    init:
      mode: always              # Runs schema.sql + data.sql on every startup

server:
  port: 8080

logging:
  level:
    com.tendanz: DEBUG
    org.hibernate.SQL: DEBUG
```

---

## Folder Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/tendanz/pricing/
│   │   │   ├── controller/
│   │   │   │   ├── ProductController.java       # REST endpoints for /api/products
│   │   │   │   └── QuoteController.java         # REST endpoints for /api/quotes
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── ProductRequestDTO.java        # Inbound payload for creating/updating products
│   │   │   │   ├── ProductResponseDTO.java       # Outbound product representation (with rules)
│   │   │   │   ├── PricingRuleDTO.java           # Represents one AgeCategory → multiplier mapping
│   │   │   │   ├── QuoteRequestDTO.java          # Inbound payload for quote calculation
│   │   │   │   └── QuoteResponseDTO.java         # Outbound quote with computed finalPrice
│   │   │   │
│   │   │   ├── entity/
│   │   │   │   ├── Product.java                  # JPA entity: id, name, description, baseRate, rules
│   │   │   │   ├── PricingRule.java              # JPA entity: id, product(FK), ageCategory, multiplier
│   │   │   │   ├── Zone.java                     # JPA entity: id, name, riskMultiplier
│   │   │   │   └── Quote.java                    # JPA entity: persisted quote with inputs + finalPrice
│   │   │   │
│   │   │   ├── enums/
│   │   │   │   └── AgeCategory.java              # Enum: YOUNG_ADULT, ADULT, SENIOR (maps to age bands)
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java   # @ControllerAdvice — maps exceptions to HTTP responses
│   │   │   │   ├── ResourceNotFoundException.java # Thrown when entity is not found by ID
│   │   │   │   └── ValidationException.java      # Thrown on business rule violations
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── ProductRepository.java        # Spring Data JPA — CRUD for Product
│   │   │   │   ├── PricingRuleRepository.java    # Spring Data JPA — CRUD for PricingRule
│   │   │   │   ├── ZoneRepository.java           # Spring Data JPA — CRUD for Zone
│   │   │   │   └── QuoteRepository.java          # Spring Data JPA — CRUD + pagination for Quote
│   │   │   │
│   │   │   └── service/
│   │   │       ├── ProductService.java           # Product CRUD, rule mapping, DTO conversion
│   │   │       ├── PricingService.java           # Core pricing formula: baseRate × ageFactor × zoneFactor
│   │   │       ├── QuoteService.java             # Orchestrates pricing, persists Quote entity
│   │   │       └── PdfService.java               # Generates PDF byte stream from QuoteResponseDTO
│   │   │
│   │   └── resources/
│   │       ├── application.yml                   # Full Spring Boot configuration
│   │       ├── schema.sql                        # DDL — CREATE TABLE statements for all entities
│   │       └── data.sql                          # DML — Seed data for products, rules, and zones
│   │
│   └── test/
│       └── java/com/tendanz/pricing/
│           ├── service/
│           │   ├── PricingServiceTest.java       # Unit tests for pricing formula logic
│           │   ├── ProductServiceTest.java       # Unit tests for product CRUD and rule mapping
│           │   └── QuoteServiceTest.java         # Unit tests for quote orchestration
│           └── controller/
│               ├── ProductControllerTest.java    # MockMvc integration tests for /api/products
│               └── QuoteControllerTest.java      # MockMvc integration tests for /api/quotes
│
└── pom.xml                                       # Maven project descriptor and dependency declarations
```

---

## Architecture Overview

```
HTTP Request
     │
     ▼
[ Controller Layer ]          ProductController / QuoteController
     │  @Valid DTO
     ▼
[ Service Layer ]             ProductService / QuoteService
     │                           └── PricingService (formula engine)
     │                           └── PdfService (PDF rendering)
     ▼
[ Repository Layer ]          Spring Data JPA interfaces
     │
     ▼
[ H2 In-Memory DB ]           Seeded from schema.sql + data.sql
```

**Request lifecycle for a quote calculation:**

1. `POST /api/quotes` receives a `QuoteRequestDTO`.
2. `QuoteController` delegates to `QuoteService.calculate()`.
3. `QuoteService` calls `PricingService` to compute `finalPrice = baseRate × ageFactor × zoneFactor`.
4. The result is persisted as a `Quote` entity via `QuoteRepository`.
5. A `QuoteResponseDTO` (including `finalPrice`) is returned to the client.

---

## API Reference

All endpoints are prefixed with `/api`. Responses use `Content-Type: application/json` unless otherwise noted.

---

### Products API

#### `GET /api/products`

Retrieve a list of all configured insurance products.

- **Auth required:** No
- **Request Body:** None
- **Response `200 OK`:**

```json
[
  {
    "id": 1,
    "name": "Essential Cover",
    "description": "Basic insurance package for low-risk profiles.",
    "baseRate": 120.00,
    "pricingRules": [
      { "ageCategory": "YOUNG_ADULT", "multiplier": 1.2 },
      { "ageCategory": "ADULT",       "multiplier": 1.0 },
      { "ageCategory": "SENIOR",      "multiplier": 1.8 }
    ]
  }
]
```

---

#### `GET /api/products/{id}`

Retrieve a single product by ID, including its full pricing rule set.

- **Auth required:** No
- **Path Variable:** `id` — Product ID (Long)
- **Response `200 OK`:** Single `ProductResponseDTO` (same structure as above)
- **Response `404 Not Found`:**

```json
{ "error": "Product not found", "id": 99 }
```

---

#### `POST /api/products`

Create a new insurance product and associate initial pricing rules.

- **Auth required:** No
- **Request Body:**

```json
{
  "name": "Premium Shield",
  "description": "Full-coverage package with enhanced benefits.",
  "baseRate": 250.00,
  "pricingRules": [
    { "ageCategory": "YOUNG_ADULT", "multiplier": 1.1 },
    { "ageCategory": "ADULT",       "multiplier": 1.0 },
    { "ageCategory": "SENIOR",      "multiplier": 2.0 }
  ]
}
```

- **Validation:**
  - `name`: required, non-blank
  - `baseRate`: required, must be > 0
  - `pricingRules`: required, must contain at least one rule, multiplier > 0

- **Response `201 Created`:** Full `ProductResponseDTO` with generated `id`
- **Response `400 Bad Request`:** Validation error details

---

#### `PUT /api/products/{id}`

Update an existing product. Replaces base rate and remaps all pricing rules.

- **Auth required:** No
- **Path Variable:** `id` — Product ID (Long)
- **Request Body:** Same structure as `POST /api/products`
- **Response `200 OK`:** Updated `ProductResponseDTO`
- **Response `404 Not Found`:** Product not found

---

### Quotes API

#### `GET /api/quotes`

Retrieve all persisted quotes with pagination support.

- **Auth required:** No
- **Query Params:**

| Param | Type | Default | Description |
|---|---|---|---|
| `page` | int | 0 | Page index (0-based) |
| `size` | int | 10 | Number of records per page |

- **Response `200 OK`:**

```json
{
  "content": [
    {
      "id": 1,
      "productName": "Essential Cover",
      "insuredAge": 35,
      "ageCategory": "ADULT",
      "zoneName": "Urban North",
      "baseRate": 120.00,
      "ageFactor": 1.0,
      "zoneFactor": 1.3,
      "finalPrice": 156.00,
      "calculatedAt": "2025-06-12T14:30:00Z"
    }
  ],
  "totalElements": 42,
  "totalPages": 5,
  "number": 0
}
```

---

#### `GET /api/quotes/{id}`

Retrieve a specific persisted quote by ID.

- **Auth required:** No
- **Path Variable:** `id` — Quote ID (Long)
- **Response `200 OK`:** Single `QuoteResponseDTO`
- **Response `404 Not Found`:**

```json
{ "error": "Quote not found", "id": 99 }
```

---

#### `POST /api/quotes`

Calculate a new quote and persist it. Applies `finalPrice = baseRate × ageFactor × zoneFactor`.

- **Auth required:** No
- **Request Body:**

```json
{
  "productId": 1,
  "insuredAge": 52,
  "zoneId": 3
}
```

- **Validation:**
  - `productId`: required, must reference an existing product
  - `insuredAge`: required, integer between 0 and 120
  - `zoneId`: required, must reference an existing zone

- **Response `201 Created`:**

```json
{
  "id": 7,
  "productName": "Essential Cover",
  "insuredAge": 52,
  "ageCategory": "SENIOR",
  "zoneName": "Rural South",
  "baseRate": 120.00,
  "ageFactor": 1.8,
  "zoneFactor": 0.9,
  "finalPrice": 194.40,
  "calculatedAt": "2025-06-12T15:00:00Z"
}
```

- **Response `400 Bad Request`:** Validation errors
- **Response `404 Not Found`:** Product or Zone not found

---

#### `PUT /api/quotes/{id}`

Edit an existing quote's parameters and recalculate `finalPrice`.

- **Auth required:** No
- **Path Variable:** `id` — Quote ID (Long)
- **Request Body:** Same structure as `POST /api/quotes`
- **Response `200 OK`:** Updated `QuoteResponseDTO` with new `finalPrice`
- **Response `404 Not Found`:** Quote not found

---

#### `GET /api/quotes/pdf/{id}`

Generate and download a PDF representation of a specific quote.

- **Auth required:** No
- **Path Variable:** `id` — Quote ID (Long)
- **Response `200 OK`:**
  - `Content-Type: application/pdf`
  - `Content-Disposition: attachment; filename="quote-{id}.pdf"`
  - Body: PDF byte stream
- **Response `404 Not Found`:** Quote not found

---

## Pricing Calculation Logic

The core pricing formula is implemented in `PricingService.java`:

```
finalPrice = baseRate × ageFactor × zoneFactor
```

| Variable | Source | Description |
|---|---|---|
| `baseRate` | `Product.baseRate` | The product's base annual premium |
| `ageFactor` | `PricingRule.multiplier` | Multiplier resolved from the insured's `AgeCategory` |
| `zoneFactor` | `Zone.riskMultiplier` | Geographic risk multiplier for the selected zone |

### Age Category Resolution

The `AgeCategory` enum maps insured age to a pricing tier:

| AgeCategory | Age Range | Example Multiplier |
|---|---|---|
| `YOUNG_ADULT` | 18 – 30 | 1.2 |
| `ADULT` | 31 – 55 | 1.0 |
| `SENIOR` | 56+ | 1.8 |

Age-to-category resolution is done inside `PricingService` before looking up the matching `PricingRule` for the given product.

### Example Calculation

```
Product: Essential Cover  → baseRate = 120.00
Insured Age: 52           → AgeCategory = ADULT → ageFactor = 1.0
Zone: Urban North         → zoneFactor = 1.3

finalPrice = 120.00 × 1.0 × 1.3 = 156.00
```

---

## Database

The application uses **H2 in-memory database**, initialized on every startup from SQL scripts in `src/main/resources/`.

### Schema (`schema.sql`)

```sql
CREATE TABLE product (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  description VARCHAR(1000),
  base_rate   DECIMAL(10, 2) NOT NULL
);

CREATE TABLE pricing_rule (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id   BIGINT NOT NULL REFERENCES product(id),
  age_category VARCHAR(50) NOT NULL,   -- matches AgeCategory enum
  multiplier   DECIMAL(5, 2) NOT NULL
);

CREATE TABLE zone (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  name            VARCHAR(255) NOT NULL,
  risk_multiplier DECIMAL(5, 2) NOT NULL
);

CREATE TABLE quote (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id     BIGINT NOT NULL REFERENCES product(id),
  zone_id        BIGINT NOT NULL REFERENCES zone(id),
  insured_age    INT NOT NULL,
  age_category   VARCHAR(50) NOT NULL,
  base_rate      DECIMAL(10, 2) NOT NULL,
  age_factor     DECIMAL(5, 2) NOT NULL,
  zone_factor    DECIMAL(5, 2) NOT NULL,
  final_price    DECIMAL(10, 2) NOT NULL,
  calculated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Entity Relationship

```
Product (1) ──< PricingRule (N)
   └── id, name, description, baseRate
   └── pricingRules: List<PricingRule>

PricingRule
   └── product (FK → Product)
   └── ageCategory (AgeCategory enum)
   └── multiplier

Zone
   └── id, name, riskMultiplier

Quote
   └── product (FK → Product)
   └── zone (FK → Zone)
   └── insuredAge, ageCategory
   └── baseRate, ageFactor, zoneFactor, finalPrice
   └── calculatedAt
```

### Seed Data (`data.sql`)

The database is pre-populated with sample products, pricing rules, and zones on every startup, enabling immediate API usage without manual setup.

---

## Entities & DTOs

### `Product.java`

```java
@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private BigDecimal baseRate;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PricingRule> pricingRules;
}
```

### `PricingRule.java`

```java
@Entity
public class PricingRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private AgeCategory ageCategory;

    private BigDecimal multiplier;
}
```

### `Quote.java`

```java
@Entity
public class Quote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Zone zone;

    private Integer insuredAge;

    @Enumerated(EnumType.STRING)
    private AgeCategory ageCategory;

    private BigDecimal baseRate;
    private BigDecimal ageFactor;
    private BigDecimal zoneFactor;
    private BigDecimal finalPrice;
    private LocalDateTime calculatedAt;
}
```

### `AgeCategory.java`

```java
public enum AgeCategory {
    YOUNG_ADULT,  // 18 – 30
    ADULT,        // 31 – 55
    SENIOR        // 56+
}
```

---

## Exception Handling

All exceptions are handled centrally by `GlobalExceptionHandler.java` (`@ControllerAdvice`).

| Exception | HTTP Status | Description |
|---|---|---|
| `ResourceNotFoundException` | `404 Not Found` | Entity not found by ID |
| `ValidationException` | `400 Bad Request` | Business rule violation |
| `MethodArgumentNotValidException` | `400 Bad Request` | Bean validation failure (`@Valid`) |
| `Exception` (catch-all) | `500 Internal Server Error` | Unhandled runtime error |

### Error Response Format

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product with id 99 not found",
  "timestamp": "2025-06-12T15:00:00Z"
}
```

---

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=PricingServiceTest

# Run tests with coverage report (requires JaCoCo plugin)
mvn verify
```

### Test Coverage

| Test File | Layer | Scope |
|---|---|---|
| `PricingServiceTest.java` | Unit | Formula correctness, age category resolution, edge cases (age = 0, 120) |
| `ProductServiceTest.java` | Unit | CRUD operations, rule mapping, DTO conversion |
| `QuoteServiceTest.java` | Unit | Full quote orchestration, PricingService mock integration |
| `ProductControllerTest.java` | Integration | MockMvc: HTTP status codes, request validation, response body structure |
| `QuoteControllerTest.java` | Integration | MockMvc: quote creation, PDF endpoint, pagination |

Tests use `@ExtendWith(MockitoExtension.class)` for unit tests and `@SpringBootTest` with `MockMvc` for controller-level integration tests.

---

## Build & Scripts

| Goal | Command | Description |
|---|---|---|
| Compile | `mvn compile` | Compiles source files |
| Run | `mvn spring-boot:run` | Starts the application on port 8080 |
| Test | `mvn test` | Executes all JUnit 5 tests |
| Package | `mvn package` | Produces executable JAR in `target/` |
| Clean build | `mvn clean install` | Full clean, compile, test, and package |
| Skip tests | `mvn clean install -DskipTests` | Fast build without running tests |

The packaged JAR is located at:

```
target/tendanz-pricing-engine-*.jar
```

Run directly with:

```bash
java -jar target/tendanz-pricing-engine-*.jar
```

---

*Last updated: 11/04/2026 