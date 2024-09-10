import {Component, EventEmitter, Output} from '@angular/core';
import {NgIf} from "@angular/common";
import {environment} from "../../../../environments/environment";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";

@Component({
  selector: 'app-menu-language',
  standalone: true,
    imports: [
        NgIf
    ],
  templateUrl: './menu-language.component.html',
  styleUrl: './menu-language.component.css'
})
export class MenuLanguageComponent {
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
