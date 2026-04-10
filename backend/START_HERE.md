# Pricing Engine - START HERE

Welcome to the Pricing Engine technical test for Full Stack Java Spring Boot + Angular candidates at Tendanz Group.

## Quick Navigation

### For Candidates - First Time Setup

1. **Read the Overview** (5 min)
   - [README.md](README.md) - Complete project documentation

2. **Understand the Requirements** (15 min)
   - [TODO_CHECKLIST.md](TODO_CHECKLIST.md) - Detailed implementation tasks
   - [PROJECT_SUMMARY.txt](PROJECT_SUMMARY.txt) - Quick reference guide

3. **Build and Run** (10 min)
   ```bash
   cd /sessions/stoic-peaceful-hopper/mnt/Recrutement/test-technique-fullstack/backend
   mvn clean install
   mvn spring-boot:run
   ```

4. **Start Implementing** (5-8 hours)
   - Follow the implementation roadmap in [TODO_CHECKLIST.md](TODO_CHECKLIST.md)
   - Refer to TODO comments in skeleton files
   - Test frequently with `mvn test`

5. **Submit**
   - Ensure all TODOs are completed
   - All tests pass
   - Use [VERIFICATION_REPORT.md](VERIFICATION_REPORT.md) checklist

---

## Documentation Index

### Main Documentation
| File | Size | Purpose |
|------|------|---------|
| [README.md](README.md) | 6.4 KB | Complete project overview and getting started guide |
| [TODO_CHECKLIST.md](TODO_CHECKLIST.md) | 12 KB | Detailed implementation requirements and tasks |
| [PROJECT_SUMMARY.txt](PROJECT_SUMMARY.txt) | 11 KB | Quick reference guide and key information |
| [FILES_CREATED.md](FILES_CREATED.md) | 8.1 KB | Complete file inventory with descriptions |
| [VERIFICATION_REPORT.md](VERIFICATION_REPORT.md) | 12 KB | Project verification and validation checklist |

### Configuration Files
| File | Purpose |
|------|---------|
| `pom.xml` | Maven configuration with Spring Boot 3.2, Java 17 |
| `src/main/resources/application.yml` | Spring application configuration |
| `src/main/resources/schema.sql` | Database schema (DDL) |
| `src/main/resources/data.sql` | Initial test data |

### Java Classes

#### Complete Files (Ready to Use)
- `src/main/java/com/tendanz/pricing/PricingApplication.java` - Main Spring Boot app
- `src/main/java/com/tendanz/pricing/entity/Zone.java` - Zone entity
- `src/main/java/com/tendanz/pricing/entity/Product.java` - Product entity
- `src/main/java/com/tendanz/pricing/entity/PricingRule.java` - Pricing rule entity
- `src/main/java/com/tendanz/pricing/dto/QuoteRequest.java` - Request DTO with validation
- `src/main/java/com/tendanz/pricing/dto/QuoteResponse.java` - Response DTO
- `src/main/java/com/tendanz/pricing/repository/ProductRepository.java` - Product repository
- `src/main/java/com/tendanz/pricing/repository/ZoneRepository.java` - Zone repository
- `src/main/java/com/tendanz/pricing/repository/PricingRuleRepository.java` - Pricing rule repository
- `src/main/java/com/tendanz/pricing/enums/AgeCategory.java` - Age category enum

#### Skeleton Files (TODO - For Implementation)
- `src/main/java/com/tendanz/pricing/service/PricingService.java` - **HIGH PRIORITY**
  - Implement `calculateQuote()` method
- `src/main/java/com/tendanz/pricing/controller/QuoteController.java` - **HIGH PRIORITY**
  - Implement POST, GET, and LIST endpoints
- `src/main/java/com/tendanz/pricing/exception/GlobalExceptionHandler.java` - **MEDIUM PRIORITY**
  - Implement exception handlers
- `src/main/java/com/tendanz/pricing/repository/QuoteRepository.java` - **MEDIUM PRIORITY**
  - Implement custom query methods
- `src/main/java/com/tendanz/pricing/entity/Quote.java` - **LOW PRIORITY**
  - Verify entity configuration
- `src/test/java/com/tendanz/pricing/PricingServiceTest.java` - **LOW PRIORITY**
  - Add comprehensive tests

---

## Key Information at a Glance

### Project Structure
```
backend/
├── pom.xml                          # Maven config
├── src/
│   ├── main/
│   │   ├── java/com/tendanz/pricing/
│   │   │   ├── controller/          # REST API (TODO)
│   │   │   ├── service/             # Business logic (TODO)
│   │   │   ├── repository/          # Data access
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   └── exception/           # Error handling (TODO)
│   │   └── resources/               # Config & SQL files
│   └── test/
│       └── java/                    # Unit tests (TODO)
├── README.md                        # Full documentation
├── TODO_CHECKLIST.md                # Implementation tasks
└── PROJECT_SUMMARY.txt              # Quick reference
```

### Technology Stack
- **Java 17** & **Spring Boot 3.2.0**
- **Spring Data JPA** & **H2 Database**
- **Maven**, **Lombok**, **JUnit 5**

### Database
**4 Tables**: zone, product, pricing_rule, quote
**Test Data**: 3 zones, 3 products, 3 pricing rules

### Pricing Formula
```
Final Price = Base Rate × Age Factor × Zone Risk Coefficient

Age Factors:
  YOUNG (18-24):    1.3
  ADULT (25-45):    1.0
  SENIOR (46-65):   1.2
  ELDERLY (66+):    1.5

Zone Coefficients:
  Tunis (TN-TUN):   1.0
  Sfax (TN-SFX):    1.15
  Sousse (TN-SUS):  1.08
```

### API Endpoints
```
POST   /api/quotes              # Create quote
GET    /api/quotes/{id}         # Get quote by ID
GET    /api/quotes              # List quotes with optional filtering
```

---

## Getting Started (5 Steps)

### 1. Build the Project
```bash
cd /sessions/stoic-peaceful-hopper/mnt/Recrutement/test-technique-fullstack/backend
mvn clean install
```

### 2. Run the Application
```bash
mvn spring-boot:run
```

### 3. Access the Application
- **API**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console

### 4. Run Tests
```bash
mvn test
```

### 5. Start Implementing
- Follow [TODO_CHECKLIST.md](TODO_CHECKLIST.md)
- Implement in priority order (High → Medium → Low)
- Test after each implementation
- Refer to TODO comments in code

---

## Implementation Roadmap

### Phase 1: Service Layer (HIGH PRIORITY - 1-2 hours)
- [ ] Implement `PricingService.calculateQuote()`
- [ ] Test: Unit tests for pricing logic

### Phase 2: API Layer (HIGH PRIORITY - 1-2 hours)
- [ ] Implement `QuoteController.createQuote()` (POST)
- [ ] Implement `QuoteController.getQuote()` (GET by ID)
- [ ] Implement `QuoteController.getAllQuotes()` (GET with filtering)
- [ ] Test: Manual API testing

### Phase 3: Error Handling (MEDIUM PRIORITY - 30-45 min)
- [ ] Implement `GlobalExceptionHandler` methods
- [ ] Test: Error scenario validation

### Phase 4: Data Access (MEDIUM PRIORITY - 15-30 min)
- [ ] Implement `QuoteRepository` custom methods
- [ ] Test: Repository method validation

### Phase 5: Testing (MEDIUM PRIORITY - 1-2 hours)
- [ ] Add comprehensive test cases
- [ ] Test coverage: 80%+

### Phase 6: Refinement (LOW PRIORITY - 30-45 min)
- [ ] Code review
- [ ] Final testing

**Total Time**: 5-8 hours

---

## Important Notes

### Code Quality
- Use `BigDecimal` for all monetary calculations (never `double`)
- Round to 2 decimal places with `RoundingMode.HALF_UP`
- Follow Spring Boot best practices
- Write clean, professional code

### Testing
- Run tests frequently: `mvn test`
- Add meaningful test cases
- Test both success and failure scenarios

### Submission Checklist
- [ ] Project compiles without errors
- [ ] All unit tests pass
- [ ] Application starts successfully
- [ ] No TODO comments remain in code
- [ ] All 3 API endpoints work correctly
- [ ] Error handling works properly
- [ ] Input validation is enforced
- [ ] Pricing calculations are correct
- [ ] Code is clean and professional

---

## Support & Resources

### For Understanding the Project
1. Read [README.md](README.md) for complete overview
2. Check [PROJECT_SUMMARY.txt](PROJECT_SUMMARY.txt) for quick reference
3. Review [FILES_CREATED.md](FILES_CREATED.md) for file descriptions

### For Implementation Help
1. Follow [TODO_CHECKLIST.md](TODO_CHECKLIST.md) for detailed tasks
2. Review TODO comments in skeleton files
3. Check example implementations in complete files

### For Verification
1. Use [VERIFICATION_REPORT.md](VERIFICATION_REPORT.md) checklist
2. Run `mvn clean install` to verify build
3. Run `mvn test` to verify tests pass
4. Test API endpoints manually

---

## FAQ

**Q: Can I start without reading documentation?**
A: No. Please read README.md first (5 min) to understand the project structure.

**Q: What should I implement first?**
A: Follow the priority order in TODO_CHECKLIST.md. Start with PricingService.calculateQuote().

**Q: How do I test my implementation?**
A: Use `mvn test` for unit tests and curl/Postman for API endpoints.

**Q: What if I get stuck?**
A: Check the TODO comments in skeleton files and review the provided complete files for patterns.

**Q: Can I modify the complete files?**
A: Only if necessary. Focus on implementing TODO items in skeleton files.

---

## Project Status

**Status**: COMPLETE AND READY FOR CANDIDATES

All foundation files are complete:
- Maven configuration
- Database schema and test data
- JPA entities and repositories
- DTOs with validation
- Project structure

TODO items are clearly marked:
- Service layer business logic
- REST API endpoints
- Exception handling
- Additional test cases

---

## Next Steps

1. **NOW**: Read this file and [README.md](README.md)
2. **NEXT**: Review [TODO_CHECKLIST.md](TODO_CHECKLIST.md)
3. **THEN**: Build and run the project (`mvn clean install && mvn spring-boot:run`)
4. **START**: Implement according to the roadmap

Good luck with the implementation!

---

*For more details, see [README.md](README.md), [TODO_CHECKLIST.md](TODO_CHECKLIST.md), or [PROJECT_SUMMARY.txt](PROJECT_SUMMARY.txt)*
