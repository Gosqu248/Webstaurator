import {Component, Inject, OnInit, PLATFORM_ID} from '@angular/core';
import { environment } from "../../../../environments/environment";
import { isPlatformBrowser, NgClass, NgIf } from "@angular/common";
import { LanguageService } from "../../../services/state/language.service";
import { LanguageTranslations } from "../../../interfaces/language.interface";
import { MenuComponent } from "../../menu-components/menu/menu.component";
import {NavigationEnd, Router} from "@angular/router";
import {RestaurantCategoryComponent} from "../../resturants-components/restaurant-category/restaurant-category.component";
import {OptionService} from "../../../services/state/option.service";
import {MatDialog} from "@angular/material/dialog";
import {filter} from "rxjs";

@Component({
    selector: 'app-navbar',
    imports: [
        NgIf,
        NgClass,
        RestaurantCategoryComponent
    ],
    templateUrl: './navbar.component.html',
    styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit{
  apiUrl = environment.api;
  logo = '/img/webstaurator-logo.png';
  ukFlag = '/img/uk-logo.png';
  plFlag = '/img/poland-logo.png';

  currentLanguage: string;
  currentFlag: string;
  showLanguageDropdown = false;
  showLanguageOption = true;

  currentRoute!: string;
  address: string | null = '';
  selectedOption: string = this.optionService.selectedOption.value;

  constructor(private languageService: LanguageService,
              @Inject(PLATFORM_ID) private platformId: Object,
              private optionService: OptionService,
              private dialog: MatDialog,
              private router: Router) {
    this.currentLanguage = this.languageService.getCurrentLanguage();
    this.currentFlag = this.getCurrentFlag();
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.getCurrentRoute();
      this.updateAddress();

    });
  }

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.updateAddress();
      this.checkWindowWidth();
      window.addEventListener('resize', this.checkWindowWidth.bind(this));
    }
  }

  updateAddress() {
    if (isPlatformBrowser(this.platformId)) {
      this.address = sessionStorage.getItem('searchAddress');
    }
  }

  getCurrentRoute() {
    this.currentRoute = this.router.url;
  }

  goToHome() {
    this.router.navigate(['/']);
    sessionStorage.removeItem('address');
  }

  goToRestaurants() {
    this.router.navigate(['/restaurants']);
  }

  goToMenu() {
    const name = sessionStorage.getItem('restaurantName');
    let formattedName = '';
    if (name) {
       formattedName = name.replace(/[\s,]+/g, '-');
    }
    this.router.navigate(['/menu', formattedName]);
  }

  isMenuRoute() {
    return this.currentRoute ? this.currentRoute.includes('menu') : false;
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

  selectOption(option: string) {
    this.selectedOption = option;
    this.optionService.setSelectedOption(option);
  }

  openMenuDialog() {
    this.dialog.open(MenuComponent, {
      width: '100%',
      maxWidth: '800px',
    });
  }

  goToAllRestaurants() {
    this.router.navigate(['/all-restaurants']);

  }
}
