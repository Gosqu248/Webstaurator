import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {AddRestaurant, Restaurant} from "../../interfaces/restaurant";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {

  private apiUrl = environment.api + '/api/restaurants';
  constructor(private http: HttpClient) {}

  getRestaurantById(id: number): Observable<Restaurant> {
    return this.http.get<Restaurant>(`${this.apiUrl}/restaurant?id=${id}`);
  }

  getAllRestaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(`${this.apiUrl}`);
  }

  getLogo(id: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/logo?id=${id}`, { responseType: 'text' });
  }

  getRestaurantForEdit(id: number): Observable<AddRestaurant> {
    return this.http.get<AddRestaurant>(`${this.apiUrl}/edit?id=${id}`)
  }

  addRestaurant(restaurant: AddRestaurant) {
    return this.http.post<AddRestaurant>(`${this.apiUrl}`, restaurant).subscribe({
      next: () => {
        console.log('Restaurant added');
      },
      error: (error) => {
        console.error('Error adding restaurant:', error);
      }
    });
  }

  updateRestaurant(id: number, restaurant: AddRestaurant) {
    return this.http.put<AddRestaurant>(`${this.apiUrl}?id=${id}`, restaurant).subscribe({
      next: () => {
        console.log('Restaurant updated');
      },
      error: (error) => {
        console.error('Error updating restaurant:', error);
      }
    });
  }

  removeRestaurant(id: number) {
    return this.http.delete(`${this.apiUrl}?id=${id}`).subscribe({
      next: () => {
        console.log('Restaurant removed', id);
        window.location.reload();
      },
      error: (error) => {
        console.error('Error removing restaurant:', error);
      }
    });
  }
}
