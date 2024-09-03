import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {FormsModule} from "@angular/forms";
import {AuthService} from "../../../services/auth.service";
import {User} from "../../../interfaces/user.interface";

@Component({
  selector: 'app-nav-menu-register',
  standalone: true,
  imports: [
    NgIf,
    FormsModule
  ],
  templateUrl: './nav-menu-register.component.html',
  styleUrl: './nav-menu-register.component.css'
})
export class NavMenuRegisterComponent {
  @Output() closeRegistry = new EventEmitter<void>();

  name: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';

  constructor(private authService: AuthService ,private languageService: LanguageService) {}

  register() {
    const user: User = {
      name: this.name,
      email: this.email,
      password: this.password,
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


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key)
  }

  backToMenu() {
    this.closeRegistry.emit();
  }



}
