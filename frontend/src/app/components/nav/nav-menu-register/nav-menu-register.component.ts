import {Component, EventEmitter, Output} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule, ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {AuthService} from "../../../services/auth.service";
import {User} from "../../../interfaces/user.interface";


@Component({
  selector: 'app-nav-menu-register',
  standalone: true,
  imports: [
    NgIf,
    FormsModule,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './nav-menu-register.component.html',
  styleUrl: './nav-menu-register.component.css'
})
export class NavMenuRegisterComponent {
  @Output() closeRegistry = new EventEmitter<void>();
  @Output() openLogin = new EventEmitter<void>();
  registerForm: FormGroup;



  constructor(private authService: AuthService ,private languageService: LanguageService, private fb: FormBuilder) {
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
          this.closeRegistry.emit();
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

  backToMenu() {
    this.closeRegistry.emit();
  }

  showErrorFor(controlName: string): boolean {
    const control = this.registerForm.get(controlName);
    if (controlName === 'confirmPassword') {
      return control ? control.invalid && (control.dirty || control.touched) || this.registerForm.hasError('passwordMismatch') : false;
    }
    return control ? control.invalid && (control.dirty || control.touched) : false;
  }

  changeToLogin() {
    this.backToMenu();
    this.openLogin.emit();
  }

}
