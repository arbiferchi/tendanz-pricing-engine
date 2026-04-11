/**
 * Product interface - represents an insurance product.
 * Must match the backend Product entity fields.
 */
export interface Product {
  id: number;
  name: string;
  description: string;
  baseRate?: number;
  ageFactorYoung?: number;
  ageFactorAdult?: number;
  ageFactorSenior?: number;
  ageFactorElderly?: number;
  createdAt?: string; // ISO timestamp
}

/**
 * Paginated response structure for products.
 */
export interface PaginatedProductResponse {
  content: Product[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

/**
 * Product creation request structure required for saving/updating a 4th product
 */
export interface ProductCreationRequest {
  name: string;
  description: string;
  baseRate: number;
  ageFactorYoung: number;
  ageFactorAdult: number;
  ageFactorSenior: number;
  ageFactorElderly: number;
}
