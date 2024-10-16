export interface Menu {
  id: number;
  name: string;
  category: string;
  ingredients: string;
  price: number;
  image: string;
  restaurantId: number;
  additives?: Additives[];
  chooseAdditives?: Additives[];
  quantity?: number;
}


export interface Additives {
  id: number;
  name: string;
  value: string;
  price?: number;
}
