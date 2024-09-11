import { Routes } from '@angular/router';
import { HomeComponent } from "./components/home/home.component";
import {MenuComponent} from "./components/menu-components/menu/menu.component";
import {MenuLoginComponent} from "./components/menu-components/menu-login/menu-login.component";
import {MenuRegisterComponent} from "./components/menu-components/menu-register/menu-register.component";
import {MenuProfileComponent} from "./components/menu-components/menu-profile/menu-profile.component";

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    children: [
      {
        path: 'menu',
        loadComponent: () => import('./components/menu-components/menu/menu.component').then(m => m.MenuComponent)
      },
      {
        path: 'login',
        loadComponent: () => import('./components/menu-components/menu-login/menu-login.component').then(m => m.MenuLoginComponent)
      },
      {
        path: 'register',
        loadComponent: () => import('./components/menu-components/menu-register/menu-register.component').then(m => m.MenuRegisterComponent)
      },
      {
        path: 'menu-profile',
        loadComponent: () => import('./components/menu-components/menu-profile/menu-profile.component').then(m => m.MenuProfileComponent)
      },
      {
        path: 'menu-language',
        loadComponent: () => import('./components/menu-components/menu-language/menu-language.component').then(m => m.MenuLanguageComponent)
      },
      {
        path: 'change-password',
        loadComponent: () => import('./components/menu-components/menu-change-password/menu-change-password.component').then(m => m.MenuChangePasswordComponent)
      }
    ]
  },
];
