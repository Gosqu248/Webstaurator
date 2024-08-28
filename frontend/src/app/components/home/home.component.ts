import { Component } from '@angular/core';
import {LanguageService} from "../../services/language.service";
import {LanguageTranslations} from "../../interfaces/language.interface";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

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
