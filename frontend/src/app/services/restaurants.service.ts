import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Restaurant} from "../interfaces/restaurant";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RestaurantsService {

  private apiUrl = environment.api + '/api/restaurant';
  constructor(private http: HttpClient) {}

  getDeliveryRestaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(`${this.apiUrl}/allDelivery`);
  }
  getPickupRestaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(`${this.apiUrl}/allPickup`);
  }

  getDeliveryCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/delivery-categories`);
  }

  getPickupCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/pickup-categories`);
  }

  getRestaurantById(id: number): Observable<Restaurant> {
    return this.http.get<Restaurant>(`${this.apiUrl}/getRestaurant?id=${id}`);
  }

}
