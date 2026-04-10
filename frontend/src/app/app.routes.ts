import { Routes } from '@angular/router';
import { QuoteListComponent } from './pages/quote-list/quote-list.component';
import { QuoteFormComponent } from './pages/quote-form/quote-form.component';
import { QuoteDetailComponent } from './pages/quote-detail/quote-detail.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'quotes',
    pathMatch: 'full'
  },
  {
    path: 'quotes',
    component: QuoteListComponent
  },
  {
    path: 'quotes/new',
    component: QuoteFormComponent
  },
  {
    path: 'quotes/:id',
    component: QuoteDetailComponent
  },
  {
    path: '**',
    redirectTo: 'quotes'
  }
];
