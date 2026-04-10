# Pricing Engine Frontend - Angular Test Project

This is a skeleton Angular 17 frontend for a pricing engine test project. The structure and imports are in place, but you (the candidate) must implement the core functionality.

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── pages/
│   │   │   ├── quote-form/          # Create new quote form
│   │   │   ├── quote-list/          # List all quotes with filtering
│   │   │   └── quote-detail/        # Display single quote details
│   │   ├── services/
│   │   │   ├── quote.service.ts     # TODO: Quote API calls
│   │   │   └── product.service.ts   # TODO: Product API calls
│   │   ├── models/
│   │   │   ├── quote.model.ts       # Quote request/response interfaces
│   │   │   └── product.model.ts     # Product interface
│   │   ├── app.component.ts         # Root component (navbar)
│   │   └── app.routes.ts            # Route configuration
│   ├── environments/
│   │   ├── environment.ts           # Development API URL
│   │   └── environment.prod.ts      # Production API URL
│   ├── main.ts                      # Bootstrap
│   ├── index.html                   # HTML shell
│   └── styles.css                   # Global styles
├── angular.json                      # Angular CLI config
├── tsconfig.json                     # TypeScript config
└── package.json                      # Dependencies
```

## What You Need to Implement

### 1. Services

#### `src/app/services/quote.service.ts`
Implement these methods:
- **`createQuote(request: QuoteRequest): Observable<QuoteResponse>`**
  - POST to `/api/quotes`
  - Send quote request data
  - Return calculated quote response

- **`getQuote(id: number): Observable<QuoteResponse>`**
  - GET from `/api/quotes/:id`
  - Return single quote details

- **`getQuotes(filters?: {productId?: number, minPrice?: number}): Observable<QuoteResponse[]>`**
  - GET from `/api/quotes`
  - Support optional query parameters for filtering
  - Return array of quotes

#### `src/app/services/product.service.ts`
Implement:
- **`getProducts(): Observable<Product[]>`**
  - GET from `/api/products`
  - Return array of available products

### 2. Components

#### Quote Form (`src/app/pages/quote-form/`)
- [ ] Initialize reactive form with FormGroup containing:
  - `customerName` (required)
  - `email` (required, email format)
  - `phone` (required)
  - `productId` (required, number)
  - `zone` (required, from: Tunis, Sfax, Sousse)
  - `age` (required, 18-100)
  - `insuredAmount` (required, min 1000)
  - `startDate` (required, date format)
  - `duration` (required, min 1 month)

- [ ] Load products from ProductService on init
- [ ] Implement `onSubmit()` method:
  - Validate form
  - Call `quoteService.createQuote()`
  - Show success/error message
  - Navigate to quote detail on success
- [ ] Display validation errors
- [ ] Show loading state during submission

#### Quote List (`src/app/pages/quote-list/`)
- [ ] Load all quotes on component init
- [ ] Implement filtering:
  - Filter by product (dropdown)
  - Filter by minimum price
  - Call service with filters
- [ ] Implement sorting:
  - Sort by date (created date)
  - Sort by price (final price)
  - Support ascending/descending
- [ ] Display quotes in table with:
  - ID, Customer Name, Product, Zone, Final Price, Created Date
  - Make rows clickable to view detail
- [ ] Show loading state
- [ ] Show error message if API fails
- [ ] Show empty state if no quotes

#### Quote Detail (`src/app/pages/quote-detail/`)
- [ ] Get quote ID from route parameters
- [ ] Load quote details from service
- [ ] Display:
  - Customer information
  - Insurance details
  - Coverage period
  - Applied pricing rules
  - Pricing breakdown with calculations
- [ ] Show loading state
- [ ] Show error message if quote not found
- [ ] Provide back button to quote list

## API Contract

### Quote Request DTO
```typescript
{
  customerName: string,
  email: string,
  phone: string,
  productId: number,
  zone: string,
  age: number,
  insuredAmount: number,
  startDate: string,      // ISO format: YYYY-MM-DD
  duration: number        // months
}
```

### Quote Response DTO
```typescript
{
  id: number,
  customerName: string,
  email: string,
  phone: string,
  productId: number,
  productName: string,
  zone: string,
  age: number,
  insuredAmount: number,
  startDate: string,
  duration: number,
  basePrice: number,
  ageModifier: number,    // percentage (e.g., 10 for +10%)
  zoneModifier: number,   // percentage
  appliedRules: string[], // e.g., ["AgeGroup25-35", "ZoneCoastal"]
  finalPrice: number,
  createdAt: string,      // ISO timestamp
  updatedAt: string
}
```

### Product DTO
```typescript
{
  id: number,
  name: string,
  description: string,
  basePrice: number,      // per month
  minAge: number,
  maxAge: number,
  active: boolean,
  createdAt: string,
  updatedAt: string
}
```

## Backend Connection

- **Development API URL**: `http://localhost:8080/api`
- **Base endpoints**:
  - GET `/api/quotes` - List all quotes
  - POST `/api/quotes` - Create new quote
  - GET `/api/quotes/:id` - Get single quote
  - GET `/api/products` - List products

## Running the Project

```bash
# Install dependencies
npm install

# Run development server
npm start
# App will be available at http://localhost:4200

# Build for production
npm run build

# Run tests (if implemented)
npm test
```

## Styling

The project includes:
- Global CSS variables for Tendanz branding (Tendanz blue: #1F3864)
- Responsive grid and flexbox layouts
- Form styling with validation states
- Loading spinners and alerts
- Professional, clean design

## Key Points

1. **All imports are in place** - Components, services, and modules are already imported
2. **Interfaces are defined** - Use the models from `src/app/models/`
3. **Routing is configured** - Routes are set up in `app.routes.ts`
4. **HttpClient is provided** - Available in main.ts
5. **Environment config is ready** - Use `environment.apiUrl` for base URL
6. **Templates are prepared** - HTML templates have placeholders for your logic

## Error Handling

- Use `catchError` operator from RxJS in services
- Log errors to console for debugging
- Display user-friendly error messages in components
- Handle HTTP errors (4xx, 5xx) appropriately

## Form Validation

The form component already has helper methods:
- `hasError(fieldName: string, errorType: string): boolean`
- `isFieldInvalid(fieldName: string): boolean`
- `getErrorMessage(fieldName: string): string`

Use these to display validation feedback in the template.

## Notes

- The app will compile successfully but won't work until the TODO items are completed
- Focus on implementing the service methods first
- Then implement the component logic for loading and form submission
- Finally, implement filtering and sorting in the list component
- Use Angular best practices: reactive forms, RxJS operators, type safety

Good luck!
