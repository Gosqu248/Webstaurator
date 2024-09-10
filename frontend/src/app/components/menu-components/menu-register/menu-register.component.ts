import {Component} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule, ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {AuthService} from "../../../services/auth.service";
import {LanguageService} from "../../../services/language.service";
import {User} from "../../../interfaces/user.interface";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {NgClass, NgIf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";

@Component({
  selector: 'app-menu-register',
  standalone: true,
  imports: [
    NgIf,
    FormsModule,
    ReactiveFormsModule,
    NgClass,
    RouterLink
  ],
  templateUrl: './menu-register.component.html',
  styleUrl: './menu-register.component.css'
})
export class MenuRegisterComponent {
  registerForm: FormGroup;
  isVisiblePassword: boolean = false;
  isVisibleConfirm: boolean = false;



  constructor(private authService: AuthService ,private languageService: LanguageService, private fb: FormBuilder, private router: Router) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[A-Z])(?=.*[!#]).*$/)
      ]],
      confirmPassword: ['', Validators.required],
    }, {validators: this.passwordMatchValidator});
  }

  passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    return password && confirmPassword && password.value !== confirmPassword.value
      ? { passwordMismatch: true }
      : null;
  };


  register() {
    if (this.registerForm.valid) {

      const user: User = {
        name: this.registerForm.value.name,
        email: this.registerForm.value.email,
        password: this.registerForm.value.password,
        role: 'user'
      }

      this.authService.register(user).subscribe({
        next: (response: any) => {
          alert(response.message);
          this.router.navigate(['/menu']);
        },
        error: (err) => {
          console.error('Registration failed', err);
          alert('Error registering user');
        }
      });
    }
  }


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key)
  }


  showErrorFor(controlName: string): boolean {
    const control = this.registerForm.get(controlName);
    if (controlName === 'confirmPassword') {
      return control ? control.invalid && (control.dirty || control.touched) || this.registerForm.hasError('passwordMismatch') : false;
    }
    return control ? control.invalid && (control.dirty || control.touched) : false;
  }


  togglePasswordVisibility(fieldId: string) {
    const inputField = document.getElementById(fieldId) as HTMLInputElement;
    if (inputField.type === 'password') {
      inputField.type = 'text';
    } else {
      inputField.type = 'password';
    }
    fieldId === 'password' ? this.isVisiblePassword = !this.isVisiblePassword : this.isVisibleConfirm = !this.isVisibleConfirm;

  }

}