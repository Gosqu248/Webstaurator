import {Component, Input, OnInit} from '@angular/core';
import {Menu} from "../../../interfaces/menu";
import {DecimalPipe} from "@angular/common";
import {CartService} from "../../../services/cart.service";

@Component({
  selector: 'app-order-basket-item',
  standalone: true,
  imports: [
    DecimalPipe
  ],
  templateUrl: './order-basket-item.component.html',
  styleUrl: './order-basket-item.component.css'
})
export class OrderBasketItemComponent implements OnInit{
  @Input() order!: Menu;
  orderPrice: number = 0;

  constructor(private cartService: CartService) {}

  ngOnInit() {
    this.setOrderPrice();
  }

  setOrderPrice() {
    const additivePrice = this.cartService.calculateAdditivePrice(this.order.chooseAdditives || []);

    this.orderPrice = (this.order.price + additivePrice) * (this.order.quantity || 1);
  }

  formatAdditives(): string {
    return this.cartService.formatAdditives(this.order.chooseAdditives || [])
  }

}
