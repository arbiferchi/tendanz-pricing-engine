# Implementation Guide - What to Complete

This document outlines exactly what needs to be implemented to complete the frontend test project.

## Quick Checklist

### Services (2 files)

- [ ] **QuoteService** - `src/app/services/quote.service.ts`
  - [ ] `createQuote()` method
  - [ ] `getQuote()` method
  - [ ] `getQuotes()` method with optional filters
  - [ ] Error handling with `catchError`

- [ ] **ProductService** - `src/app/services/product.service.ts`
  - [ ] `getProducts()` method
  - [ ] Error handling

### Components (3 features)

- [ ] **Quote Form** - `src/app/pages/quote-form/`
  - [ ] Form initialization with 9 fields
  - [ ] Product dropdown loading
  - [ ] Form submission logic
  - [ ] Validation and error display
  - [ ] Loading state management
  - [ ] Navigation on success

- [ ] **Quote List** - `src/app/pages/quote-list/`
  - [ ] Load quotes on init
  - [ ] Filtering by product and price
  - [ ] Sorting by date and price
  - [ ] Table rendering
  - [ ] Loading and error states

- [ ] **Quote Detail** - `src/app/pages/quote-detail/`
  - [ ] Load quote by ID from route params
  - [ ] Display all quote information
  - [ ] Show pricing breakdown
  - [ ] Display applied rules

---

## Detailed Implementation Steps

### Step 1: Implement QuoteService

**File**: `src/app/services/quote.service.ts`

```typescript
// TODO: Complete these 3 methods

createQuote(request: QuoteRequest): Observable<QuoteResponse> {
  // 1. POST to ${this.apiUrl}/quotes
  // 2. Send the request body
  // 3. Handle errors with catchError and this.handleError()
  // 4. Return Observable<QuoteResponse>
}

getQuote(id: number): Observable<QuoteResponse> {
  // 1. GET from ${this.apiUrl}/quotes/${id}
  // 2. Handle errors
  // 3. Return Observable<QuoteResponse>
}

getQuotes(filters?: {productId?: number; minPrice?: number}): Observable<QuoteResponse[]> {
  // 1. GET from ${this.apiUrl}/quotes
  // 2. If filters provided, build HttpParams with query parameters
  // 3. Pass params to the request
  // 4. Handle errors
  // 5. Return Observable<QuoteResponse[]>
}
```

**Error handling helper**: Fill in `handleError()` method to log errors and return a user-friendly message.

### Step 2: Implement ProductService

**File**: `src/app/services/product.service.ts`

```typescript
// TODO: Complete this method

getProducts(): Observable<Product[]> {
  // 1. GET from ${this.apiUrl}/products
  // 2. Handle errors with catchError
  // 3. Return Observable<Product[]>
}
```

### Step 3: Implement Quote Form Component

**File**: `src/app/pages/quote-form/quote-form.component.ts`

In `ngOnInit()`:
```typescript
// 1. Load products from ProductService
// 2. Subscribe and populate this.products array
// 3. Handle errors
```

In `onSubmit()`:
```typescript
// 1. Check if form is valid (use this.form.valid)
// 2. Set this.submitted = true (for validation feedback)
// 3. If invalid, return
// 4. Set this.loading = true
// 5. Call this.quoteService.createQuote(this.form.value)
// 6. On success:
//    - Show success message
//    - Navigate to quote detail: this.router.navigate(['/quotes', response.id])
// 7. On error:
//    - Show error message
//    - Set this.loading = false
// 8. Unsubscribe properly (use takeUntilDestroyed or unsubscribe)
```

### Step 4: Implement Quote List Component

**File**: `src/app/pages/quote-list/quote-list.component.ts`

In `ngOnInit()`:
```typescript
// 1. Load products from ProductService
// 2. Load all quotes from QuoteService (no filters initially)
// 3. Set this.loading = true/false appropriately
// 4. Handle errors
```

`applyFilters()` method:
```typescript
// 1. Collect filter values: this.selectedProductId, this.minPrice
// 2. Build filter object only if values are set
// 3. Call this.quoteService.getQuotes(filters)
// 4. Update this.filteredQuotes
// 5. Re-sort if needed
```

`resetFilters()` method:
```typescript
// 1. Clear filter values
// 2. Reload all quotes
```

`changeSortField()` method:
```typescript
// 1. If clicking same field, toggle sortDirection
// 2. Otherwise, set new field and reset direction to 'asc'
// 3. Call this.sortQuotes()
```

`sortQuotes()` (private method):
```typescript
// 1. Sort this.filteredQuotes based on sortField and sortDirection
// 2. For 'date' field: sort by createdAt
// 3. For 'price' field: sort by finalPrice
// 4. For 'asc': use normal sort
// 5. For 'desc': reverse the sort
```

### Step 5: Implement Quote Detail Component

**File**: `src/app/pages/quote-detail/quote-detail.component.ts`

In `ngOnInit()`:
```typescript
// 1. Get quote ID from route params: this.route.snapshot.paramMap.get('id')
// 2. Convert to number
// 3. Call this.quoteService.getQuote(id)
// 4. On success: set this.quote = response
// 5. On error: set this.errorMessage
// 6. Manage this.loading state
```

Helper methods for pricing calculation:
```typescript
getBaseTotal(): number {
  // Return basePrice * duration
}

getAgeModifierImpact(): number {
  // Calculate: (basePrice * duration) * (ageModifier / 100)
}

getZoneModifierImpact(): number {
  // Calculate: (basePrice * duration) * (zoneModifier / 100)
}
```

---

## Implementation Tips

### For Services:
- HttpParams is imported in HttpClient
- Use `.pipe(catchError(...))` for error handling
- Return `throwError(() => new Error(message))` from handleError

### For Components:
- Use `this.loading = true/false` around async operations
- Use `.subscribe()` or `.pipe(takeUntilDestroyed())` for cleanup
- Cast route params to appropriate types
- Date pipe is available: `{{ date | date:'medium' }}`
- Number pipe for currency: `{{ value | number:'1.2-2' }}`

### For Forms:
- FormGroup has `.valid` property
- FormControl has `.hasError()`, `.touched`, `.dirty` methods
- Helper methods in component: `isFieldInvalid()`, `getErrorMessage()`

### For Routing:
- ActivatedRoute provides current route info
- router.navigate() for programmatic navigation
- routerLink directive in templates

### For Templates:
- *ngIf for conditionals
- *ngFor for loops
- [(ngModel)] for two-way binding (FormsModule imported)
- {{ pipe | transformPipe }} for pipes

---

## Testing Your Implementation

1. **Start backend server** (should be running on port 8080)
2. **Install dependencies**: `npm install`
3. **Start dev server**: `npm start`
4. **Navigate to**: http://localhost:4200

### Test Scenarios:
1. Navigate to `/quotes` - should load and display list
2. Click "New Quote" - should load form with product dropdown
3. Fill form and submit - should create quote and navigate to detail
4. View quote detail - should show all pricing information
5. Try filters on list - should filter and sort results

---

## Common Mistakes to Avoid

1. **Forgetting to unsubscribe** - Use `takeUntilDestroyed()` or manual unsubscribe
2. **Not handling errors** - Always use `catchError` in services
3. **Not setting loading states** - Users need feedback during API calls
4. **Forgetting route parameters** - Use `ActivatedRoute.snapshot.paramMap`
5. **Form validation not working** - Remember to check `this.submitted` for error display
6. **Dates not formatting** - Use the `date` pipe in templates
7. **Prices not showing decimals** - Use `number:'1.2-2'` pipe for currency

---

## Success Criteria

The application should:
- [ ] Compile without errors (`ng build` succeeds)
- [ ] Load list of quotes from API
- [ ] Create new quotes through form submission
- [ ] Display quote details with pricing breakdown
- [ ] Filter quotes by product and price
- [ ] Sort quotes by date and price
- [ ] Handle loading states visually
- [ ] Display error messages appropriately
- [ ] Navigate between pages correctly
- [ ] Validate form inputs

Good luck!
