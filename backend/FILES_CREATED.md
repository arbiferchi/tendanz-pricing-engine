# Pricing Engine Backend - Files Created

This document lists all files created for the Spring Boot Pricing Engine test project.

## Project Configuration Files

### 1. pom.xml
- **Path**: `/backend/pom.xml`
- **Status**: Complete
- **Description**: Maven configuration file with Spring Boot 3.2, Java 17, and all required dependencies (spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-validation, h2, lombok, mapstruct, junit5)

### 2. application.yml
- **Path**: `/backend/src/main/resources/application.yml`
- **Status**: Complete
- **Description**: Spring configuration for H2 in-memory database, JPA settings, SQL initialization mode, logging levels, and server port configuration

## Database Configuration Files

### 3. schema.sql
- **Path**: `/backend/src/main/resources/schema.sql`
- **Status**: Complete
- **Description**: Database schema (DDL) defining tables for zone, product, pricing_rule, and quote with proper relationships and indexes

### 4. data.sql
- **Path**: `/backend/src/main/resources/data.sql`
- **Status**: Complete
- **Description**: Initial test data with 3 zones (Tunis, Sfax, Sousse), 3 products (Auto, Habitation, Santé), and corresponding pricing rules

## Java Classes - Enums

### 5. AgeCategory.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/enums/AgeCategory.java`
- **Status**: Complete (Provided)
- **Description**: Enum representing age categories (YOUNG, ADULT, SENIOR, ELDERLY) with min/max age ranges and factory method fromAge()

## Java Classes - Entities

### 6. Zone.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/entity/Zone.java`
- **Status**: Complete (Provided)
- **Description**: JPA entity for geographic zones with id, code, name, and risk_coefficient

### 7. Product.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/entity/Product.java`
- **Status**: Complete (Provided)
- **Description**: JPA entity for insurance products with id, name, description, and createdAt timestamp

### 8. PricingRule.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/entity/PricingRule.java`
- **Status**: Complete (Provided)
- **Description**: JPA entity for pricing rules with base_rate and age factors for all age categories, linked to Product

### 9. Quote.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/entity/Quote.java`
- **Status**: Skeleton with TODO
- **Description**: JPA entity for quotes containing client info, product/zone references, pricing, and applied rules (JSON)

## Java Classes - DTOs

### 10. QuoteRequest.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/dto/QuoteRequest.java`
- **Status**: Complete (Provided)
- **Description**: Request DTO with validation annotations (productId: NotNull, zoneCode: NotBlank, clientName: NotBlank Size, clientAge: Min 18 Max 99)

### 11. QuoteResponse.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/dto/QuoteResponse.java`
- **Status**: Complete (Provided)
- **Description**: Response DTO containing quoteId, productName, zoneName, clientName, clientAge, basePrice, finalPrice, appliedRules, and createdAt

## Java Classes - Repositories

### 12. ProductRepository.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/repository/ProductRepository.java`
- **Status**: Complete (Provided)
- **Description**: JpaRepository for Product entity

### 13. ZoneRepository.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/repository/ZoneRepository.java`
- **Status**: Complete (Provided)
- **Description**: JpaRepository for Zone entity with custom method findByCode(String code)

### 14. PricingRuleRepository.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/repository/PricingRuleRepository.java`
- **Status**: Complete (Provided)
- **Description**: JpaRepository for PricingRule entity with custom method findByProductId(Long productId)

### 15. QuoteRepository.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/repository/QuoteRepository.java`
- **Status**: Skeleton with TODO
- **Description**: JpaRepository for Quote entity with TODO methods: findByClientName, findByProductId, findQuotesAboveThreshold

## Java Classes - Service

### 16. PricingService.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/service/PricingService.java`
- **Status**: Skeleton with TODO
- **Description**: Main service class with TODO calculateQuote() method implementing pricing logic:
  - Load product and zone
  - Apply age factors based on age category
  - Apply zone risk coefficients
  - Calculate final price = baseRate × ageFactor × zoneCoefficient
  - Save quote and return response
  - Handle exceptions for missing products/zones

## Java Classes - Controller

### 17. QuoteController.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/controller/QuoteController.java`
- **Status**: Skeleton with TODO
- **Description**: REST controller with TODO endpoints:
  - POST /api/quotes - Create new quote
  - GET /api/quotes/{id} - Get quote by ID
  - GET /api/quotes - List all quotes with optional filters (productId, minPrice)

## Java Classes - Exception Handling

### 18. GlobalExceptionHandler.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/exception/GlobalExceptionHandler.java`
- **Status**: Skeleton with TODO
- **Description**: @ControllerAdvice class with TODO handlers for:
  - MethodArgumentNotValidException (validation errors)
  - IllegalArgumentException (not found errors)
  - General Exception fallback

## Java Classes - Main Application

### 19. PricingApplication.java
- **Path**: `/backend/src/main/java/com/tendanz/pricing/PricingApplication.java`
- **Status**: Complete (Provided)
- **Description**: Spring Boot main application class with @SpringBootApplication annotation and ObjectMapper bean

## Test Classes

### 20. PricingServiceTest.java
- **Path**: `/backend/src/test/java/com/tendanz/pricing/PricingServiceTest.java`
- **Status**: Skeleton with TODO
- **Description**: Test class with 2 example test methods and TODO comments for additional tests:
  - testCalculateQuoteForAdult()
  - testCalculateQuoteForYoungClient()
  - TODO: Tests for zone coefficients, error cases, edge cases

## Documentation

### 21. README.md
- **Path**: `/backend/README.md`
- **Status**: Complete
- **Description**: Comprehensive project documentation including:
  - Project structure and organization
  - Technology stack details
  - Build and run instructions
  - Database schema documentation
  - API endpoint specifications
  - Business logic explanation
  - Implementation tasks for candidates
  - Testing instructions
  - Submission checklist

## Summary of File Statuses

- **Complete/Provided**: 15 files
  - Can be used as-is
  - Foundation for the project
  - Ready for candidates to build upon

- **Skeleton with TODO**: 6 files
  - Structure in place
  - Clear TODO comments marking implementation points
  - Candidates must complete these
  - Project compiles but doesn't fully function until completed

## Key Features

### For Provided Files:
- Full JPA entity definitions with annotations
- Proper DTOs with validation
- Repository interfaces ready to use
- Spring configuration complete
- Database schema with proper relationships
- Test data included

### For TODO Files:
- Clear structure and method signatures
- TODO comments indicating what needs to be implemented
- Example implementations or scaffolding
- Integration points already set up
- Candidates can complete independently

## Project Flow

1. Candidate receives the project
2. Runs `mvn clean install` - should compile successfully
3. Runs `mvn spring-boot:run` - application starts
4. Tests fail due to unimplemented TODOs
5. Candidate implements TODO items:
   - Business logic in PricingService.calculateQuote()
   - REST endpoints in QuoteController
   - Repository methods in QuoteRepository
   - Exception handling in GlobalExceptionHandler
   - Add test cases in PricingServiceTest
6. All tests pass and API works correctly

## Testing the Project

After candidates complete the TODOs, they should be able to:
- Build the project: `mvn clean install`
- Run tests: `mvn test`
- Start the application: `mvn spring-boot:run`
- Call API endpoints with proper pricing calculations
- Handle errors gracefully
