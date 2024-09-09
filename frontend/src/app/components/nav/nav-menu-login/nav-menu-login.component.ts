import {Component, EventEmitter, Output} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgClass, NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {AuthService} from "../../../services/auth.service";

@Component({
  selector: 'app-nav-menu-login',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './nav-menu-login.component.html',
  styleUrl: './nav-menu-login.component.css'
})
export class NavMenuLoginComponent {
  @Output() closeLogin = new EventEmitter<void>();
  @Output() openRegistry = new EventEmitter<void>();
  loginForm: FormGroup;
  isloginError: boolean = false;
  isVisible: boolean = false;


  constructor(private languageService: LanguageService, private authService: AuthService, private fb: FormBuilder) {
    this.loginForm = this.fb.group({
      email: ['', Validators.email],
      password: ['']
    });
  }

  login() {
    const email = this.loginForm.value.email;
    const password = this.loginForm.value.password;
    this.isloginError = false;

    this.authService.login(email, password)
      .subscribe((isAuthenticated: boolean) => {
        if (isAuthenticated) {
          this.closeLogin.emit();
          console.log("Login " + this.loginForm.value.email);
        } else {
          this.isloginError = !this.isloginError;
          console.log("Invalid login or password");
          console.log(email);
          console.log(password);
        }
      });
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }

  backToMenu() {
    this.closeLogin.emit();
  }

  changeToRegister() {
    this.backToMenu();
    this.openRegistry.emit();
  }

  togglePasswordVisibility(fieldId: string): void {
    const inputField = document.getElementById(fieldId) as HTMLInputElement;
    if (inputField.type === 'password') {
      inputField.type = 'text';
    } else {
      inputField.type = 'password';
    }
    this.isVisible = !this.isVisible;
  }
}
