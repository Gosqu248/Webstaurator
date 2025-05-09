import {Component, OnInit} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {environment} from "../../../../environments/environment";
import {LanguageService} from "../../../services/state/language.service";
import {Router, RouterLink} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {debounceTime, distinctUntilChanged, of, Subject, switchMap} from "rxjs";
import {AddressSuggestionsService} from "../../../services/api/address-suggestions.service";
import {NgForOf, NgIf} from "@angular/common";
import {Suggest} from "../../../interfaces/suggest";
import {RestaurantAddressService} from "../../../services/api/restaurant-address.service";
import {SearchedRestaurantsService} from "../../../services/state/searched-restaurant.service";

@Component({
  selector: 'app-home-first-section',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './home-first-section.component.html',
  styleUrl: './home-first-section.component.css'
})
export class HomeFirstSectionComponent implements OnInit{
  currentLanguage: string;
  apiUrl = environment.api;
  background: string = '/img/background.webp';
  searchAddress: string = '';
  suggestions: Suggest[] = [];
  private searchSubject = new Subject<string>();


  constructor(private languageService: LanguageService,
              private suggestionsService: AddressSuggestionsService,
              private restaurantAddressService: RestaurantAddressService,
              private searchedRestaurantService: SearchedRestaurantsService,
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

  ngOnInit() {
    this.searchedRestaurantService.clearSearchedRestaurants();
  }

  searchRestaurants() {
    if (this.searchAddress !== '') {
      this.restaurantAddressService.searchRestaurant(this.searchAddress).subscribe((restaurants) => {
        this.searchedRestaurantService.setSearchedRestaurants(restaurants)
      });
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
    return this.suggestionsService.getSuggestions(query);
  }

  selectSuggestion(suggestion: string) {
    this.searchAddress = suggestion;
    this.searchRestaurants();
    this.suggestions = [];
  }

}
