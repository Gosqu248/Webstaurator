import {Component, Inject, OnInit, PLATFORM_ID} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {isPlatformBrowser, NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {MenuComponent} from "../../menu-components/menu/menu.component";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    NgIf,
    MenuComponent,
    RouterLink
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

  constructor(private languageService: LanguageService, @Inject(PLATFORM_ID) private platformId: Object) {
    this.currentLanguage = this.languageService.getCurrentLanguage();
    this.currentFlag = this.getCurrentFlag();
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)) {
      this.checkWindowWidth();
      window.addEventListener('resize', this.checkWindowWidth.bind(this));
    }
  }


  checkWindowWidth() {
    if(isPlatformBrowser(this.platformId)) {
    this.showLanguageOption = window.innerWidth > 768;
    }
  }
  toggleLanguageDropdown() {
    this.showLanguageDropdown = !this.showLanguageDropdown;
  }

  setLanguage(language: string) {
    if(this.currentLanguage !== language) {
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
