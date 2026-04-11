import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ProductService } from '../../services/product.service';
import { ProductCreationRequest } from '../../models/product.model';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnDestroy {
  productForm: FormGroup;
  isSubmitting = false;
  error: string | null = null;
  successMessage: string | null = null;
  submitted = false;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private router: Router
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

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
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

    this.productService.createProduct(request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (product) => {
          this.isSubmitting = false;
          this.successMessage = 'Product successfully created!';
          
          setTimeout(() => {
            this.router.navigate(['/products']);
          }, 1000);
        },
        error: (err) => {
          this.error = err.message || 'Failed to create product. Please check your inputs.';
          this.isSubmitting = false;
        }
      });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.productForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched || this.submitted));
  }
}
