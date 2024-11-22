import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Restaurant} from "../interfaces/restaurant";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {

  private apiUrl = environment.api + '/api/restaurant';
  constructor(private http: HttpClient) {}

  getRestaurantById(id: number): Observable<Restaurant> {
    return this.http.get<Restaurant>(`${this.apiUrl}/getRestaurant?id=${id}`);
  }

  getLogo(id: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/getLogo?id=${id}`, { responseType: 'text' });
  }

}
