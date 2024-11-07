import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {CartService} from "./cart.service";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Order, OrderMenu} from "../interfaces/order";
import {BehaviorSubject} from "rxjs";
import {isPlatformBrowser} from "@angular/common";

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = environment.api + '/api/order';

  private orderMenus = new BehaviorSubject<OrderMenu[]>(this.loadOrderMenusFromLocalStorage());
  orderMenus$ = this.orderMenus.asObservable();

  constructor(private http: HttpClient, private cartService: CartService, @Inject(PLATFORM_ID) private platformId: Object) {}

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


  private loadOrderMenusFromLocalStorage(): OrderMenu[] {
    if (isPlatformBrowser(this.platformId)) {
      const orderMenus = localStorage.getItem('orderMenus');
      return orderMenus ? JSON.parse(orderMenus) : [];
    }
    return [];
  }

  private saveOrderMenus(orderMenus: OrderMenu[]) {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('orderMenus', JSON.stringify(orderMenus));
    }
  }

  updateOrderMenus(orderMenus: OrderMenu[]) {
    this.orderMenus.next(orderMenus);
    this.saveOrderMenus(orderMenus);
  }

  calculateOrderPrice(orderMenu: OrderMenu[], deliveryOption: string): { ordersPrice: number, deliveryPrice: string | null, totalPrice: number } {
    const ordersPrice = orderMenu.reduce((total, order) => {
      const additivePrice = this.cartService.calculateAdditivePrice(order.chooseAdditives || []);
      return total + ((order.menu.price + additivePrice) * (order.quantity || 1));
    }, 0);
    let deliveryPrice = null;

    typeof sessionStorage !== 'undefined' ? deliveryPrice = sessionStorage.getItem("deliveryPrice") : deliveryPrice = null;

      if (deliveryOption === 'delivery') {
        return {ordersPrice, deliveryPrice, totalPrice: ordersPrice + (deliveryPrice ? parseFloat(deliveryPrice) : 0)};
      } else {
        return {ordersPrice, deliveryPrice: null, totalPrice: ordersPrice};
      }
    }

}
