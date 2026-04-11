import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { QuoteService } from '../../services/quote.service';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { QuoteRequest } from '../../models/quote.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

const ZONES = [
  { code: 'TUN', name: 'Grand Tunis' },
  { code: 'SFX', name: 'Sfax' },
  { code: 'SOU', name: 'Sousse' }
];

@Component({
  selector: 'app-quote-update',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './quote-update.component.html',
  styleUrl: './quote-update.component.css'
})
export class QuoteUpdateComponent implements OnInit, OnDestroy {
  form: FormGroup;
  quoteId: number | null = null;
  products: Product[] = [];
  zones = ZONES;
  
  loading = false;
  loadingInit = true;
  submitted = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private quoteService: QuoteService,
    private productService: ProductService
  ) {
    this.form = this.fb.group({
      clientName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      productId: ['', [Validators.required]],
      zoneCode: ['', [Validators.required]],
      clientAge: ['', [Validators.required, Validators.min(18), Validators.max(99)]]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.quoteId = parseInt(idParam, 10);
      this.initializeData();
    } else {
      this.errorMessage = 'No Quote ID provided for update.';
      this.loadingInit = false;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeData(): void {
    this.loadingInit = true;
    
    // Load products first
    this.productService.getProducts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (prods) => {
          this.products = prods;
          this.loadExistingQuote(); // Chain to load quote details map
        },
        error: (err) => {
          this.errorMessage = 'Failed to load system products.';
          this.loadingInit = false;
        }
      });
  }

  private loadExistingQuote(): void {
    if (!this.quoteId) return;

    this.quoteService.getQuote(this.quoteId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (quote) => {
          // Pre-fill form from existing quote details
          const matchingProduct = this.products.find(p => p.name === quote.productName);
          const matchingZone = this.zones.find(z => z.name === quote.zoneName);
          
          this.form.patchValue({
            clientName: quote.clientName,
            clientAge: quote.clientAge,
            productId: matchingProduct ? matchingProduct.id : '',
            zoneCode: matchingZone ? matchingZone.code : ''
          });
          this.loadingInit = false;
        },
        error: (err) => {
          this.errorMessage = 'Quote mapping failed. Unable to fetch details.';
          this.loadingInit = false;
        }
      });
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = null;
    this.successMessage = null;

    if (this.form.invalid || !this.quoteId) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    const updateRequest: QuoteRequest = {
      productId: Number(this.form.value.productId),
      zoneCode: this.form.value.zoneCode,
      clientName: this.form.value.clientName,
      clientAge: Number(this.form.value.clientAge)
    };

    this.quoteService.updateQuote(this.quoteId, updateRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.loading = false;
          this.successMessage = 'Quote successfully updated! Calculating diff...';
          
          setTimeout(() => {
             this.router.navigate(['/quotes', response.quoteId]);
          }, 1000);
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.message || 'Failed to update quote.';
        }
      });
  }

  hasError(fieldName: string, errorType: string): boolean {
    const field = this.form.get(fieldName);
    return !!(field && field.hasError(errorType) && (field.dirty || field.touched || this.submitted));
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.form.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched || this.submitted));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.form.get(fieldName);
    if (!field || !field.errors) return '';
    if (field.hasError('required')) return `This field is required`;
    if (field.hasError('minlength')) return `Min ${field.errors['minlength'].requiredLength} chars`;
    if (field.hasError('min')) return `Min value: ${field.errors['min'].min}`;
    if (field.hasError('max')) return `Max value: ${field.errors['max'].max}`;
    return 'Invalid input';
  }
}
