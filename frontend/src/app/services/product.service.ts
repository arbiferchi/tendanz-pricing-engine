import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Product, PaginatedProductResponse, ProductCreationRequest } from '../models/product.model';

/**
 * Service for managing products (insurance products).
 * Handles API communication related to product catalog rendering and modifications.
 */
@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly apiUrl = environment.apiUrl;
  private readonly endpoint = '/products';

  constructor(private http: HttpClient) {}

  /**
   * Retrieves all available products, accounting for pagination.
   *
   * @param params Filtering and pagination params mappings.
   * @returns Observable of paginated responses or arrays depending on response.
   */
  getProducts(): Observable<Product[]> {
    return this.http.get<PaginatedProductResponse | Product[]>(`${this.apiUrl}${this.endpoint}`).pipe(
      map(response => {
        // Unpack backend paginated instances intelligently.
        if ('content' in response) {
          return response.content;
        }
        return response;
      }),
      catchError(error => this.handleError(error, 'load products'))
    );
  }

  /**
   * Recovers a single specific product by its unique ID.
   * 
   * @param id The requested product identifier.
   * @returns The targeted object populated.
   */
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}${this.endpoint}/${id}`).pipe(
      catchError(error => this.handleError(error, 'load product details'))
    );
  }

  /**
   * Creates a new dynamic product tracking rules globally payload binding.
   * 
   * @param request Data to form base rate rules alongside definition.
   * @returns Emits the success instance returned mapping ID directly.
   */
  createProduct(request: ProductCreationRequest): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}${this.endpoint}`, request).pipe(
      catchError(error => this.handleError(error, 'create product'))
    );
  }

  /**
   * Updates an existing product alongside mapping rules.
   * 
   * @param id ID to modify uniquely.
   * @param request Data properties overriding the core definitions.
   */
  updateProduct(id: number, request: ProductCreationRequest): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}${this.endpoint}/${id}`, request).pipe(
      catchError(error => this.handleError(error, 'update product'))
    );
  }

  /**
   * Uniformly manages formatting user-friendly HTTP client error displays.
   */
  private handleError(error: any, context: string): Observable<never> {
    console.error(`Product service error during [${context}]:`, error);
    let errorMessage = `Failed to ${context}`;
    
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Client error: ${error.error.message}`;
    } else if (error.status) {
      errorMessage = error.error?.message || `Server returned error (${error.status}): ${error.message}`;
    }
    
    return throwError(() => new Error(errorMessage));
  }
}
