import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { QuoteService } from '../../services/quote.service';
import { QuoteResponse } from '../../models/quote.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

/**
 * Component for displaying the details of a single quote
 */
@Component({
  selector: 'app-quote-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './quote-detail.component.html',
  styleUrl: './quote-detail.component.css'
})
export class QuoteDetailComponent implements OnInit, OnDestroy {
  quote: QuoteResponse | null = null;
  loading = false;
  errorMessage: string | null = null;
  
  private destroy$ = new Subject<void>();

  constructor(
    private quoteService: QuoteService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = parseInt(idParam, 10);
      if (!isNaN(id)) {
        this.loadQuote(id);
      } else {
        this.errorMessage = 'Identifiant du devis invalide.';
      }
    } else {
      this.errorMessage = 'Aucun identifiant de devis fourni.';
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadQuote(id: number): void {
    this.loading = true;
    this.errorMessage = null;

    this.quoteService.getQuote(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.quote = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching quote:', err);
          this.errorMessage = 'Erreur lors du chargement du devis. Veuillez réessayer plus tard.';
          this.loading = false;
        }
      });
  }
}
