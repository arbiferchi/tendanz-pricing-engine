# 🧮 Frontend — Tendanz Pricing Engine

> An Angular 17 standalone application providing the full administration interface for managing insurance products, configuring age-tier pricing rules, and executing real-time quote calculations. Built for internal actuarial and operations teams.

---

## 📌 Table of Contents

- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation & Local Setup](#-installation--local-setup)
- [Environment Configuration](#-environment-configuration)
- [Folder Structure](#-folder-structure)
- [Pages & Routes](#-pages--routes)
- [Components Documentation](#-components-documentation)
- [Services & API Integration](#-services--api-integration)
- [Models & Data Contracts](#-models--data-contracts)
- [State Management](#-state-management)
- [Styling & Design System](#-styling--design-system)
- [Forms & Validation](#-forms--validation)
- [Error Handling](#-error-handling)
- [Testing](#-testing)
- [Build & Scripts](#-build--scripts)
- [Deployment](#-deployment)
- [Known Limitations & Roadmap](#-known-limitations--roadmap)

---

## 🛠 Tech Stack

| Category | Technology | Version | Purpose |
|---|---|---|---|
| Framework | Angular | 17 | Component lifecycle, DI, routing |
| Language | TypeScript | 5.x | Type-safe application code |
| Async / Reactivity | RxJS | 7.x | Observable pipelines, event streams |
| HTTP Client | @angular/common/http | 17 | REST API communication |
| Routing | @angular/router | 17 | SPA navigation, lazy loading |
| Forms | @angular/forms | 17 | Reactive forms, validators |
| Styling | Vanilla CSS + CSS Variables | — | Design token system, responsive layout |
| Build Tool | Angular CLI / Webpack | 17 | Bundling, tree-shaking, dev server |
| Package Manager | npm | 9+ | Dependency management |

> **Architecture Note:** This application uses Angular 17's **Standalone Components API** exclusively. There are no `NgModule` declarations. Every component, pipe, and directive is self-contained and imported directly where needed.

---

## ✅ Prerequisites

Ensure the following tools are installed before setting up the project:

| Tool | Minimum Version | Check Command |
|---|---|---|
| Node.js | 18.x LTS | `node --version` |
| npm | 9.x | `npm --version` |
| Angular CLI | 17.x | `ng version` |
| Java Backend | Running on `localhost:8080` | See `/backend/README.md` |

Install Angular CLI globally if not present:

```bash
npm install -g @angular/cli@17
```

---

## 🚀 Installation & Local Setup

```bash
# 1. Navigate to the frontend directory
cd frontend

# 2. Install all dependencies
npm install

# 3. Start the development server (default port: 4200)
ng serve

# 4. Open in browser
http://localhost:4200
```

> The application proxies API calls to the Java backend. Ensure the backend is running before starting the frontend. See [Environment Configuration](#-environment-configuration) for proxy setup.

---

## ⚙️ Environment Configuration

### `src/environments/environment.ts` (Development)

```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api'
};
```

### `src/environments/environment.prod.ts` (Production)

```typescript
export const environment = {
  production: true,
  apiBaseUrl: 'https://api.tendanz.com/api'
};
```

### Proxy Configuration (`proxy.conf.json`)

API calls prefixed with `/api` are proxied to the backend during development to avoid CORS issues:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

This proxy is referenced in `angular.json` under `serve > options > proxyConfig`.

---

## 📁 Folder Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── models/                          # TypeScript interfaces — shared data contracts
│   │   │   ├── product.model.ts             # Product, AgeTierRule, PricingBand interfaces
│   │   │   ├── quote-request.model.ts       # QuoteRequest DTO sent to backend
│   │   │   └── quote-response.model.ts      # QuoteResponse DTO received from backend
│   │   │
│   │   ├── services/                        # Angular injectable services (API wrappers)
│   │   │   ├── product.service.ts           # CRUD operations for /api/products
│   │   │   └── quote.service.ts             # Quote submission and retrieval /api/quotes
│   │   │
│   │   ├── pages/                           # Route-level standalone components
│   │   │   ├── product-list/
│   │   │   │   ├── product-list.component.ts
│   │   │   │   ├── product-list.component.html
│   │   │   │   └── product-list.component.css
│   │   │   ├── product-detail/
│   │   │   │   ├── product-detail.component.ts
│   │   │   │   ├── product-detail.component.html
│   │   │   │   └── product-detail.component.css
│   │   │   ├── product-form/                # Create new product
│   │   │   │   ├── product-form.component.ts
│   │   │   │   ├── product-form.component.html
│   │   │   │   └── product-form.component.css
│   │   │   ├── product-update/              # Edit existing product
│   │   │   │   ├── product-update.component.ts
│   │   │   │   ├── product-update.component.html
│   │   │   │   └── product-update.component.css
│   │   │   ├── quote-list/
│   │   │   │   ├── quote-list.component.ts
│   │   │   │   ├── quote-list.component.html
│   │   │   │   └── quote-list.component.css
│   │   │   ├── quote-detail/
│   │   │   │   ├── quote-detail.component.ts
│   │   │   │   ├── quote-detail.component.html
│   │   │   │   └── quote-detail.component.css
│   │   │   ├── quote-form/                  # Real-time quote calculator
│   │   │   │   ├── quote-form.component.ts
│   │   │   │   ├── quote-form.component.html
│   │   │   │   └── quote-form.component.css
│   │   │   ├── quote-update/                # Edit and recalculate an existing quote
│   │   │   │   ├── quote-update.component.ts
│   │   │   │   ├── quote-update.component.html
│   │   │   │   └── quote-update.component.css
│   │   │   └── quote-history/               # Historical quote audit log
│   │   │       ├── quote-history.component.ts
│   │   │       ├── quote-history.component.html
│   │   │       └── quote-history.component.css
│   │   │
│   │   ├── app.component.ts                 # Root standalone component — shell layout
│   │   ├── app.component.html               # Navigation bar + <router-outlet>
│   │   ├── app.component.css                # Global layout styles
│   │   └── app.routes.ts                    # Centralized route definitions
│   │
│   ├── environments/
│   │   ├── environment.ts                   # Dev config (apiBaseUrl, feature flags)
│   │   └── environment.prod.ts              # Production config
│   │
│   ├── assets/                              # Static files (images, icons, fonts)
│   ├── styles.css                           # Global CSS — CSS variable declarations, resets
│   └── main.ts                              # Angular bootstrap entry point
│
├── angular.json                             # Angular workspace configuration (build, serve, test targets)
├── tsconfig.json                            # TypeScript base configuration
├── tsconfig.app.json                        # App-specific TS config
├── proxy.conf.json                          # Dev proxy routing /api → backend
└── package.json                             # npm dependencies and CLI scripts
```

---

## 🗺 Pages & Routes

All routes are defined in `src/app/app.routes.ts`.

### Route Table

| Path | Component | Auth | Description |
|---|---|---|---|
| `/` | — | — | Redirects to `/quotes` |
| `/quotes` | `QuoteListComponent` | — | Paginated list of all computed quotes with filters |
| `/quotes/new` | `QuoteFormComponent` | — | Interactive form for real-time quote calculation |
| `/quotes/:id` | `QuoteDetailComponent` | — | Read-only breakdown of a specific computed quote |
| `/quotes/:id/edit` | `QuoteUpdateComponent` | — | Modify inputs and recalculate an existing quote |
| `/quotes/history` | `QuoteHistoryComponent` | — | Audit log of quote versions and recalculations |
| `/products` | `ProductListComponent` | — | Table of all insurance base products |
| `/products/new` | `ProductFormComponent` | — | Create and configure a new insurance product |
| `/products/:id` | `ProductDetailComponent` | — | Full product detail with age-tier pricing breakdown |
| `/products/:id/edit` | `ProductUpdateComponent` | — | Modify product rules and sync DTOs to backend |

### Route Definition (excerpt from `app.routes.ts`)

```typescript
export const routes: Routes = [
  { path: '', redirectTo: 'quotes', pathMatch: 'full' },
  { path: 'quotes', component: QuoteListComponent },
  { path: 'quotes/new', component: QuoteFormComponent },
  { path: 'quotes/history', component: QuoteHistoryComponent },
  { path: 'quotes/:id', component: QuoteDetailComponent },
  { path: 'quotes/:id/edit', component: QuoteUpdateComponent },
  { path: 'products', component: ProductListComponent },
  { path: 'products/new', component: ProductFormComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: 'products/:id/edit', component: ProductUpdateComponent },
];
```

> **Route ordering matters:** `/quotes/new` and `/quotes/history` must be declared **before** `/quotes/:id` to prevent Angular's router from matching the literal strings `new` and `history` as IDs.

---

## 🧩 Components Documentation

### `QuoteFormComponent` — `/quotes/new`

The core interactive component of the application. Users select demographic inputs and receive a real-time premium calculation.

**Key Responsibilities:**
- Renders a reactive form with product selection, age input, coverage type, and region fields.
- On form change, submits a `QuoteRequest` DTO to `QuoteService.calculate()`.
- Displays a `QuoteResponse` breakdown including base premium, age-tier multiplier applied, and final price.
- Allows saving the computed quote to persistent storage.

**API Calls:** `POST /api/quotes/calculate`, `POST /api/quotes`

---

### `QuoteListComponent` — `/quotes`

Displays all previously computed and saved quotes in a paginated, filterable table.

**Key Responsibilities:**
- Fetches all quotes via `QuoteService.getAll()` on init.
- Supports client-side filtering by date range, product, and status.
- Provides PDF export functionality per quote row.
- Navigates to `/quotes/:id` for detail view.

**API Calls:** `GET /api/quotes`

---

### `QuoteDetailComponent` — `/quotes/:id`

Read-only view of a saved quote.

**Key Responsibilities:**
- Reads `:id` from `ActivatedRoute`.
- Fetches quote details via `QuoteService.getById(id)`.
- Renders the full breakdown: input parameters, applied multiplier, base price, final premium.
- Provides navigation to edit mode (`/quotes/:id/edit`).

**API Calls:** `GET /api/quotes/:id`

---

### `QuoteUpdateComponent` — `/quotes/:id/edit`

Allows modification of an existing quote's input parameters and recalculation.

**Key Responsibilities:**
- Pre-populates form with existing `QuoteRequest` data.
- On submit, sends updated `QuoteRequest` to `QuoteService.update(id, dto)`.
- Replaces the existing quote with the recalculated result.

**API Calls:** `GET /api/quotes/:id`, `PUT /api/quotes/:id`

---

### `QuoteHistoryComponent` — `/quotes/history`

Audit log showing version history and recalculations for quotes.

**API Calls:** `GET /api/quotes/history`

---

### `ProductListComponent` — `/products`

Displays all configured insurance base products.

**Key Responsibilities:**
- Fetches product catalog from `ProductService.getAll()`.
- Renders a table with product name, base price, coverage type, and active status.
- Links to detail and edit views.

**API Calls:** `GET /api/products`

---

### `ProductDetailComponent` — `/products/:id`

Detailed view of a single product including its full age-tier pricing matrix.

**Key Responsibilities:**
- Fetches product by ID.
- Renders the full `AgeTierRule[]` array as a pricing table (age range → multiplier → computed price).

**API Calls:** `GET /api/products/:id`

---

### `ProductFormComponent` — `/products/new`

Form for creating a new insurance product with base pricing and age-tier rules.

**Key Responsibilities:**
- Reactive form with dynamic `FormArray` for adding/removing age-tier rules.
- Submits `ProductDTO` to `ProductService.create()`.
- Redirects to product list on success.

**API Calls:** `POST /api/products`

---

### `ProductUpdateComponent` — `/products/:id/edit`

Modifies an existing product's configuration and syncs back to the Java backend.

**Key Responsibilities:**
- Pre-populates form (including nested age-tier `FormArray`) from existing product data.
- Submits updated DTO via `ProductService.update(id, dto)`.

**API Calls:** `GET /api/products/:id`, `PUT /api/products/:id`

---

## 🌐 Services & API Integration

All HTTP communication is encapsulated in injectable services located in `src/app/services/`. Components never call `HttpClient` directly.

### `ProductService` (`product.service.ts`)

```typescript
@Injectable({ providedIn: 'root' })
export class ProductService {
  getAll(): Observable<Product[]>           // GET  /api/products
  getById(id: number): Observable<Product>  // GET  /api/products/:id
  create(dto: ProductDTO): Observable<Product>          // POST /api/products
  update(id: number, dto: ProductDTO): Observable<Product> // PUT /api/products/:id
  delete(id: number): Observable<void>      // DELETE /api/products/:id
}
```

### `QuoteService` (`quote.service.ts`)

```typescript
@Injectable({ providedIn: 'root' })
export class QuoteService {
  getAll(): Observable<QuoteResponse[]>                         // GET  /api/quotes
  getById(id: number): Observable<QuoteResponse>                // GET  /api/quotes/:id
  getHistory(): Observable<QuoteResponse[]>                     // GET  /api/quotes/history
  calculate(req: QuoteRequest): Observable<QuoteResponse>       // POST /api/quotes/calculate
  save(req: QuoteRequest): Observable<QuoteResponse>            // POST /api/quotes
  update(id: number, req: QuoteRequest): Observable<QuoteResponse> // PUT /api/quotes/:id
}
```

### HTTP Interceptors

| Interceptor | Purpose |
|---|---|
| `ErrorInterceptor` | Catches HTTP errors globally, maps status codes to user-facing messages |
| `LoadingInterceptor` | Toggles a global loading state during in-flight requests |

---

## 📐 Models & Data Contracts

All interfaces are located in `src/app/models/` and mirror backend DTOs exactly.

### `Product`

```typescript
export interface Product {
  id: number;
  name: string;
  description: string;
  basePremium: number;
  coverageType: 'BASIC' | 'STANDARD' | 'PREMIUM';
  isActive: boolean;
  ageTierRules: AgeTierRule[];
}

export interface AgeTierRule {
  id: number;
  minAge: number;
  maxAge: number;
  multiplier: number;         // Applied to basePremium: finalPrice = basePremium * multiplier
}
```

### `QuoteRequest`

```typescript
export interface QuoteRequest {
  productId: number;
  insuredAge: number;
  coverageType: 'BASIC' | 'STANDARD' | 'PREMIUM';
  region: string;
  effectiveDate: string;      // ISO 8601 format: "YYYY-MM-DD"
}
```

### `QuoteResponse`

```typescript
export interface QuoteResponse {
  id: number;
  productName: string;
  insuredAge: number;
  coverageType: string;
  region: string;
  basePremium: number;
  appliedMultiplier: number;
  finalPremium: number;
  calculatedAt: string;       // ISO 8601 timestamp
  status: 'DRAFT' | 'SAVED' | 'EXPIRED';
}
```

---

## 🗃️ State Management

State is managed at the component level using RxJS. There is no centralized store (no NgRx, no Signals store).

### Pattern: `takeUntil(destroy$)`

Every component that subscribes to an Observable implements the destroy guard pattern to prevent memory leaks:

```typescript
@Component({ standalone: true, ... })
export class ExampleComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.quoteService.getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe(quotes => this.quotes = quotes);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

### Data Flow Overview

```
User Interaction
      │
      ▼
Component (Reactive Form / Template Event)
      │
      ▼
Service (ProductService / QuoteService)
      │  Observable<T>
      ▼
HttpClient → Backend REST API
      │
      ▼
Response mapped to Model interface
      │
      ▼
Component updates local state → Template re-renders
```

---

## 🎨 Styling & Design System

Styling is implemented in Vanilla CSS using a centralized CSS Variable system defined in `src/styles.css`.

### CSS Design Tokens

```css
:root {
  /* Brand Colors */
  --primary-color: #1a73e8;
  --primary-hover: #1558b0;
  --secondary-color: #34a853;
  --danger-color: #d93025;
  --warning-color: #f9ab00;

  /* Neutral Palette */
  --background: #f8f9fa;
  --surface: #ffffff;
  --border: #dadce0;
  --text-primary: #202124;
  --text-secondary: #5f6368;

  /* Spacing Scale */
  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;
  --spacing-2xl: 48px;

  /* Typography */
  --font-family: 'Segoe UI', system-ui, sans-serif;
  --font-size-sm: 0.875rem;
  --font-size-base: 1rem;
  --font-size-lg: 1.125rem;
  --font-size-xl: 1.5rem;

  /* Elevation */
  --shadow-sm: 0 1px 3px rgba(0,0,0,0.12);
  --shadow-md: 0 4px 12px rgba(0,0,0,0.1);

  /* Border Radius */
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;
}
```

### Layout Patterns

- **Card Grid:** Responsive CSS Grid with `auto-fill` columns for product/quote listing pages.
- **Data Tables:** Standardized `<table>` layout with consistent row hover states, sortable headers, and sticky first column on mobile.
- **Form Layout:** Single-column stacked form fields with clear label + input + error message grouping.

---

## 📝 Forms & Validation

All forms use Angular **Reactive Forms** (`ReactiveFormsModule`).

### Validation Rules

| Field | Rules |
|---|---|
| `productId` | Required, must be a valid product ID |
| `insuredAge` | Required, numeric, min: 0, max: 120 |
| `coverageType` | Required, one of: BASIC, STANDARD, PREMIUM |
| `region` | Required, non-empty string |
| `effectiveDate` | Required, valid ISO date, not in the past |
| `basePremium` | Required, numeric, min: 0.01 |
| `multiplier` (AgeTierRule) | Required, numeric, min: 0.1, max: 10 |
| Age range overlap | Custom validator: age ranges in `ageTierRules` must not overlap |

### Custom Validator: Age Range Overlap

```typescript
export function noOverlappingAgeRanges(control: AbstractControl): ValidationErrors | null {
  const rules: AgeTierRule[] = control.value;
  // checks that no two rules have overlapping [minAge, maxAge] ranges
  // returns { overlappingRanges: true } if conflict detected
}
```

---

## 🔒 Error Handling

| Error Type | Handling Strategy |
|---|---|
| `400 Bad Request` | Form-level error message displayed inline |
| `404 Not Found` | Redirects to list page with toast notification |
| `500 Server Error` | Global error banner displayed at top of page |
| Network Timeout | Retry once automatically, then display error state |
| Empty States | Each list page shows a contextual empty state with CTA |

---

## 🧪 Testing

### Running Tests

```bash
# Run unit tests once
ng test --watch=false

# Run tests in watch mode (development)
ng test

# Run tests with coverage report
ng test --code-coverage
```

### Test Coverage

| Module | Test File | Coverage Scope |
|---|---|---|
| `ProductService` | `product.service.spec.ts` | HTTP calls, error handling, response mapping |
| `QuoteService` | `quote.service.spec.ts` | Calculate, save, update, getAll |
| `QuoteFormComponent` | `quote-form.component.spec.ts` | Form validation, submission, response rendering |
| `ProductFormComponent` | `product-form.component.spec.ts` | Dynamic FormArray, age range validation |

Tests use Angular's `TestBed` with `HttpClientTestingModule` to mock backend responses.

---

## 📦 Build & Scripts

All scripts are defined in `package.json`.

| Script | Command | Description |
|---|---|---|
| Start dev server | `ng serve` | Starts on `http://localhost:4200` with hot reload |
| Production build | `ng build --configuration production` | Outputs to `dist/` with optimizations |
| Run tests | `ng test` | Karma + Jasmine test runner |
| Lint | `ng lint` | ESLint checks across all `.ts` files |
| Generate component | `ng generate component pages/my-page --standalone` | Scaffold new standalone component |

---

## 🐳 Deployment

### Production Build

```bash
# Build optimized production bundle
ng build --configuration production

# Output directory: dist/tendanz-frontend/
```



