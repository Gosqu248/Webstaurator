import { Injectable } from '@angular/core';
import {Menu} from "../interfaces/menu";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private currentRestaurantId: number | null = null;

  cart = new BehaviorSubject<Menu[]>(this.loadCartFromLocalStorage());
  cart$ = this.cart.asObservable();

  setCurrentRestaurantId(restaurantId: number) {
    this.currentRestaurantId = restaurantId;
    this.cart.next(this.loadCartFromLocalStorage());
  }

  addToCart(item: Menu) {
    const currentCart = this.cart.value;
    const existingItem = currentCart.find(i => i.name === item.name);

    if(existingItem) {
      existingItem.quantity = (existingItem.quantity || 1) + 1;
    } else {
      item.quantity = 1;
      currentCart.push(item);
    }
    this.cart.next(currentCart);
    this.saveCartToLocalStorage(currentCart);
  }

  removeFromCart(item: Menu) {
    const currentCart = this.cart.value;
    const existingItem = currentCart.find(i => i.name === item.name);

    if(existingItem) {
      if(existingItem.quantity && existingItem.quantity > 1) {
        existingItem.quantity -= 1;
      } else {
        currentCart.splice(currentCart.indexOf(existingItem), 1);
      }
    }
    this.cart.next(currentCart);
    this.saveCartToLocalStorage(currentCart);
  }

  private saveCartToLocalStorage(cart: Menu[]) {
    if (this.currentRestaurantId !== null && typeof localStorage !== 'undefined') {
      localStorage.setItem(`cart_${this.currentRestaurantId}`, JSON.stringify(cart));
    }
  }

  private loadCartFromLocalStorage(): Menu[] {
    if (this.currentRestaurantId !== null && typeof localStorage !== 'undefined') {
      const cart = localStorage.getItem(`cart_${this.currentRestaurantId}`);
      return cart ? JSON.parse(cart) : [];
    }
    return [];
  }


}
