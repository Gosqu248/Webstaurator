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

    this.authService.login(email, password)
      .subscribe((isAuthenticated: boolean) => {
        if (isAuthenticated) {
          this.router.navigate([], {fragment: 'menu'});
          console.log("Login " + this.loginForm.value.email);
          this.backToMenuDialog();
        } else {
          this.isLoginError = !this.isLoginError;
          console.log("Invalid login or password");
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
