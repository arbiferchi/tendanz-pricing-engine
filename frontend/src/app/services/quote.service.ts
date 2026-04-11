import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { QuoteRequest, QuoteResponse, PaginatedQuoteResponse, QuoteHistoryResponse } from '../models/quote.model';

/**
 * Service for interfacing with the backend Pricing Engine.
 * Responsible for Quote creations, historical modifications tracking,
 * downloading quotes as PDF, and paginated data retrievals.
 */
@Injectable({
  providedIn: 'root'
})
export class QuoteService {
  private readonly apiUrl = environment.apiUrl;
  private readonly endpoint = '/quotes';

  constructor(private http: HttpClient) {}

  /**
   * Dispatches the rules configuration to backend engines for final evaluation.
   * 
   * @param request Validated form binding details carrying personal identifiers and target mappings.
   * @returns Evaluated rules directly wrapped alongside overall summary object.
   */
  createQuote(request: QuoteRequest): Observable<QuoteResponse> {
    return this.http.post<QuoteResponse>(`${this.apiUrl}${this.endpoint}`, request).pipe(
      catchError(error => this.handleError(error, 'create quote'))
    );
  }

  /**
   * Fetch complete standalone quote mapping for reviewing specific records.
   * 
   * @param id Tracking numerical.
   */
  getQuote(id: number): Observable<QuoteResponse> {
    return this.http.get<QuoteResponse>(`${this.apiUrl}${this.endpoint}/${id}`).pipe(
      catchError(error => this.handleError(error, 'fetch quote details'))
    );
  }

  /**
   * Overrides existing payload properties and records application-layer modification.
   * 
   * @param id The origin record context ID.
   * @param request Payload adjustments carrying the updated configuration identifiers.
   */
  updateQuote(id: number, request: QuoteRequest): Observable<QuoteResponse> {
    return this.http.put<QuoteResponse>(`${this.apiUrl}${this.endpoint}/${id}`, request).pipe(
      catchError(error => this.handleError(error, 'update quote data'))
    );
  }

  /**
   * Leverages optional API logic binding filtering query mappings for table generation.
   * 
   * @param filters Supports dynamic sorting across product categorizations and financial thresholds.
   */
  getQuotes(filters?: { productId?: number; minPrice?: number }): Observable<QuoteResponse[]> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.productId != null) {
        params = params.set('productId', filters.productId.toString());
      }
      if (filters.minPrice != null) {
        params = params.set('minPrice', filters.minPrice.toString());
      }
    }
    
    return this.http.get<PaginatedQuoteResponse | QuoteResponse[]>(`${this.apiUrl}${this.endpoint}`, { params }).pipe(
      map(response => {
        // Fallback for Paginated structure parsing transparent map injection
        if (response && 'content' in response) {
          return response.content;
        }
        return response as QuoteResponse[];
      }),
      catchError(error => this.handleError(error, 'fetch quotes list'))
    );
  }

  /**
   * Retrieve Quote PDF blob for local downloading triggers.
   */
  getQuotePdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}${this.endpoint}/${id}/pdf`, { responseType: 'blob' }).pipe(
      catchError(error => this.handleError(error, 'generate quote PDF'))
    );
  }

  /**
   * Retrieves array history for transparent data-layer modification reviews.
   */
  getQuoteHistory(id: number): Observable<QuoteHistoryResponse[]> {
    return this.http.get<QuoteHistoryResponse[]>(`${this.apiUrl}${this.endpoint}/${id}/history`).pipe(
      catchError(error => this.handleError(error, 'fetch quote history'))
    );
  }

  /**
   * Internal mechanism for displaying standardized readable backend server constraints.
   */
  private handleError(error: any, action: string): Observable<never> {
    console.error(`Quote Service exception during [${action}]:`, error);
    let errorMessage = `Unable to ${action}. Please try again later.`;

    if (error.error instanceof ErrorEvent) {
      // Client-side issues mapping network errors 
      errorMessage = `Network or Client Exception: ${error.error.message}`;
    } else if (error.status) {
      // API responses mappings
      errorMessage = error.error?.message || error.error?.error || `Server Error ${error.status}: ${error.message}`;
    }
    
    return throwError(() => new Error(errorMessage));
  }
}
