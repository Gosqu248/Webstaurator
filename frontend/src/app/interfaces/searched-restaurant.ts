export interface SearchedRestaurant {
  restaurantId: number;
  name: string;
  category: string;
  latitude: number;
  longitude: number;
  distance: number;
  pickup: boolean;
}
