import {Component, ElementRef, HostListener, OnInit} from '@angular/core';

import {NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {AuthService} from "../../../services/auth.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {MenuLanguageComponent} from "../menu-language/menu-language.component";
import {MenuRegisterComponent} from "../menu-register/menu-register.component";
import {MenuLoginComponent} from "../menu-login/menu-login.component";
import {MenuProfileComponent} from "../menu-profile/menu-profile.component";
import {Router, RouterLink} from "@angular/router";

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    NgIf,
    MenuLanguageComponent,
    MenuRegisterComponent,
    MenuLoginComponent,
    MenuProfileComponent,
    RouterLink
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {
  showLanguageOption: boolean = false;
  name: string = '';
  private isFetchingUserData: boolean = false;
  private isAuthChecked: boolean = false;

  constructor(private languageService: LanguageService, private authService: AuthService, private router: Router, private elementRef: ElementRef) {}

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

  checkWindowWidth() {
    this.showLanguageOption = window.innerWidth < 768;
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

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    const targetElement = event.target as HTMLElement;

    if (targetElement.classList.contains('fa-chevron-left')) {
      return;
    }
    
    if (!this.elementRef.nativeElement.contains(targetElement) && !targetElement.closest('.menu-button')) {
      this.router.navigate(['']);
    }
  }
}