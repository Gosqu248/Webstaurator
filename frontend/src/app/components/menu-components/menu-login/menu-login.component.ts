import {Component,} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgClass, NgIf} from "@angular/common";
import {LanguageService} from "../../../services/state/language.service";
import {AuthService} from "../../../services/api/auth.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {Router} from "@angular/router";
import {MenuComponent} from "../menu/menu.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MenuRegisterComponent} from "../menu-register/menu-register.component";

@Component({
  selector: 'app-menu-login',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './menu-login.component.html',
  styleUrl: './menu-login.component.css'
})
export class MenuLoginComponent {
  loginForm: FormGroup;
  isLoginError: boolean = false;
  isVisible: boolean = false;
  errorMessage: string | null = null;
  show2FA: boolean = false;
  twoFactorCode: string = '';
  showForgotPassword: boolean = false;
  resetEmail: string = '';


  constructor(private languageService: LanguageService,
              private authService: AuthService,
              private fb: FormBuilder,
              private router: Router,
              private dialog: MatDialog,
              public dialogRef: MatDialogRef<MenuLoginComponent>) {
    this.loginForm = this.fb.group({
      email: ['', Validators.email],
      password: ['']
    });
  }

  login() {
    const email = this.loginForm.value.email;
    const password = this.loginForm.value.password;
    this.isLoginError = false;
    this.errorMessage = null;
    this.show2FA = false;

    this.authService.login(email, password)
      .subscribe({
        next: (isAuthenticated: boolean) => {
          if (isAuthenticated) {
            this.show2FA = true;
          } else {
            this.isLoginError = true;
            console.log("Invalid login or password");
          }
        },
        error: (error) => {
          if (error.message === 'Account is locked. Try again later.') {
            console.error('Account is locked. Try again later.');
            this.isLoginError = true;
            this.errorMessage = 'locked';
          } else {
            console.error('Login error: ', error);
            this.isLoginError = true;
          }
        }
      });
  }

  verify2FA(code: string) {
    this.authService.verify2FA(code).subscribe({
        next: (isAuthenticated: boolean) => {
          if (isAuthenticated) {
            console.log("Logged in successfully");
            this.authService.loginEvent.emit();
            this.backToMenuDialog();
          } else {
            console.error("Invalid 2FA code");
            this.isLoginError = true;
          }
        },
        error: (error) => {
          console.error('2FA error: ', error);
          this.isLoginError = true;
        }
    });
  }

  loginWithGoogle() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }

  togglePasswordVisibility(fieldId: string): void {
    const inputField = document.getElementById(fieldId) as HTMLInputElement;
    inputField.type === 'password' ? inputField.type = 'text' : inputField.type = 'password';
    this.isVisible = !this.isVisible;
  }


  closeDialog() {
    this.dialogRef.close();
  }

  backToMenuDialog() {
    this.closeDialog();
    this.dialog.open(MenuComponent);
  }

  goToRegisterDialog() {
    this.closeDialog();
    this.dialog.open(MenuRegisterComponent);
  }

  showForgotPasswordForm() {
    this.showForgotPassword = true;
  }

  sendResetPasswordEmail() {
    this.authService.resetPassword(this.resetEmail).subscribe({
      next: (response) => {
        if (response.error) {
          alert('Nie znaleziono takiego email. \nSpróbuj ponownie lub skontaktuj się z biurem');
        } else {
          this.showForgotPassword = false;
        }
      },
      error: (error) => {
        if (error.status === 401) {
          alert('Nie znaleziono takiego email');
        } else {
          console.log('Error resetting password: ', error);
        }
      }
    });
  }
}
