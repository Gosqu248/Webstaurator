export interface Menu {
  id: number;
  name: string;
  category: string;
  ingredients: string;
  price: number;
  image: string;
  restaurantId: number;
  quantity?: number;
}
