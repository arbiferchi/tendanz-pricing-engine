/**
 * Product interface - represents an insurance product.
 * Must match the backend Product entity fields.
 */
export interface Product {
  id: number;
  name: string;
  description: string;
  createdAt: string; // ISO timestamp
}
