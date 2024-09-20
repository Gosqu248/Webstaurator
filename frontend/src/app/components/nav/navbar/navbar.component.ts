import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { environment } from "../../../../environments/environment";
import { isPlatformBrowser, NgClass, NgIf } from "@angular/common";
import { LanguageService } from "../../../services/language.service";
import { LanguageTranslations } from "../../../interfaces/language.interface";
import { MenuComponent } from "../../menu-components/menu/menu.component";
import { ActivatedRoute, NavigationEnd, Router, RouterLink, RouterOutlet } from "@angular/router";
import { filter } from "rxjs";
import {MenuLoginComponent} from "../../menu-components/menu-login/menu-login.component";
import {MenuRegisterComponent} from "../../menu-components/menu-register/menu-register.component";
import {MenuProfileComponent} from "../../menu-components/menu-profile/menu-profile.component";
import {MenuAddressesComponent} from "../../menu-components/menu-addresses/menu-addresses.component";
import {MenuLanguageComponent} from "../../menu-components/menu-language/menu-language.component";
import {MenuAddressChangeComponent} from "../../menu-components/menu-address-change/menu-address-change.component";
import {MenuChangePasswordComponent} from "../../menu-components/menu-change-password/menu-change-password.component";
import {MenuAddAddressComponent} from "../../menu-components/menu-add-address/menu-add-address.component";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    NgIf,
    MenuComponent,
    RouterLink,
    RouterOutlet,
    NgClass,
    MenuLoginComponent,
    MenuRegisterComponent,
    MenuProfileComponent,
    MenuAddressesComponent,
    MenuLanguageComponent,
    MenuAddressChangeComponent,
    MenuChangePasswordComponent,
    MenuAddAddressComponent
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  apiUrl = environment.api;
  logo = '/img/webstaurator-logo.png';
  ukFlag = '/img/uk-logo.png';
  plFlag = '/img/poland-logo.png';

  currentLanguage: string;
  currentFlag: string;
  showLanguageDropdown = false;
  showLanguageOption = true;
  isDimmed: boolean = false;
  showMenu: boolean = false;
  showRegister: boolean = false;
  showLogin: boolean = false;
  showProfile: boolean = false;
  showLanguage: boolean = false;
  showAddresses: boolean = false;
  showChangePassword: boolean = false;
  showAddAddress: boolean = false;
  showChangeAddress: boolean = false;
  currentRoute!: string;


  constructor(private languageService: LanguageService,
              @Inject(PLATFORM_ID) private platformId: Object,
              private route: ActivatedRoute,
              private router: Router) {
    this.currentLanguage = this.languageService.getCurrentLanguage();
    this.currentFlag = this.getCurrentFlag();
  }

  ngOnInit() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const menuItems = ['menu', 'menu-profile', 'menu-language', 'change-password', 'addresses', 'add-address', 'change-address', 'register', 'login'];
      this.isDimmed = menuItems.some(item => event.url.endsWith(item));
      this.currentRoute = event.url;
    });


    if (isPlatformBrowser(this.platformId)) {
      this.checkWindowWidth();
      window.addEventListener('resize', this.checkWindowWidth.bind(this));
    }

    this.route.fragment.subscribe(fragment => {
      this.showMenu = fragment === 'menu';
      this.showLogin = fragment === 'login';
      this.showRegister = fragment === 'register';
      this.showProfile = fragment === 'menu-profile';
      this.showLanguage = fragment === 'menu-language';
      this.showChangePassword = fragment === 'change-password';
      this.showAddresses = fragment === 'addresses';
      this.showAddAddress = fragment === 'add-address';
      this.showChangeAddress = fragment === 'change-address';
      this.isDimmed = this.showMenu;
    });
  }


  checkWindowWidth() {
    if (isPlatformBrowser(this.platformId)) {
      this.showLanguageOption = window.innerWidth > 768;
    }
  }

  toggleLanguageDropdown() {
    this.showLanguageDropdown = !this.showLanguageDropdown;
  }

  setLanguage(language: string) {
    if (this.currentLanguage !== language) {
      this.languageService.switchLanguage();
      this.currentLanguage = language;
      this.currentFlag = this.getCurrentFlag();
    }
    this.showLanguageDropdown = false;
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

  private getCurrentFlag() {
    return this.currentLanguage === 'pl' ? this.plFlag : this.ukFlag;
  }
}
