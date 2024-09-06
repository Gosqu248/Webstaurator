import {ChangeDetectorRef, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {NgIf} from "@angular/common";
import {NavMenuLanguageComponent} from "../nav-menu-language/nav-menu-language.component";
import {NavMenuRegisterComponent} from "../nav-menu-register/nav-menu-register.component";
import {NavMenuLoginComponent} from "../nav-menu-login/nav-menu-login.component";
import {AuthService} from "../../../services/auth.service";

@Component({
  selector: 'app-nav-menu',
  standalone: true,
  imports: [
    NgIf,
    NavMenuLanguageComponent,
    NavMenuRegisterComponent,
    NavMenuLoginComponent
  ],
  templateUrl: './nav-menu.component.html',
  styleUrl: './nav-menu.component.css'
})
export class NavMenuComponent implements OnInit{
  @Output() menuClosed = new EventEmitter<void>();

  showLanguageOption: boolean = false;
  showLanguageMenu: boolean = false;
  showRegister: boolean = false;
  showLogin: boolean = false;
  name: string = '';

  constructor(private languageService: LanguageService, private authService: AuthService, private cdr: ChangeDetectorRef) {
    this.checkAuth();
  }

  ngOnInit() {
    this.checkWindowWidth();
    window.addEventListener('resize', this.checkWindowWidth.bind(this));
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key)
  }

  getUserData() {
    const token = sessionStorage.getItem('jwt');

    if (token) {
      this.authService.getUser(token).subscribe( {
        next: user => {
          sessionStorage.setItem('name', user.name);
          this.name = user.name
          sessionStorage.setItem('email', user.email);
          this.cdr.detectChanges();
        },
        error: error => {
          console.error('Error fetching user data', error);
        }
      });
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
  }

  checkAuth(): boolean {
    if (!!sessionStorage.getItem('jwt')) {
      this.getUserData();
      return true;
    } else return false;
  }

  logout() {
    sessionStorage.removeItem('jwt');
    sessionStorage.removeItem('name');
    sessionStorage.removeItem('email');
  }


}
