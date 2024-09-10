import {Component, EventEmitter, OnInit, Output} from '@angular/core';

import {NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {AuthService} from "../../../services/auth.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {MenuLanguageComponent} from "../menu-language/menu-language.component";
import {MenuRegisterComponent} from "../menu-register/menu-register.component";
import {MenuLoginComponent} from "../menu-login/menu-login.component";
import {MenuProfileComponent} from "../menu-profile/menu-profile.component";

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    NgIf,
    MenuLanguageComponent,
    MenuRegisterComponent,
    MenuLoginComponent,
    MenuProfileComponent
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {
  @Output() menuClosed = new EventEmitter<void>();

  showLanguageOption: boolean = false;
  showLanguageMenu: boolean = false;
  showRegister: boolean = false;
  showLogin: boolean = false;
  name: string = '';
  showProfile: boolean = false;
  private isFetchingUserData: boolean = false;
  private isAuthChecked: boolean = false;

  constructor(private languageService: LanguageService, private authService: AuthService) {}

  ngOnInit() {
    this.checkAuth();
    this.checkWindowWidth();
    window.addEventListener('resize', this.checkWindowWidth.bind(this));
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key)
  }

  getUserData(token: string) {
    if (this.isFetchingUserData) return;
    this.isFetchingUserData = true;

    if (token) {
      this.authService.getUser(token).subscribe({
        next: user => {
          const userData = JSON.parse(user.name);
          localStorage.setItem('name', userData.name);
          this.name = userData.name;
          localStorage.setItem('email', user.email);
          console.log('User data fetched: ', user);
          this.isFetchingUserData = false;
        },
        error: error => {
          console.error('Error fetching user data', error);

          this.isFetchingUserData = false;
        }
      });
    } else {
      this.isFetchingUserData = false;
    }
  }

  closeMenu() {
    this.menuClosed.emit();
  }

  checkWindowWidth() {
    this.showLanguageOption = window.innerWidth < 768;
  }

  toggleLanguageMenu() {
    this.showLanguageMenu = !this.showLanguageMenu;
  }

  toggleRegister() {
    this.showRegister = !this.showRegister;
  }

  toggleLogin() {
    this.showLogin = !this.showLogin;

    const token = localStorage.getItem('jwt');
    if (token) {
      this.getUserData(token);
    }
  }

  checkAuth(): boolean {
    if (!this.isAuthChecked) {
      this.isAuthChecked = true;
      const token = localStorage.getItem('jwt');

      if (token) {
        this.getUserData(token);
        return true;
      }
    }
    return !!localStorage.getItem('jwt');
  }

  logout() {
    this.authService.logout();
  }

  toggleProfile() {
    if (this.checkAuth()) {
      this.showProfile = !this.showProfile;
    } else {
      this.showLogin = true;
    }
    const jwt = localStorage.getItem('jwt');
    if(jwt) this.getUserData(jwt);
  }
}
