import {Component, ElementRef, HostListener, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {AuthService} from "../../../services/auth.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {MenuLanguageComponent} from "../menu-language/menu-language.component";
import {MenuRegisterComponent} from "../menu-register/menu-register.component";
import {MenuLoginComponent} from "../menu-login/menu-login.component";
import {MenuProfileComponent} from "../menu-profile/menu-profile.component";
import {ActivatedRoute, RouterLink, RouterOutlet} from "@angular/router";
import {FragmentsService} from "../../../services/fragments.service";
import {OptionService} from "../../../services/option.service";
import {AddressesService} from "../../../services/addresses.service";

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    NgIf,
    MenuLanguageComponent,
    MenuRegisterComponent,
    MenuLoginComponent,
    MenuProfileComponent,
    RouterLink,
    RouterOutlet
  ],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  showLanguageOption: boolean = false;
  name: string = '';
  private isFetchingUserData: boolean = false;
  private isAuthChecked: boolean = false;
  showRegister: boolean = false;
  showLogin: boolean = false;

  constructor(private languageService: LanguageService,
              private authService: AuthService,
              private elementRef: ElementRef,
              private route: ActivatedRoute,
              private fragmentService: FragmentsService,
              private addressService: AddressesService,
              private optionService: OptionService) {}

  ngOnInit() {
    this.checkAuth();
    this.checkWindowWidth();
    window.addEventListener('resize', this.checkWindowWidth.bind(this));
    this.route.fragment.subscribe(fragment => {
      this.showRegister = fragment === 'register';
      this.showLogin = fragment === 'login';
    });
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

  getUserData(token: string) {
    if (this.isFetchingUserData) return;
    this.isFetchingUserData = true;

    if (token) {
      this.authService.getUser(token).subscribe({
        next: user => {
          this.name = user.name;
          localStorage.setItem('name', user.name);
          localStorage.setItem('email', user.email);
          if (user.id) {
            localStorage.setItem('userId', user.id.toString())
            this.optionService.fetchFavouritesFromDatabase(user.id);
          }

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
      console.log('Token: ', token);

      if (token) {
        this.getUserData(token);
        return true;
      }
    }
    return !!localStorage.getItem('jwt');
  }

  logout() {
    this.authService.logout();
    this.optionService.clearFavourites();
    this.addressService.clearAddresses();
  }

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    const targetElement = event.target as HTMLElement;

    if (targetElement.classList.contains('fa-chevron-left')) {
      return;
    }

    if (!this.elementRef.nativeElement.contains(targetElement) && !targetElement.closest('.menu-button')) {
      this.removeFragment();
    }
  }

  removeFragment() {
    this.fragmentService.removeFragment();

  }
}
