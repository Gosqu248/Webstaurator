import {Component, EventEmitter, Output} from '@angular/core';
import {NgIf} from "@angular/common";
import {environment} from "../../../../environments/environment";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-menu-language',
  standalone: true,
  imports: [
    NgIf,
    RouterLink
  ],
  templateUrl: './menu-language.component.html',
  styleUrl: './menu-language.component.css'
})
export class MenuLanguageComponent {
  apiUrl = environment.api;
  ukFlag = '/img/uk-logo.png';
  plFlag = '/img/poland-logo.png';

  constructor(private languageService: LanguageService) {}


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key)
  }

  setLanguage(language: string) {
    if(this.getCurrentLanguage() != language) {
      this.languageService.switchLanguage();
    }
  }

  getCurrentLanguage() {
    return this.languageService.getCurrentLanguage();
  }




}
