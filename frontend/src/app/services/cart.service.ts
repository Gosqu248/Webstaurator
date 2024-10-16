import { Injectable } from '@angular/core';
import {Additives, Menu} from "../interfaces/menu";
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

  addToCart(item: Menu, quantity: number = 1) {
    const currentCart = this.cart.value;
    const clonedItem = { ...item, chooseAdditives: [...(item.chooseAdditives || [])] };

    const existingItem = currentCart.find(i =>
      i.name === clonedItem.name &&
      JSON.stringify(i.chooseAdditives) === JSON.stringify(clonedItem.chooseAdditives)
    );

    if (existingItem) {
      existingItem.quantity = (existingItem.quantity || 1) + quantity;
    } else {
      clonedItem.quantity = quantity;
      currentCart.push(clonedItem);
    }

    this.cart.next(currentCart);
    this.saveCartToLocalStorage(currentCart);
  }

  removeFromCart(item: Menu) {
    const currentCart = this.cart.value;
    const existingItem = currentCart.find(i =>
      i.name === item.name &&
      JSON.stringify(i.chooseAdditives) === JSON.stringify(item.chooseAdditives)
    );

    if (existingItem) {
      if (existingItem.quantity && existingItem.quantity > 1) {
        existingItem.quantity -= 1;
      } else {
        currentCart.splice(currentCart.indexOf(existingItem), 1);
      }
    }
    this.cart.next(currentCart);
    this.saveCartToLocalStorage(currentCart);
  }

  removeProductFromCart(item: Menu) {
    const currentCart = this.cart.value;
    const existingItem = currentCart.find(i =>
      i.name === item.name &&
      JSON.stringify(i.chooseAdditives) === JSON.stringify(item.chooseAdditives)
    );

    if (existingItem) {
      currentCart.splice(currentCart.indexOf(existingItem), 1);
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

  calculateAdditivePrice(additives: Additives[]): number {
    let price = 0; // Reset the additivesPrice to 0
    additives.forEach(a => {
      price += a.price || 0;
    });
    return price;
  }


}
