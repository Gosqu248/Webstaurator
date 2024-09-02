import {Component, EventEmitter, Output} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-nav-menu-language',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './nav-menu-language.component.html',
  styleUrl: './nav-menu-language.component.css'
})
export class NavMenuLanguageComponent {
  @Output() closeLanguageMenu = new EventEmitter<void>();
  apiUrl = environment.api;
  ukFlag = '/img/uk-logo.png';
  plFlag = '/img/poland-logo.png';

  constructor(private languageService: LanguageService) {}

  closeMenu() {
    this.closeLanguageMenu.emit();
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key)
  }

  setLanguage(language: string) {
    if(this.getCurrentLanguage() != language) {
      this.languageService.switchLanguage();
      this.closeMenu();
    }
  }

  getCurrentLanguage() {
    return this.languageService.getCurrentLanguage();
  }




}
