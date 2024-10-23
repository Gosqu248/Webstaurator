import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {RestaurantAddress} from "../interfaces/restaurant-address";

@Injectable({
  providedIn: 'root'
})
export class RestaurantAddressService {
  private apiUrl = environment.api + '/api/restaurantAddress';
  constructor(private http: HttpClient) {}

  getRestaurantAddress(id: number): Observable<RestaurantAddress>{
    return this.http.get<RestaurantAddress>(`${this.apiUrl}/get?restaurantId=${id}`);
  }

}
