import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Menu} from "../../interfaces/menu";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private apiUrl = environment.api + '/api/menus';
  constructor(private http: HttpClient) {}

  getMenuByRestaurantId(id: number): Observable<Menu[]>{
    return this.http.get<Menu[]>(`${this.apiUrl}/restaurant?restaurantId=${id}`);
  }

  getCategories(id: number): Observable<string[]>{
    return this.http.get<string[]>(`${this.apiUrl}/categories?restaurantId=${id}`);
  }
}
