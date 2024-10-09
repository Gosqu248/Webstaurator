export interface Favourites {
  id: number;
  restaurantId: number;
  restaurantName: string;
  restaurantCategory: string;
  restaurantLogoUrl: string;
  street: string;
  flatNumber: string;
  restaurantOpinions: RestaurantOpinionDTO[];
}

export interface RestaurantOpinionDTO {
  id: number;
  qualityRating: number;
  deliveryRating: number;
}
