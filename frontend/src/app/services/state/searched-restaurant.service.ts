import { Injectable } from '@angular/core';
import { BehaviorSubject} from "rxjs";
import { SearchedRestaurant } from "../../interfaces/searched-restaurant";

@Injectable({
  providedIn: 'root'
})
export class SearchedRestaurantsService {
  private searchedRestaurantsSubject = new BehaviorSubject<SearchedRestaurant[]>(this.loadRestaurantsFromSessionStorage());
  searchedRestaurants$ = this.searchedRestaurantsSubject.asObservable();

  constructor() {}

  get searchedRestaurants(): SearchedRestaurant[] {
    return this.searchedRestaurantsSubject.getValue();
  }

  getSearchedRestaurant(id: number): SearchedRestaurant {
    const restaurant = this.searchedRestaurants.find(restaurant => restaurant.restaurantId === id);
    return restaurant ? restaurant : {} as SearchedRestaurant;
  }

  setSearchedRestaurants(restaurants: SearchedRestaurant[]): void {
    sessionStorage.setItem('searchedRestaurants', JSON.stringify(restaurants));
    this.searchedRestaurantsSubject.next(restaurants);
  }

  loadRestaurantsFromSessionStorage(): SearchedRestaurant[] {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      const restaurants = sessionStorage.getItem('searchedRestaurants');
      return restaurants ? JSON.parse(restaurants) : [];
    }
    return [];
  }

  clearSearchedRestaurants(): void {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      sessionStorage.removeItem('searchedRestaurants');
      this.searchedRestaurantsSubject.next([]);
    }
  }
}
