import {Additives, Menu} from "./menu";
import {Restaurant} from "./restaurant";
import {User, UserDTO} from "./user.interface";
import {UserAddress} from "./user.address.interface";

export interface Order {
  paymentMethod: string;
  status: OrderStatus;
  totalPrice: number;
  deliveryTime: string;
  deliveryOption: string;
  comment: string;
  orderMenus: OrderMenu[];
  user: UserDTO;
  userAddress: UserAddress | null;
  restaurant: Restaurant;
  paymentId: string | null;
}

export interface OrderRequest {
  userId: number;
  userAddressId: number | null;
  restaurantId: number;
  orderMenus: OrderMenuRequest[];
  deliveryOption: string;
  deliveryTime: string;
  paymentId: string | null;
  paymentMethod: string;
  status: OrderStatus;
  totalPrice: number;
  comment: string;
}

export interface OrderMenuRequest {
  quantity: number;
  menuId: number;
  chooseAdditivesId: number[];
}

export interface OrderMenu {
  menu: Menu;
  quantity: number;
  chooseAdditives: Additives[];
}

export interface OrderDTO {
  id: number;
  deliveryOption: string;
  deliveryTime: string;
  orderDate: string;
  orderMenus: OrderMenu[];
  paymentId: string;
  paymentMethod: string;
  restaurantId: number;
  restaurantName: string;
  restaurantLogo: string;
  status: OrderStatus;
  totalPrice: number;
  userAddress: UserAddress;
  hasOpinion: boolean;
}

export interface AdminOrderDTO {
  id: number;
  deliveryOption: string;
  deliveryTime: string;
  orderDate: string;
  orderMenus: OrderMenu[];
  paymentId: string;
  paymentMethod: string;
  restaurantId: number;
  restaurantName: string;
  status: OrderStatus;
  totalPrice: number;
  city: string;
  houseNumber: string;
  street: string;
  zipCode: string;
  restaurantLogo: string;
  userName: string;
  userEmail: string;
  userPhone: string;
}

export enum OrderStatus {
  zaplacone = "zapłacone",
  niezaplacone = "niezapłacone",
}
