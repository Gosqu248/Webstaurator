import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs";
import {RestaurantAddress} from "../interfaces/restaurant-address";
import {SearchedRestaurant} from "../interfaces/searched-restaurant";
import {Coordinates} from "../interfaces/coordinates";

@Injectable({
  providedIn: 'root'
})
export class RestaurantAddressService {
  private apiUrl = environment.api + '/api/restaurantAddress';

  searchedRestaurants = new BehaviorSubject<SearchedRestaurant[]>(this.loadRestaurantsFromSessionStorage());
  searchedRestaurants$ = this.searchedRestaurants.asObservable();


  constructor(private http: HttpClient) {}

  getRestaurantAddress(id: number): Observable<RestaurantAddress>{
    return this.http.get<RestaurantAddress>(`${this.apiUrl}/get?restaurantId=${id}`);
  }

  searchRestaurant(address: string): Observable<SearchedRestaurant[]>{
    return this.http.get<SearchedRestaurant[]>(`${this.apiUrl}/search?address=${address}`);
  }

  getCoordinates(id: number): Observable<Coordinates>{
    return this.http.get<Coordinates>(`${this.apiUrl}/getCoordinates?restaurantId=${id}`);
  }


  setSearchedRestaurants(restaurants: SearchedRestaurant[]){
    sessionStorage.setItem('searchedRestaurants', JSON.stringify(restaurants));
    this.searchedRestaurants.next(restaurants);
  }

  loadRestaurantsFromSessionStorage() {
    if (typeof window !== 'undefined' && window.localStorage) {
      const restaurants = sessionStorage.getItem('searchedRestaurants');
      return restaurants ? JSON.parse(restaurants) : [];
    }
  }

  removeRestaurantFromSessionStorage() {
    if (typeof window !== 'undefined' && window.localStorage) {
      sessionStorage.removeItem('searchedRestaurants');
      this.searchedRestaurants.next([]);
    }
  }

}
