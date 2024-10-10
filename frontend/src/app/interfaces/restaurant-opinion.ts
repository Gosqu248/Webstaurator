export interface RestaurantOpinion {
  id: number;
  qualityRating: number;
  deliveryRating: number;
  comment: string;
  createdAt: string;
  user: UserDTO;
}

export interface UserDTO {
  id: number;
  name: string;
}
