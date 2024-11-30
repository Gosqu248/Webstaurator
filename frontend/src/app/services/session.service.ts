import { Injectable } from '@angular/core';
import {Restaurant} from "../interfaces/restaurant";
import {Delivery} from "../interfaces/delivery.interface";

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  setSessionRestaurant(restaurantId: number, restaurant: Restaurant, restaurantDelivery: Delivery, isOpen: boolean): void {
    if (restaurantId && restaurant && restaurantDelivery && typeof sessionStorage !== 'undefined') {
      console.log(restaurant.name);
      sessionStorage.setItem('restaurantName', restaurant.name);
      sessionStorage.setItem('deliveryMin', restaurantDelivery.deliveryMinTime.toString());
      sessionStorage.setItem('deliveryMax', restaurantDelivery.deliveryMaxTime.toString());
      sessionStorage.setItem('deliveryPrice', restaurantDelivery.deliveryPrice.toString());
      sessionStorage.setItem('minPrice', restaurantDelivery.minimumPrice.toString());
      sessionStorage.setItem('pickupTime', restaurantDelivery.pickupTime.toString());
      sessionStorage.setItem('isOpen', isOpen.toString());
    }
  }
}
