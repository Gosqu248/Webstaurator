import {Component, HostListener} from '@angular/core';
import {environment} from "../../../environments/environment";
import {NgIf} from "@angular/common";
import {LanguageService} from "../../services/language.service";
import {LanguageTranslations} from "../../interfaces/language.interface";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  apiUrl = environment.api;
  logo = '/img/webstaurator-logo.png';
  ukFlag = '/img/uk-logo.png';
  plFlag = '/img/poland-logo.png';

  currentLanguage: string;
  currentFlag: string;
  showLanguageDropdown = false;
  showMenu = false;


  constructor(private languageService: LanguageService) {
    this.currentLanguage = this.languageService.getCurrentLanguage();
    this.currentFlag = this.getCurrentFlag();
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

  toggleMenu() {
    this.showMenu = !this.showMenu;
    const overlay = document.querySelector('.overlay') as HTMLElement;
    if (this.showMenu) {
      overlay.style.display = 'block';
    } else {
      overlay.style.display = 'none';
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const menu = document.querySelector('.menu') as HTMLElement;
    const overlay = document.querySelector('.overlay') as HTMLElement;
    if(this.showMenu && !menu.contains(event.target as Node)) {
      this.showMenu = false;
      overlay.style.display = 'none';
    }
  }


}
