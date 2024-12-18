export interface Menu {
  id?: number;
  name: string;
  category: string;
  ingredients: string;
  price: number;
  image: string;
  additives?: Additives[];
}


export interface Additives {
  id?: number;
  name: string;
  value: string;
  price: number;
}
