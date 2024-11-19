import {Component,} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgClass, NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {AuthService} from "../../../services/auth.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {Router, RouterLink} from "@angular/router";
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
    NgClass,
    RouterLink
  ],
  templateUrl: './menu-login.component.html',
  styleUrl: './menu-login.component.css'
})
export class MenuLoginComponent {
  loginForm: FormGroup;
  isLoginError: boolean = false;
  isVisible: boolean = false;
  errorMessage: string | null = null;


  constructor(private languageService: LanguageService,
              private authService: AuthService,
              private fb: FormBuilder,
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

    this.authService.login(email, password)
      .subscribe({ next: (isAuthenticated: boolean) => {
          if (isAuthenticated) {
            console.log("Login " + this.loginForm.value.email);
            this.authService.loginEvent.emit();
            this.backToMenuDialog();
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
}
