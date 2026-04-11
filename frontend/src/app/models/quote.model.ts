/**
 * Quote Request DTO - sent to backend when creating a new quote.
 * Must match the backend QuoteRequest DTO fields exactly.
 */
export interface QuoteRequest {
  productId: number;
  zoneCode: string;
  clientName: string;
  clientAge: number;
}

/**
 * Quote Response DTO - returned from backend after creating or fetching a quote.
 * Must match the backend QuoteResponse DTO fields exactly.
 */
export interface QuoteResponse {
  quoteId: number;
  productName: string;
  zoneName: string;
  clientName: string;
  clientAge: number;
  basePrice: number;
  finalPrice: number;
  appliedRules: string[];
  createdAt: string; // ISO timestamp
}

/**
 * Paginated response structure for quotes.
 */
export interface PaginatedQuoteResponse {
  content: QuoteResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

/**
 * Quote History Response DTO.
 */
export interface QuoteHistoryResponse {
  id: number;
  quoteId: number;
  previousPrice: number;
  newPrice: number;
  changeSummary: string;
  modifiedAt: string;
}
