import {Component,} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgClass, NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {AuthService} from "../../../services/auth.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {Router, RouterLink} from "@angular/router";

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
  isloginError: boolean = false;
  isVisible: boolean = false;


  constructor(private languageService: LanguageService, private authService: AuthService, private fb: FormBuilder, private router: Router) {
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
          this.router.navigate(['/menu']);
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

  togglePasswordVisibility(fieldId: string): void {
    const inputField = document.getElementById(fieldId) as HTMLInputElement;
    inputField.type === 'password' ? inputField.type = 'text' : inputField.type = 'password';
    this.isVisible = !this.isVisible;
  }
}
