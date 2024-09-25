import {Delivery} from "./delivery.interface";

export interface Restaurant {
  id: number;
  name: string;
  category: string;
  logoUrl: string;
  imageUrl: string;
  delivery: Delivery;
  restaurantOpinions: RestaurantOpinions[];
}




export interface RestaurantOpinions {
  id: number;
  qualityRating: number;
  deliveryRating: number;
  comment: string;
  createdAt: string;
}
