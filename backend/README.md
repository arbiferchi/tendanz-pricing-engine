# Pricing Engine - Technical Test Backend

This is a Spring Boot backend for a pricing engine system designed for insurance and financial products. This is a technical assessment for Full Stack Java Spring Boot + Angular candidates at Tendanz Group.

## Project Structure

```
backend/
├── pom.xml                          # Maven configuration with dependencies
├── src/
│   ├── main/
│   │   ├── java/com/tendanz/pricing/
│   │   │   ├── PricingApplication.java          # Main Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── QuoteController.java         # REST API endpoints (TODO)
│   │   │   ├── service/
│   │   │   │   └── PricingService.java          # Business logic (TODO)
│   │   │   ├── repository/
│   │   │   │   ├── ProductRepository.java       # Product data access
│   │   │   │   ├── ZoneRepository.java          # Zone data access
│   │   │   │   ├── PricingRuleRepository.java   # Pricing rule data access
│   │   │   │   └── QuoteRepository.java         # Quote data access (TODO)
│   │   │   ├── entity/
│   │   │   │   ├── Product.java                 # Product entity
│   │   │   │   ├── Zone.java                    # Zone entity
│   │   │   │   ├── PricingRule.java             # Pricing rule entity
│   │   │   │   └── Quote.java                   # Quote entity (TODO)
│   │   │   ├── dto/
│   │   │   │   ├── QuoteRequest.java            # Request DTO with validation
│   │   │   │   └── QuoteResponse.java           # Response DTO
│   │   │   ├── enums/
│   │   │   │   └── AgeCategory.java             # Age category enum
│   │   │   └── exception/
│   │   │       └── GlobalExceptionHandler.java  # Centralized error handling (TODO)
│   │   └── resources/
│   │       ├── application.yml                  # Spring configuration
│   │       ├── schema.sql                       # Database schema
│   │       └── data.sql                         # Initial test data
│   └── test/
│       └── java/com/tendanz/pricing/
│           └── PricingServiceTest.java          # Service tests (TODO)
└── README.md                        # This file
```

## Technology Stack

- **Java 17** - Language
- **Spring Boot 3.2** - Framework
- **Spring Data JPA** - ORM and data access
- **H2 Database** - In-memory database for testing
- **Lombok** - Boilerplate code reduction
- **MapStruct** - DTO mapping (optional)
- **JUnit 5** - Testing framework

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access H2 Console
Navigate to `http://localhost:8080/h2-console`

## Database

The application uses an in-memory H2 database with the following tables:

### zone
- id (PK)
- code (unique)
- name
- risk_coefficient

### product
- id (PK)
- name
- description
- created_at

### pricing_rule
- id (PK)
- product_id (FK)
- base_rate
- age_factor_young
- age_factor_adult
- age_factor_senior
- age_factor_elderly
- created_at

### quote
- id (PK)
- product_id (FK)
- zone_id (FK)
- client_name
- client_age
- base_price
- final_price
- applied_rules (JSON)
- created_at

## Business Logic

### Pricing Calculation

The pricing calculation follows this formula:

```
Final Price = Base Rate × Age Factor × Zone Risk Coefficient
```

**Age Factors:**
- YOUNG (18-24): 1.3
- ADULT (25-45): 1.0
- SENIOR (46-65): 1.2
- ELDERLY (66-99): 1.5

**Example:**
- Product: Auto Insurance
- Base Rate: 500
- Age: 22 (YOUNG factor: 1.3)
- Zone: Tunis (risk coefficient: 1.0)
- Final Price: 500 × 1.3 × 1.0 = 650

## API Endpoints

### Create Quote
```
POST /api/quotes
Content-Type: application/json

{
  "productId": 1,
  "zoneCode": "TN-TUN",
  "clientName": "John Doe",
  "clientAge": 30
}

Response: 201 Created
{
  "quoteId": 1,
  "productName": "Auto Insurance",
  "zoneName": "Tunis",
  "clientName": "John Doe",
  "clientAge": 30,
  "basePrice": 500.00,
  "finalPrice": 500.00,
  "appliedRules": [...],
  "createdAt": "2026-04-07T12:00:00"
}
```

### Get Quote
```
GET /api/quotes/{id}

Response: 200 OK
{...quote details...}
```

### List Quotes
```
GET /api/quotes
GET /api/quotes?productId=1
GET /api/quotes?minPrice=500

Response: 200 OK
[{...quote 1...}, {...quote 2...}]
```

## Implementation Tasks

Files marked with TODO comments contain implementation tasks for candidates:

1. **QuoteController.java**
   - Implement POST /api/quotes endpoint
   - Implement GET /api/quotes/{id} endpoint
   - Implement GET /api/quotes endpoint with filtering

2. **PricingService.java**
   - Implement calculateQuote() method
   - Implement business logic for pricing calculations
   - Handle exceptions properly

3. **QuoteRepository.java**
   - Implement custom query methods
   - Add findByClientName
   - Add findByProductId
   - Add custom query for quotes above price threshold

4. **Quote.java Entity**
   - Add any additional validation if needed
   - Implement proper JSON serialization for appliedRules

5. **GlobalExceptionHandler.java**
   - Implement exception handlers for validation errors
   - Implement exception handlers for not found errors
   - Implement generic exception handler

6. **PricingServiceTest.java**
   - Add test cases for different age categories
   - Add test cases for zone coefficients
   - Add edge case tests
   - Add error handling tests

## Testing

Run the test suite:
```bash
mvn test
```

## Notes for Candidates

- Use clean, professional Java code following Spring Boot best practices
- Add proper validation on all inputs
- Implement comprehensive error handling
- Write meaningful test cases
- Use meaningful variable and method names
- Add JavaDoc comments where appropriate
- Apply SOLID principles in your implementation

## Submission Checklist

Before submitting, ensure:
- All Maven dependencies resolve correctly
- The application compiles without errors
- All TODO items are completed
- Unit tests pass
- API endpoints work correctly
- Database operations are properly handled
- Error cases are handled gracefully
