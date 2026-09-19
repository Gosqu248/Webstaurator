import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {CartService} from "../state/cart.service";
import {environment} from "../../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AdminOrderDTO, Order, OrderDTO, OrderMenu, OrderRequest} from "../../interfaces/order";
import {BehaviorSubject} from "rxjs";
import {isPlatformBrowser} from "@angular/common";

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = environment.api + '/api/orders';

  private orderMenus = new BehaviorSubject<OrderMenu[]>(this.loadOrderMenusFromLocalStorage());
  orderMenus$ = this.orderMenus.asObservable();


  constructor(private http: HttpClient, private cartService: CartService, @Inject(PLATFORM_ID) private platformId: Object) {}

  getUserOrders(token: string) {
    const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
    return this.http.get<OrderDTO[]>(`${this.apiUrl}/user`, {headers});
  }

  createOrder(order: OrderRequest) {
    order.totalPrice = parseFloat(order.totalPrice.toFixed(2));
    return this.http.post<Order>(`${this.apiUrl}`, order).subscribe({
      next: () => {
        this.cartService.deleteCartFromLocalStorage();
      },
      error: (error) => {
        console.error('Error creating order:', error);
      }
    });
  }

  getAllOrders() {
    return this.http.get<AdminOrderDTO[]>(`${this.apiUrl}`)
  }

  updateOrderStatus(orderId: number) {
    return this.http.put(`${this.apiUrl}?orderId=${orderId}`, {});
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

  calculateOrderPrice(orderMenu: OrderMenu[], deliveryOption: string, delPrice: number): { ordersPrice: number, deliveryPrice: number, totalPrice: number } {
    const ordersPrice = orderMenu.reduce((total, order) => {
      const additivePrice = this.cartService.calculateAdditivePrice(order.chooseAdditives || []);
      return total + ((order.menu.price + additivePrice) * (order.quantity || 1));
    }, 0);

      if (deliveryOption === 'delivery') {
        return {ordersPrice, deliveryPrice: delPrice, totalPrice: ordersPrice + delPrice};
      } else {
        return {ordersPrice, deliveryPrice: 0, totalPrice: ordersPrice};
      }
    }
}
