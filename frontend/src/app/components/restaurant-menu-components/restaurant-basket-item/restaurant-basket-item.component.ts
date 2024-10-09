import {Component, Input, OnInit} from '@angular/core';
import {Menu} from "../../../interfaces/menu";
import {DecimalPipe} from "@angular/common";
import {CartService} from "../../../services/cart.service";

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
  constructor(private cartService: CartService) {}

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


}
