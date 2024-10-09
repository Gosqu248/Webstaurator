import {Component, Input, OnInit} from '@angular/core';
import {Menu} from "../../../interfaces/menu";
import {DecimalPipe} from "@angular/common";
import {CartService} from "../../../services/cart.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";

@Component({
  selector: 'app-restaurant-basket-item',
  standalone: true,
  imports: [
    DecimalPipe
  ],
  templateUrl: './restaurant-basket-item.component.html',
  styleUrl: './restaurant-basket-item.component.css'
})
export class RestaurantBasketItemComponent implements OnInit{
  @Input() order!: Menu;
  orderPrice: number = 0;
  constructor(private cartService: CartService,
              private languageService: LanguageService) {}

  ngOnInit() {
    this.setOrderPrice();
  }

  addOne() {
    this.cartService.addToCart(this.order);
    this.setOrderPrice();
  }

  removeOne() {
    this.cartService.removeFromCart(this.order)
    this.setOrderPrice();
  }

  setOrderPrice() {
    this.orderPrice = this.order.price * (this.order.quantity || 1);
  }

  getTranslate<k extends keyof LanguageTranslations>(key: k): string{
    return this.languageService.getTranslation(key)

  }


}
