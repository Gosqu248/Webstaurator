import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {Favourites} from "../../interfaces/favourites";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FavouriteService {
  private apiUrl = environment.api + '/api/favourites';

  constructor(private http:HttpClient) { }

  getUserFavourites(id: number): Observable<Favourites[]>{
    return this.http.get<Favourites[]>(`${this.apiUrl}?userId=${id}`);
  }

  isFavorite(userId: number, restaurantId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/isFavourite?userId=${userId}&restaurantId=${restaurantId}`);
  }

  addFavourite(userId: number, restaurantId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}`, {
      userId,
      restaurantId
    });
  }

  deleteFavourite(userId: number, restaurantId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}`, {
      body: {
        userId,
        restaurantId
      }
    });
  }

}
