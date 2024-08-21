import { Component } from '@angular/core';
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  apiUrl = environment.api;
  logo = '/img/webstaurator-logo.png';

  currentLanguage = 'pl';
  currentFlag = '/img/poland.png';
  showLanguageDropdown = false;

  toggleLanguageDropdown() {
    this.showLanguageDropdown = !this.showLanguageDropdown;
  }

  setLanguage(language: string) {
    this.currentLanguage = language;
    this.currentFlag = language === 'pl' ? '/img/poland.png' : '/img/uk.png';
    this.showLanguageDropdown = false;
  }


}
