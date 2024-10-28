import {Additives, Menu} from "./menu";
import {Restaurant} from "./restaurant";
import {User} from "./user.interface";

export interface Order {
  paymentMethod: string;
  status: OrderStatus;
  totalPrice: number;
  deliveryTime: string;
  comment: string;
  orderMenus: OrderMenu[];
  user: User;
  restaurant: Restaurant;

}

export interface OrderMenu {
  menu: Menu;
  quantity: number;
  chooseAdditives: Additives[];
}


export enum OrderStatus {
  zaplacone = "zapłacone",
  niezaplacone = "niezapłacone",
}
