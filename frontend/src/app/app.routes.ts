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
  }

];
