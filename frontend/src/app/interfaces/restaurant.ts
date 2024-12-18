import {Menu} from "./menu";
import {Delivery, DeliveryHour} from "./delivery.interface";
import {RestaurantAddress} from "./restaurant-address";

export interface Restaurant {
  id: number;
  name: string;
  category: string;
  logoUrl: string;
  imageUrl: string;
}
export interface AddRestaurant {
  name: string;
  category: string;
  imageUrl: string;
  logoUrl: string;
  paymentMethods: string[];
  restaurantAddress: RestaurantAddress;
  delivery: Delivery;
  deliveryHours: DeliveryHour[];
  menu: Menu[];
}
