import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { QuoteService } from '../../services/quote.service';
import { ProductService } from '../../services/product.service';
import { QuoteResponse } from '../../models/quote.model';
import { Product } from '../../models/product.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

/**
 * Component for displaying a list of all quotes
 */
@Component({
  selector: 'app-quote-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './quote-list.component.html',
  styleUrl: './quote-list.component.css'
})
export class QuoteListComponent implements OnInit, OnDestroy {
  quotes: QuoteResponse[] = [];
  filteredQuotes: QuoteResponse[] = [];
  products: Product[] = [];
  loading = false;
  loadingProducts = false;
  errorMessage: string | null = null;
  
  private destroy$ = new Subject<void>();

  // Filter state
  selectedProductId: number | null | undefined = undefined;
  minPrice: number | null | undefined = undefined;

  // Sort state
  sortField: 'date' | 'price' = 'date';
  sortDirection: 'asc' | 'desc' = 'desc';

  // Pagination state
  currentPage = 0;
  pageSize = 10;
  totalPages = 1;
  totalElements = 0;

  constructor(
    private quoteService: QuoteService,
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadQuotes();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Initialize products dropdown data
   */
  private loadProducts(): void {
    this.loadingProducts = true;
    this.productService.getProducts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.products = data;
          this.loadingProducts = false;
        },
        error: (err) => {
          console.error('Failed to load filter products', err);
          this.loadingProducts = false;
        }
      });
  }

  /**
   * Load base quotes (optionally using current active filters from inputs)
   */
  private loadQuotes(): void {
    this.loading = true;
    this.errorMessage = null;

    const filters: any = {
      page: this.currentPage,
      size: this.pageSize
    };
    if (this.selectedProductId) filters.productId = this.selectedProductId;
    if (this.minPrice) filters.minPrice = this.minPrice;

    this.quoteService.getQuotes(filters)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response && response.content) {
            this.quotes = response.content;
            this.filteredQuotes = [...response.content];
            this.totalPages = response.totalPages;
            this.totalElements = response.totalElements;
            this.currentPage = response.number;
          } else {
            // Fallback if backend suddenly returned array instead of pagination
            let data = response as any;
            this.quotes = data;
            this.filteredQuotes = [...data];
            this.totalPages = 1;
            this.totalElements = data.length;
          }
          this.sortQuotes();
          this.loading = false;
        },
        error: (err) => {
          this.errorMessage = 'Failed to load quotes. ' + err.message;
          this.loading = false;
        }
      });
  }

  /**
   * Pagination Controls
   */
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadQuotes();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadQuotes();
    }
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadQuotes();
    }
  }

  /**
   * Apply filters to the quotes bounding to API params directly
   */
  applyFilters(): void {
    this.currentPage = 0; // Reset to page 0 on filter change
    this.loadQuotes();
  }

  /**
   * Reset all filters and reload all quotes
   */
  resetFilters(): void {
    this.selectedProductId = undefined;
    this.minPrice = undefined;
    this.currentPage = 0;
    this.loadQuotes();
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
   * Sort filteredQuotes in memory locally representing table adjustments
   */
  private sortQuotes(): void {
    if (!this.filteredQuotes || this.filteredQuotes.length === 0) return;

    this.filteredQuotes.sort((a, b) => {
      let comparison = 0;
      
      if (this.sortField === 'date') {
        const dateA = new Date(a.createdAt).getTime();
        const dateB = new Date(b.createdAt).getTime();
        comparison = dateA - dateB;
      } else if (this.sortField === 'price') {
        comparison = a.finalPrice - b.finalPrice;
      }

      return this.sortDirection === 'asc' ? comparison : -comparison;
    });
  }

  /**
   * Navigate to quote detail page explicitly 
   */
  viewQuote(id: number): void {
    this.router.navigate(['/quotes', id]);
  }
}
