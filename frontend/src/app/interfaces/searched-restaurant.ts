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
}
