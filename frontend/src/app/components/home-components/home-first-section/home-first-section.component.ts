import {Component} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {environment} from "../../../../environments/environment";
import {LanguageService} from "../../../services/language.service";

@Component({
  selector: 'app-home-first-section',
  standalone: true,
  imports: [],
  templateUrl: './home-first-section.component.html',
  styleUrl: './home-first-section.component.css'
})
export class HomeFirstSectionComponent {
  currentLanguage: string;
  apiUrl = environment.api;
  background: string = '/img/background.webp';

  constructor(private languageService: LanguageService) {
    this.currentLanguage = this.languageService.getCurrentLanguage();

  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

}
