import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {Delivery, DeliveryHour} from "../../interfaces/delivery.interface";

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  private apiUrl = environment.api + '/api/delivery';
  constructor(private http:HttpClient) { }


  getDelivery(restaurantId: number) {
    return this.http.get<Delivery>(`${this.apiUrl}?restaurantId=${restaurantId}`);
  }

  getDeliveryTIme(restaurantId: number) {
    return this.http.get<DeliveryHour[]>(`${this.apiUrl}/time`, {
      params: {
        restaurantId: restaurantId
      }
    });
  }

  checkIfOpen(deliveryTime: DeliveryHour[] | undefined): boolean {
    const currentTime = new Date();
    const currentDay = currentTime.getDay(); // 0: Sunday, 1: Monday, ..., 6: Saturday
    const currentHour = currentTime.getHours();
    const currentMinutes = currentTime.getMinutes();
    const currentTotalMinutes = currentHour * 60 + currentMinutes;

    if (!deliveryTime) {
      console.error('Delivery time data is not available.');
      return false;
    }

    const todayDeliveryHour = deliveryTime.find(d => d.dayOfWeek === currentDay); // Adjust for Sunday

    if (todayDeliveryHour) {
      const openingTime = todayDeliveryHour.openTime.split(':');
      const closingTime = todayDeliveryHour.closeTime.split(':');

      const openingTotalMinutes = parseInt(openingTime[0]) * 60 + parseInt(openingTime[1]);
      const closingTotalMinutes = parseInt(closingTime[0]) * 60 + parseInt(closingTime[1]);

      return currentTotalMinutes >= openingTotalMinutes && currentTotalMinutes <= closingTotalMinutes;
    } else {
      return false;
    }
  }
}
