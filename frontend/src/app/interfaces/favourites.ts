export interface Favourites {
  id: number;
  restaurantId: number;
  restaurantName: string;
  restaurantCategory: string;
  restaurantLogoUrl: string;
  street: string;
  flatNumber: string;
  restaurantOpinion: RestaurantOpinionDTO[];
}

export interface RestaurantOpinionDTO {
  id: number;
  qualityRating: number;
  deliveryRating: number;
}
