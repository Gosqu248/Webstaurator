import { Routes } from '@angular/router';
import { HomeComponent } from "./components/home/home.component";

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'restaurants',
    loadComponent: () => import('./components/resturants-components/restaurant/restaurant.component').then(m => m.RestaurantComponent),
  },
  {
    path: 'menu/:restaurantName',
    loadComponent: () => import('./components/restaurant-menu-components/restaurant-menu/restaurant-menu.component').then(m => m.RestaurantMenuComponent),
  },
  {
    path: 'checkout',
    loadComponent: () => import('./components/order-components/order-home/order-home.component').then(m => m.OrderHomeComponent),
  },
  {
    path: 'payment-confirmation',
    loadComponent: () => import('./components/payment-confirmation-components/payment-confirmation/payment-confirmation.component').then(m => m.PaymentConfirmationComponent),
  },
  {
    path: 'orders-history',
    loadComponent: () => import('./components/user-orders-components/order-home/order-home.component').then(m => m.OrderHomeComponent),
  },
  { path: 'auth/callback',
    loadComponent: () => import('./components/menu-components/google-callback/google-callback.component').then(m => m.GoogleCallbackComponent),
  },
  {
    path: 'order-monitoring',
    loadComponent: () => import('./components/admin-components/order-monitoring/monitor-home/monitor-home.component').then(m => m.MonitorHomeComponent),
  }
];
