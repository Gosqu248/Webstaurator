// language.service.ts
import { Injectable } from '@angular/core';
import { Translations } from "../interfaces/language.interface";

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private currentLanguage: 'pl' | 'en' = 'pl';

  translations: Translations = {
    pl: {
      chooseLanguage: 'Wybierz język',
      polish: 'Polski',
      english: 'Angielski'
    },
    en: {
      chooseLanguage: 'Choose language',
      polish: 'Polish',
      english: 'English'
    }
  };

  getCurrentLanguage() {
    return this.currentLanguage;
  }

  switchLanguage() {
    this.currentLanguage = this.currentLanguage === 'pl' ? 'en' : 'pl';
  }

  getTranslation<K extends keyof Translations[typeof this.currentLanguage]>(key: K) {
    return this.translations[this.currentLanguage][key];
  }
}
