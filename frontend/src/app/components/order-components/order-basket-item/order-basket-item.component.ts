import {Component, Input, OnInit} from '@angular/core';
import {Menu} from "../../../interfaces/menu";
import {DecimalPipe} from "@angular/common";
import {CartService} from "../../../services/cart.service";
import {OrderMenu} from "../../../interfaces/order";

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
  @Input() orderMenus!: OrderMenu;
  orderPrice: number = 0;

  constructor(private cartService: CartService) {}

  ngOnInit() {
    this.setOrderPrice();
  }

  setOrderPrice() {
    const additivePrice = this.cartService.calculateAdditivePrice(this.orderMenus.chooseAdditives || []);

    this.orderPrice = (this.orderMenus.menu.price + additivePrice) * (this.orderMenus.quantity || 1);
  }

  formatAdditives(): string {
    return this.cartService.formatAdditives(this.orderMenus.chooseAdditives || [])
  }

}
