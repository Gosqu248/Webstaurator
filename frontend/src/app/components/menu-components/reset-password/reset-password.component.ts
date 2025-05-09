import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../../../services/api/auth.service";
import {Router} from "@angular/router";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent {
  resetPasswordForm: FormGroup;
  isResetError: boolean = false;
  resetMessage: string | null = null;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.resetPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  sendResetPasswordEmail() {
    const email = this.resetPasswordForm.value.email;
    this.isResetError = false;
    this.resetMessage = null;

    this.authService.resetPassword(email).subscribe({
      next: (response) => {
        if (response.error) {
          this.isResetError = true;
          this.resetMessage = 'Nie znaleziono takiego email. Spróbuj ponownie lub skontaktuj się z biurem';
        } else {
          this.resetMessage = 'Email z linkiem do resetowania hasła został wysłany';
        }
      },
      error: (error) => {
        this.isResetError = true;
        this.resetMessage = 'Wystąpił błąd podczas resetowania hasła. Spróbuj ponownie później';
      }
    });
  }
}
