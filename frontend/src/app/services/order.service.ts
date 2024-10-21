import { Injectable } from '@angular/core';
import {Menu} from "../interfaces/menu";
import {CartService} from "./cart.service";

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private orders: Menu[] = [];

  constructor(private cartService: CartService) {
    const storedOrders = sessionStorage.getItem('orders');
    this.orders = storedOrders ? JSON.parse(storedOrders) : [];
  }

  setOrders(orders: Menu[]) {
    this.orders = orders;
    sessionStorage.setItem('orders', JSON.stringify(orders));

  }

  getOrders(): Menu[] {
    return this.orders;
  }

  calculateOrderPrice(orders: Menu[]): { ordersPrice: number, deliveryPrice: string | null, totalPrice: number } {
    const ordersPrice = orders.reduce((total, order) => {
      const additivePrice = this.cartService.calculateAdditivePrice(order.chooseAdditives || []);
      return total + ((order.price + additivePrice) * (order.quantity || 1));
    }, 0);
    const deliveryPrice = sessionStorage.getItem("deliveryPrice");
    const totalPrice = ordersPrice + (deliveryPrice ? parseFloat(deliveryPrice) : 0);
    return { ordersPrice, deliveryPrice, totalPrice };
  }
}
