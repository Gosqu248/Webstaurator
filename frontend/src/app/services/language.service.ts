// language.service.ts
import { Injectable } from '@angular/core';
import { Translations } from "../interfaces/language.interface";

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private currentLanguage: 'pl' | 'en' = 'pl';

  constructor() {
    if(this.isLocalStorageAvailable()) {
      const savedLanguage = localStorage.getItem('language');
      if(savedLanguage === 'pl' || savedLanguage === 'en') {
        this.currentLanguage = savedLanguage;
      }
    }
  }

  translations: Translations = {
    pl: {
      chooseLanguage: 'Wybierz język',
      polish: 'Polski',
      english: 'Angielski',
      home1: 'Nie wiesz co dziś zjeść?',
      home2: 'Znajdź restauracje w pobliżu dla siebie',
      address: 'Podaj adres...',
      search: 'Szukaj',
      second1: 'Zamawianie jest proste.',
      second2: 'Wystarczą 3 kroki!'
    },
    en: {
      chooseLanguage: 'Choose language',
      polish: 'Polish',
      english: 'English',
      home1: 'Don\'t know what to eat today?',
      home2: 'Find restaurants nearby for yourself',
      address: 'Enter address...',
      search: 'Search',
      second1: 'Ordering is easy.',
      second2: 'Just 3 steps!'

    }
  };

  getCurrentLanguage() {
    return this.currentLanguage;
  }

  switchLanguage() {
    this.currentLanguage = this.currentLanguage === 'pl' ? 'en' : 'pl';
    localStorage.setItem('language', this.currentLanguage);
  }

  getTranslation<K extends keyof Translations[typeof this.currentLanguage]>(key: K) {
    return this.translations[this.currentLanguage][key];
  }

  isLocalStorageAvailable() {
    return typeof localStorage !== 'undefined';
  }
}
