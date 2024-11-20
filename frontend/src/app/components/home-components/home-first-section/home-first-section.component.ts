import {Component} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {environment} from "../../../../environments/environment";
import {LanguageService} from "../../../services/language.service";
import {Router, RouterLink} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {debounceTime, distinctUntilChanged, of, Subject, switchMap} from "rxjs";
import {AddressSuggestionsService} from "../../../services/address-suggestions.service";
import {NgForOf, NgIf} from "@angular/common";
import {Suggest} from "../../../interfaces/suggest";

@Component({
  selector: 'app-home-first-section',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './home-first-section.component.html',
  styleUrl: './home-first-section.component.css'
})
export class HomeFirstSectionComponent {
  currentLanguage: string;
  apiUrl = environment.api;
  background: string = '/img/background.webp';
  searchAddress: string = '';
  suggestions: Suggest[] = [];
  private searchSubject = new Subject<string>();


  constructor(private languageService: LanguageService,
              private suggestionsService: AddressSuggestionsService,
              private router: Router) {
    this.currentLanguage = this.languageService.getCurrentLanguage();

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((query) => this.fetchSuggestions(query))
    ).subscribe((results: Suggest[]) => {
      this.suggestions = results;
    });
  }

  searchRestaurants() {
    if (this.searchAddress !== '') {
      sessionStorage.setItem('searchAddress', this.searchAddress);
      this.router.navigate(['/restaurants'])
    }
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

  onKeydown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.searchRestaurants();
    }
  }

  onInputChange(query: string) {
    this.searchSubject.next(query);
  }

  fetchSuggestions(query: string) {
    if (query === '') {
      return of([]);
    }
    return this.suggestionsService.getSuggestions(query);
  }

  selectSuggestion(suggestion: string) {
    this.searchAddress = suggestion;
    this.searchRestaurants();
    this.suggestions = [];
  }

}
