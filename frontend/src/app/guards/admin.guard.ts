import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {MatDialog} from "@angular/material/dialog";
import {MenuLoginComponent} from "../components/menu-components/menu-login/menu-login.component";

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const dialog = inject(MatDialog);

  if (!authService.isAuthenticated()) {
    dialog.open(MenuLoginComponent, {
      disableClose: true
    });
    return false;
  } else {
    const token = localStorage.getItem('role');
    if (token !== 'admin') {
      router.navigate(['/']);
      return false;
    } else {
      return true;
    }
  }
};

