import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Favourites} from "../interfaces/favourites";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FavouriteService {
  private apiUrl = environment.api + '/api/favourite';

  constructor(private http:HttpClient) { }

  getUserFavourites(id: number) : Observable<Favourites>{
    return this.http.get<Favourites>(`${this.apiUrl}/all?userId=${id}`);
  }
}