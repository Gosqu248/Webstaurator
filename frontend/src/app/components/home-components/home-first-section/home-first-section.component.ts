import {Component} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {environment} from "../../../../environments/environment";
import {LanguageService} from "../../../services/language.service";
import {Router, RouterLink} from "@angular/router";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-home-first-section',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule
  ],
  templateUrl: './home-first-section.component.html',
  styleUrl: './home-first-section.component.css'
})
export class HomeFirstSectionComponent {
  currentLanguage: string;
  apiUrl = environment.api;
  background: string = '/img/background.webp';
  searchAddress: string = '';

  constructor(private languageService: LanguageService, private router: Router) {
    this.currentLanguage = this.languageService.getCurrentLanguage();
  }

  searchRestaurants() {
    sessionStorage.setItem('searchAddress', this.searchAddress);
    this.router.navigate(['/restaurants'])
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

}
