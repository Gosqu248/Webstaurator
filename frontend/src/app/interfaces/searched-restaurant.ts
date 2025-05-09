import {RestaurantAddress} from "./restaurant-address";
import {Delivery, DeliveryHour} from "./delivery.interface";
import {PaymentMethod} from "./paymentMethod";

export interface SearchedRestaurant {
  restaurantId: number;
  name: string;
  category: string;
  distance: number;
  pickup: boolean;
  rating: number;
  deliveryPrice: number;
  lat: number;
  lon: number;
  restaurantAddress?: RestaurantAddress;
  deliveryHours?: DeliveryHour[];
  delivery?: Delivery;
  paymentMethods?: PaymentMethod[];
}
