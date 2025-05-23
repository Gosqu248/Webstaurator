import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../services/api/auth.service";


export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
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

