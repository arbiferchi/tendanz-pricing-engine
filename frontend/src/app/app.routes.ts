import { Routes } from '@angular/router';
import { QuoteListComponent } from './pages/quote-list/quote-list.component';
import { QuoteFormComponent } from './pages/quote-form/quote-form.component';
import { QuoteDetailComponent } from './pages/quote-detail/quote-detail.component';
import { QuoteUpdateComponent } from './pages/quote-update/quote-update.component';
import { QuoteHistoryComponent } from './pages/quote-history/quote-history.component';
import { ProductFormComponent } from './pages/product-form/product-form.component';
import { ProductListComponent } from './pages/product-list/product-list.component';
import { ProductUpdateComponent } from './pages/product-update/product-update.component';
import { ProductDetailComponent } from './pages/product-detail/product-detail.component';

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
    path: 'quotes/:id/edit',
    component: QuoteUpdateComponent
  },
  {
    path: 'quotes/:id/history',
    component: QuoteHistoryComponent
  },
  {
    path: 'products',
    component: ProductListComponent
  },
  {
    path: 'products/new',
    component: ProductFormComponent
  },
  {
    path: 'products/:id',
    component: ProductDetailComponent
  },
  {
    path: 'products/:id/edit',
    component: ProductUpdateComponent
  },
  {
    path: '**',
    redirectTo: 'quotes'
  }
];
