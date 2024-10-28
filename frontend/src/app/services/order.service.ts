import { Injectable } from '@angular/core';
import {CartService} from "./cart.service";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Order, OrderMenu} from "../interfaces/order";

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private orderMenus: OrderMenu[] = [];
  private apiUrl = environment.api + '/api/order';


  constructor(private http: HttpClient, private cartService: CartService) {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      const storedOrders = sessionStorage.getItem('orderMenu');
      this.orderMenus = storedOrders ? JSON.parse(storedOrders) : [];
    }
  }

  getUserOrders(userId: number) {
    return this.http.get<Order[]>(`${this.apiUrl}/getUserOrders?userId=${userId}`);
  }

  createOrder(order: Order) {
    console.log('Order created:', order);
    return this.http.post<Order>(`${this.apiUrl}/createOrder`, order).subscribe({
      next: () => {
        this.cartService.deleteCartFromLocalStorage();
      },
      error: (error) => {
        console.error('Error creating order:', error);
      }
    });
  }
  setOrders(orderMenu: OrderMenu[]) {
    this.orderMenus = orderMenu;
    sessionStorage.setItem('orderMenu', JSON.stringify(orderMenu));

  }
  getOrders(): OrderMenu[] {
    return this.orderMenus;
  }

  calculateOrderPrice(orderMenu: OrderMenu[]): { ordersPrice: number, deliveryPrice: string | null, totalPrice: number } {
    const ordersPrice = orderMenu.reduce((total, order) => {
      const additivePrice = this.cartService.calculateAdditivePrice(order.chooseAdditives || []);
      return total + ((order.menu.price + additivePrice) * (order.quantity || 1));
    }, 0);
    const deliveryPrice = sessionStorage.getItem("deliveryPrice")
    const totalPrice = ordersPrice + (deliveryPrice ? parseFloat(deliveryPrice) : 0);
    return { ordersPrice, deliveryPrice, totalPrice };
  }
}
