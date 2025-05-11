import { Injectable } from '@angular/core';
import { BehaviorSubject} from "rxjs";
import { SearchedRestaurant } from "../../interfaces/searched-restaurant";

@Injectable({
  providedIn: 'root'
})
export class SearchedRestaurantsService {
  private selectedRestaurantSubject = new BehaviorSubject<SearchedRestaurant>(this.loadSelectedRestaurantFromSessionStorage());
  selectedRestaurant$ = this.selectedRestaurantSubject.asObservable();

  private searchedRestaurantsSubject = new BehaviorSubject<SearchedRestaurant[]>(this.loadRestaurantsFromSessionStorage());
  searchedRestaurants$ = this.searchedRestaurantsSubject.asObservable();

  constructor() {}

  getSelectedRestaurant(): SearchedRestaurant {
    return this.selectedRestaurantSubject.getValue();
  }

  getSearchedRestaurant(id: number): SearchedRestaurant {
    const restaurant = this.searchedRestaurants.find(restaurant => restaurant.restaurantId === id);
    return restaurant ? restaurant : {} as SearchedRestaurant;
  }

  setSelectedRestaurant(id: number): void {
    const restaurant = this.searchedRestaurants.find(restaurant => restaurant.restaurantId === id)
    sessionStorage.setItem('selectedRestaurant', JSON.stringify(restaurant));
    restaurant ? this.selectedRestaurantSubject.next(restaurant) : this.selectedRestaurantSubject.next({} as SearchedRestaurant);
  }

  setSearchedRestaurants(restaurants: SearchedRestaurant[]): void {
    sessionStorage.setItem('searchedRestaurants', JSON.stringify(restaurants));
    this.searchedRestaurantsSubject.next(restaurants);
  }

  get searchedRestaurants(): SearchedRestaurant[] {
    return this.searchedRestaurantsSubject.getValue();
  }

  loadRestaurantsFromSessionStorage(): SearchedRestaurant[] {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      const restaurants = sessionStorage.getItem('searchedRestaurants');
      return restaurants ? JSON.parse(restaurants) : [];
    }
    return [];
  }

  loadSelectedRestaurantFromSessionStorage(): SearchedRestaurant {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      const restaurant = sessionStorage.getItem('selectedRestaurant');
      return restaurant ? JSON.parse(restaurant) : {} as SearchedRestaurant;
    }
    return {} as SearchedRestaurant;
  }

  clearSearchedRestaurants(): void {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      sessionStorage.removeItem('searchedRestaurants');
      this.searchedRestaurantsSubject.next([]);
    }
  }
}
