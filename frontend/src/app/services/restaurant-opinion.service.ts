import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {RestaurantOpinion} from "../interfaces/restaurant-opinion";

@Injectable({
  providedIn: 'root'
})
export class RestaurantOpinionService {
  private apiUrl = environment.api + '/api/opinions';
  constructor(private http: HttpClient) {}

  getRestaurantOpinions(id: number): Observable<RestaurantOpinion[]>{
    return this.http.get<RestaurantOpinion[]>(`${this.apiUrl}/all?restaurantId=${id}`);
  }


}
