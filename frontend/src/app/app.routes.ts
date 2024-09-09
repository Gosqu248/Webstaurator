import { Routes } from '@angular/router';
import { HomeComponent } from "./components/home/home.component";

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'menu',
    loadComponent: () => import('./components/nav/nav-menu/nav-menu.component').then(m => m.NavMenuComponent),
    outlet: 'menu'
  },
  {
    path: 'register',
    loadComponent: () => import('./components/nav/nav-menu-register/nav-menu-register.component').then(m => m.NavMenuRegisterComponent),
    outlet: 'menu-register'
  },
  {
    path: 'login',
    loadComponent: () => import('./components/nav/nav-menu-login/nav-menu-login.component').then(m => m.NavMenuLoginComponent),
    outlet: 'menu-login'
  },
  {
    path: 'profile',
    loadComponent: () => import('./components/nav/nav-menu-profile/nav-menu-profile.component').then(m => m.NavMenuProfileComponent),
    outlet: 'menu-profile'
  }
];
