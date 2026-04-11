import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { QuoteService } from '../../services/quote.service';
import { QuoteHistoryResponse } from '../../models/quote.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-quote-history',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './quote-history.component.html',
  styleUrl: './quote-history.component.css'
})
export class QuoteHistoryComponent implements OnInit, OnDestroy {
  historyRecords: QuoteHistoryResponse[] = [];
  quoteId: number | null = null;
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
        this.quoteId = id;
        this.loadHistory(id);
      } else {
        this.errorMessage = 'Invalid Quote ID parameter.';
      }
    } else {
      this.errorMessage = 'No Quote ID provided.';
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadHistory(id: number): void {
    this.loading = true;
    this.errorMessage = null;

    this.quoteService.getQuoteHistory(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.historyRecords = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching quote history:', err);
          this.errorMessage = 'Failed to load history. Error: ' + err.message;
          this.loading = false;
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/quotes']);
  }
}
