import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit, OnDestroy {
  products: Product[] = [];
  isLoading = true;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  // Pagination state (like QuoteList)
  currentPage = 0;
  pageSize = 10;
  totalPages = 1;
  totalElements = 0;

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.error = null;

    // Simulate sending page/size filters as QuoteList does
    const filters = {
      page: this.currentPage,
      size: this.pageSize
    };

    this.productService.getProducts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: any) => {
          // If backend returns paginated object, extract it
          if (data && typeof data === 'object' && 'content' in data) {
            this.products = data.content;
            this.totalPages = data.totalPages || 1;
            this.totalElements = data.totalElements || data.content.length;
            this.currentPage = data.number !== undefined ? data.number : this.currentPage;
          } else {
            // Fallback for simple array
            this.products = Array.isArray(data) ? data : [];
            this.totalPages = 1;
            this.totalElements = this.products.length;
          }
          this.isLoading = false;
        },
        error: (err) => {
          this.error = err.message || 'Failed to load products';
          this.isLoading = false;
        }
      });
  }

  /**
   * Pagination Controls
   */
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProducts();
    }
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadProducts();
    }
  }
}
