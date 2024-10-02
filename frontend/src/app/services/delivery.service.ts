import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {DeliveryHour} from "../interfaces/delivery.interface";

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  private apiUrl = environment.api + '/api/delivery';
  constructor(private http:HttpClient) { }
  getDeliveryTIme(restaurantId: number) {
    return this.http.get<DeliveryHour[]>(`${this.apiUrl}/time`, {
      params: {
        restaurantId: restaurantId
      }
    });
  }
}
