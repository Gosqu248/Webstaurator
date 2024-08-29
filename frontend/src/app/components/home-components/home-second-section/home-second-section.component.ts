import { Component } from '@angular/core';
import {environment} from "../../../../environments/environment";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";

@Component({
  selector: 'app-home-second-section',
  standalone: true,
  imports: [],
  templateUrl: './home-second-section.component.html',
  styleUrl: './home-second-section.component.css'
})
export class HomeSecondSectionComponent {
  currentLanguage: string;


  constructor(private languageService: LanguageService) {
    this.currentLanguage = this.languageService.getCurrentLanguage();

  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

}
