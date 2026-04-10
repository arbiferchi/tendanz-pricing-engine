import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { QuoteService } from '../../services/quote.service';
import { ProductService } from '../../services/product.service';
import { QuoteResponse } from '../../models/quote.model';
import { Product } from '../../models/product.model';

/**
 * Component for displaying a list of all quotes
 *
 * TODO: Candidate must implement the following:
 * 1. Load all quotes on component initialization using QuoteService.getQuotes()
 *
 * 2. Load products for filter dropdown using ProductService.getProducts()
 *
 * 3. Implement filtering in applyFilters():
 *    - Build filter object from selectedProductId and minPrice
 *    - Call QuoteService.getQuotes(filters)
 *    - Update filteredQuotes with results
 *
 * 4. Implement sorting in sortQuotes():
 *    - Sort by creation date (ascending/descending)
 *    - Sort by final price (ascending/descending)
 *
 * 5. Implement viewQuote() to navigate to /quotes/:id
 *
 * 6. Handle loading and error states
 */
@Component({
  selector: 'app-quote-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './quote-list.component.html',
  styleUrl: './quote-list.component.css'
})
export class QuoteListComponent implements OnInit {
  quotes: QuoteResponse[] = [];
  filteredQuotes: QuoteResponse[] = [];
  products: Product[] = [];
  loading = false;
  errorMessage: string | null = null;

  // Filter state
  selectedProductId: number | null = null;
  minPrice: number | null = null;

  // Sort state
  sortField: 'date' | 'price' = 'date';
  sortDirection: 'asc' | 'desc' = 'desc';

  constructor(
    private quoteService: QuoteService,
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // TODO: Load products for filter dropdown
    // TODO: Load quotes from QuoteService
    // TODO: Handle loading and error states
  }

  /**
   * Apply filters to the quotes
   *
   * TODO: Implement filtering
   * - Get filter values from component properties
   * - Call quoteService.getQuotes(filters)
   * - Update filteredQuotes with results
   * - Call sortQuotes() after receiving results
   * - Handle errors
   */
  applyFilters(): void {
    // TODO: Implement filtering logic
    console.log('Filters applied (TODO: implement)');
  }

  /**
   * Reset all filters and reload all quotes
   */
  resetFilters(): void {
    this.selectedProductId = null;
    this.minPrice = null;
    // TODO: Reload all quotes
  }

  /**
   * Toggle sort direction or change sort field
   */
  changeSortField(field: 'date' | 'price'): void {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.sortQuotes();
  }

  /**
   * Sort filteredQuotes in memory
   *
   * TODO: Implement sorting
   * - For 'date': sort by createdAt
   * - For 'price': sort by finalPrice
   * - Apply sortDirection (asc/desc)
   */
  private sortQuotes(): void {
    // TODO: Implement in-memory sorting of this.filteredQuotes
  }

  /**
   * Navigate to quote detail page
   *
   * TODO: Implement navigation to /quotes/:id
   * Hint: use this.router.navigate(['/quotes', id])
   */
  viewQuote(id: number): void {
    // TODO: Navigate to quote detail
  }
}
