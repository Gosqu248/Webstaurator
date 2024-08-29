import { Component } from '@angular/core';
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";

@Component({
  selector: 'app-home-third-section',
  standalone: true,
  imports: [],
  templateUrl: './home-third-section.component.html',
  styleUrl: './home-third-section.component.css'
})
export class HomeThirdSectionComponent {
  currentLanguage: string;


  constructor(private languageService: LanguageService) {
    this.currentLanguage = this.languageService.getCurrentLanguage();

  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
