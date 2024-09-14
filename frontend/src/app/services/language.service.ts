// language.service.ts
import { Injectable } from '@angular/core';
import { Translations } from "../interfaces/language.interface";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private currentLanguage: 'pl' | 'en' = 'pl';
  private languageChangeSubject = new BehaviorSubject<'pl' | 'en'>('pl');
  languageChange$ = this.languageChangeSubject.asObservable();



  constructor() {
    if(this.isLocalStorageAvailable()) {
      const savedLanguage = localStorage.getItem('language');
      if(savedLanguage === 'pl' || savedLanguage === 'en') {
        this.currentLanguage = savedLanguage;
        this.languageChangeSubject.next(this.currentLanguage);
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
      second2: 'Wystarczą 3 kroki!',
      step1_1: 'Podaj swoją lokalizację',
      step1_2: 'Znajdziemy wszystkie restauracje w Twojej okolicy.',
      step2_1: 'Znajdź to, czego szukasz',
      step2_2: 'Szukaj interesującego Cię jedzenia w różnych restauracjach.',
      step3_1: 'Złóż zamówienie',
      step3_2: 'Wybierz dostawę lub osobisty odbiór.',
      third1: 'Dlaczego warto zamawiać na',
      third2: 'Webstraurator?',
      why1_1: 'Szybkość i wygoda',
      why1_2: 'Zamów jedzenie poprzez kilka kliknięć bez wychodzenia z domu.',
      why2_1: 'Bogata oferta',
      why2_2: 'Ponad 10 000 restauracji do wyboru w całym kraju.',
      why3_1: 'Gwarancja jakości',
      why3_2: 'Współpracujemy tylko z zaufanymi restauracjami, które oferują najwyższą jakość.',
      why4_1: 'Bezpieczeństwo',
      why4_2: 'Płatności online są szyfrowane, a dane są chronione.',
      myAccount: 'Moje konto',
      createAccount: 'Załóż konto',
      login: 'Zaloguj się',
      profile: 'Twoje dane',
      orders: 'Zamówienia',
      favourites: 'Ulubione',
      opinions: 'Opinie',
      help: 'Pomoc i Regulaminy',
      changeLanguage: 'Zmień język',
      language: 'Język',
      name: 'Nazwisko',
      email: 'E-mail',
      password: 'Hasło',
      confirmPassword: 'Potwierdź hasło',
      registerAccept: "Klikając przycisk 'Załóż konto', akceptujesz regulamin serwisu i polityką prywatności.",
      haveAccount: 'Mam już konto',
      emailError: 'Nieprawidłowy adres email',
      passwordError: 'Hasło musi zawierać co najmniej 8 znaków, jedną wielką literę i jeden znak specjalny',
      confirmPasswordError: 'Hasła muszą być takie same',
      loginError: 'Nieprawidłowy login lub hasło',
      logout: 'Wyloguj',
      newName: "Zaktualizuj nazwisko",
      wannaChangePassword: 'Chcesz zmienić hasło?',
      clickHere: 'Kliknij tutaj',
      changePassword: "Zmień hasło",
      oldPassword: "Stare hasło",
      newPassword: "Nowe hasło",
      changePasswordError: "Błąd zmiany hasła",
      addresses: "Adresy",
      addAddress: "Dodaj adres",
      street: "Ulica: *",
      houseNumber: "Numer mieszkania: *",
      floorNumber: "Piętro",
      accessCode: "Kod dostępu",
      city: "Miasto: *",
      zipCode: "Kod pocztowy: *",
      phoneNumber: "Numer telefonu: *",
      changeAddress: "Edytuj adres",
      delete: "Usuń",
      updateAddress: "Zaktualizuj adres"
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
      second2: 'Just 3 steps!',
      step1_1: 'Enter your location',
      step1_2: 'We will find all the restaurants in your area',
      step2_1: 'Find what you are looking for',
      step2_2: 'Search for the food you are interested in in different restaurants.',
      step3_1: 'Place an order',
      step3_2: 'Choose delivery or personal pickup.',
      third1: 'Why is it worth ordering at',
      third2: 'Webstraurator?',
      why1_1: 'Speed and convenience',
      why1_2: 'Order food in a few clicks without leaving your home.',
      why2_1: 'Rich offer',
      why2_2: 'Over 10,000 restaurants to choose from nationwide.',
      why3_1: 'Quality guarantee',
      why3_2: 'We only cooperate with trusted restaurants that offer the highest quality.',
      why4_1: 'Security',
      why4_2: 'Online payments are encrypted and data is protected.',
      myAccount: 'My account',
      createAccount: 'Create account',
      login: 'Log in',
      profile: 'Your data',
      orders: 'Orders',
      favourites: 'Favourites',
      opinions: 'Opinions',
      help: 'Help and Terms',
      changeLanguage: 'Change language',
      language: 'Language',
      name: 'Name',
      email: 'Email address',
      password: 'Password',
      confirmPassword: 'Confirm password',
      registerAccept: "By clicking the 'Create account' button, you accept the terms of service and privacy policy.",
      haveAccount: 'I already have an account',
      emailError: 'Invalid email address',
      passwordError: 'The password must be at least 8 characters long, contain one uppercase letter and one special character',
      confirmPasswordError: 'Passwords must match',
      loginError: 'Invalid login or password',
      logout: 'Logout',
      newName: "Update name",
      wannaChangePassword: 'Do you want to change your password?',
      clickHere: 'Click here',
      changePassword: "Change password",
      oldPassword: "Old password",
      newPassword: "New password",
      changePasswordError: "Error changing password",
      addresses: "Addresses",
      addAddress: "Add address",
      street: "Street: *",
      houseNumber: "House number: *",
      floorNumber: "Floor",
      accessCode: "Access code",
      city: "City: *",
      zipCode: "Zip code: *",
      phoneNumber: "Phone number: *",
      changeAddress: "Edit address",
      delete: "Delete",
      updateAddress: "Update address"

    }
  };

  getCurrentLanguage() {
    return this.currentLanguage;
  }

  switchLanguage() {
    this.currentLanguage = this.currentLanguage === 'pl' ? 'en' : 'pl';
    localStorage.setItem('language', this.currentLanguage);
    this.languageChangeSubject.next(this.currentLanguage);
  }

  getTranslation<K extends keyof Translations[typeof this.currentLanguage]>(key: K) {
    return this.translations[this.currentLanguage][key];
  }

  isLocalStorageAvailable() {
    return typeof localStorage !== 'undefined';
  }
}
