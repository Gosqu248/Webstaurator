import {Additives, Menu} from "./menu";
import {Restaurant} from "./restaurant";
import {User} from "./user.interface";
import {UserAddress} from "./user.address.interface";

export interface Order {
  paymentMethod: string;
  status: OrderStatus;
  totalPrice: number;
  deliveryTime: string;
  deliveryOption: string;
  comment: string;
  orderMenus: OrderMenu[];
  user: User;
  userAddress: UserAddress;
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
