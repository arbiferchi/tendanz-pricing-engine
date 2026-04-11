import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { QuoteService } from '../../services/quote.service';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { QuoteRequest } from '../../models/quote.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

/**
 * Available zones with their codes (must match backend data.sql)
 */
const ZONES = [
  { code: 'TUN', name: 'Grand Tunis' },
  { code: 'SFX', name: 'Sfax' },
  { code: 'SOU', name: 'Sousse' }
];

/**
 * Component for creating a new quote
 */
@Component({
  selector: 'app-quote-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './quote-form.component.html',
  styleUrl: './quote-form.component.css'
})
export class QuoteFormComponent implements OnInit, OnDestroy {
  form: FormGroup;
  products: Product[] = [];
  zones = ZONES;
  loading = false;
  submitted = false;
  loadingProducts = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private quoteService: QuoteService,
    private productService: ProductService,
    private router: Router
  ) {
    this.form = this.fb.group({
      clientName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      productId: ['', [Validators.required]],
      zoneCode: ['', [Validators.required]],
      clientAge: ['', [Validators.required, Validators.min(18), Validators.max(99)]]
    });
  }

  ngOnInit(): void {
    this.loadProducts();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Fetches available products from the backend API to populate the product dropdown.
   */
  private loadProducts(): void {
    this.loadingProducts = true;
    this.errorMessage = null;

    this.productService.getProducts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: Product[]) => {
          this.products = data;
          this.loadingProducts = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to load products. Please refresh the page.';
          this.loadingProducts = false;
          console.error(error);
        }
      });
  }

  /**
   * Submit the form to generate a new quote.
   */
  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = null;
    this.successMessage = null;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    const request: QuoteRequest = {
      productId: Number(this.form.value.productId),
      zoneCode: this.form.value.zoneCode,
      clientName: this.form.value.clientName,
      clientAge: Number(this.form.value.clientAge)
    };

    this.quoteService.createQuote(request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.loading = false;
          this.successMessage = 'Quote successfully created!';
          
          // Navigate to the quote details page after successful creation
          setTimeout(() => {
             this.router.navigate(['/quotes', response.quoteId]);
          }, 1000);
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.message || 'Failed to create quote. Please check your inputs.';
        }
      });
  }

  /**
   * Check if a form field has an error (provided helper)
   */
  hasError(fieldName: string, errorType: string): boolean {
    const field = this.form.get(fieldName);
    return !!(field && field.hasError(errorType) && (field.dirty || field.touched || this.submitted));
  }

  /**
   * Check if a form field is invalid (provided helper)
   */
  isFieldInvalid(fieldName: string): boolean {
    const field = this.form.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched || this.submitted));
  }

  /**
   * Get error message for a field (provided helper)
   */
  getErrorMessage(fieldName: string): string {
    const field = this.form.get(fieldName);
    if (!field || !field.errors) return '';

    if (field.hasError('required')) return `This field is required`;
    if (field.hasError('minlength')) return `Minimum ${field.errors['minlength'].requiredLength} characters`;
    if (field.hasError('min')) return `Minimum value is ${field.errors['min'].min}`;
    if (field.hasError('max')) return `Maximum value is ${field.errors['max'].max}`;

    return 'Invalid input';
  }
}
