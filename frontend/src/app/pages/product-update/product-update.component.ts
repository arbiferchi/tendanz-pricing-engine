import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ProductService } from '../../services/product.service';
import { ProductCreationRequest } from '../../models/product.model';

@Component({
  selector: 'app-product-update',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './product-update.component.html',
  styleUrls: ['./product-update.component.css']
})
export class ProductUpdateComponent implements OnInit, OnDestroy {
  productForm: FormGroup;
  isSubmitting = false;
  isLoading = true;
  error: string | null = null;
  successMessage: string | null = null;
  submitted = false;
  productId!: number;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      baseRate: ['', [Validators.required, Validators.min(0.01)]],
      ageFactorYoung: ['', [Validators.required, Validators.min(0.01)]],
      ageFactorAdult: ['', [Validators.required, Validators.min(0.01)]],
      ageFactorSenior: ['', [Validators.required, Validators.min(0.01)]],
      ageFactorElderly: ['', [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.productId = +idParam;
      this.loadProduct(this.productId);
    } else {
      this.error = 'Invalid product ID.';
      this.isLoading = false;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadProduct(id: number): void {
    this.productService.getProductById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (product) => {
          // Pre-fill the form with exact existing data from backend!
          this.productForm.patchValue({
            name: product.name,
            description: product.description || '',
            baseRate: product.baseRate || 0,
            ageFactorYoung: product.ageFactorYoung || 1.0,
            ageFactorAdult: product.ageFactorAdult || 1.0,
            ageFactorSenior: product.ageFactorSenior || 1.0,
            ageFactorElderly: product.ageFactorElderly || 1.0
          });
          this.isLoading = false;
        },
        error: (err) => {
          this.error = 'Failed to load product details: ' + err.message;
          this.isLoading = false;
        }
      });
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = null;
    this.successMessage = null;

    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    const request: ProductCreationRequest = this.productForm.value;

    this.productService.updateProduct(this.productId, request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.isSubmitting = false;
          this.successMessage = 'Product successfully updated!';
          
          setTimeout(() => {
            this.router.navigate(['/products']);
          }, 1000);
        },
        error: (err) => {
          this.error = err.message || 'Failed to update product.';
          this.isSubmitting = false;
        }
      });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.productForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched || this.submitted));
  }
}
