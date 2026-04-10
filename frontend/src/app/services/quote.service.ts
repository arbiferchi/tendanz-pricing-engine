import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { QuoteRequest, QuoteResponse } from '../models/quote.model';

/**
 * Service for managing quotes
 * This service handles all API communication with the backend pricing engine
 *
 * TODO: Candidate must implement the following methods:
 * - createQuote(request: QuoteRequest): Observable<QuoteResponse>
 * - getQuote(id: number): Observable<QuoteResponse>
 * - getQuotes(filters?: {productId?: number, minPrice?: number}): Observable<QuoteResponse[]>
 *
 * Requirements:
 * - Use HttpClient for HTTP requests
 * - Use catchError operator to handle errors
 * - Base URL should be configurable via environment.apiUrl
 * - Handle error responses appropriately (log errors, throw user-friendly messages)
 */
@Injectable({
  providedIn: 'root'
})
export class QuoteService {
  private readonly apiUrl = environment.apiUrl;
  private readonly endpoint = '/quotes';

  constructor(private http: HttpClient) {}

  /**
   * Create a new quote
   * POST /api/quotes
   *
   * @param request Quote request data
   * @returns Observable of the created quote response with calculated pricing
   *
   * TODO: Implement this method
   */
  createQuote(request: QuoteRequest): Observable<QuoteResponse> {
    // TODO: POST to ${this.apiUrl}${this.endpoint}
    // TODO: Send request body
    // TODO: Handle errors with catchError
    // TODO: Provide error feedback to user
    throw new Error('Method not implemented');
  }

  /**
   * Get a single quote by ID
   * GET /api/quotes/:id
   *
   * @param id Quote ID
   * @returns Observable of the quote details
   *
   * TODO: Implement this method
   */
  getQuote(id: number): Observable<QuoteResponse> {
    // TODO: GET from ${this.apiUrl}${this.endpoint}/${id}
    // TODO: Handle errors with catchError
    throw new Error('Method not implemented');
  }

  /**
   * Get all quotes with optional filtering
   * GET /api/quotes?productId=X&minPrice=Y
   *
   * @param filters Optional filter criteria
   * @param filters.productId Filter by product ID
   * @param filters.minPrice Filter by minimum price
   * @returns Observable of array of quotes
   *
   * TODO: Implement this method
   */
  getQuotes(filters?: { productId?: number; minPrice?: number }): Observable<QuoteResponse[]> {
    // TODO: GET from ${this.apiUrl}${this.endpoint}
    // TODO: Build HttpParams with optional filters
    // TODO: Pass params to HTTP request
    // TODO: Handle errors with catchError
    throw new Error('Method not implemented');
  }

  /**
   * Handle HTTP errors
   *
   * @param error The error object from HttpClient
   * @returns Observable that throws a user-friendly error message
   *
   * TODO: Implement error handling
   * - Log error to console for debugging
   * - Extract error message from backend response or use default
   * - Return Observable error with appropriate message
   */
  private handleError(error: any): Observable<never> {
    // TODO: Implement error handling
    console.error('Quote service error:', error);
    return throwError(() => new Error('Failed to process quote'));
  }
}
