import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DecimalPipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {
    RestaurantBasketItemComponent
} from "../../restaurant-menu-components/restaurant-basket-item/restaurant-basket-item.component";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {Menu} from "../../../interfaces/menu";
import {OrderService} from "../../../services/order.service";
import {OrderBasketItemComponent} from "../order-basket-item/order-basket-item.component";
import {OrderMenu} from "../../../interfaces/order";

@Component({
  selector: 'app-order-basket',
  standalone: true,
  imports: [
    DecimalPipe,
    NgForOf,
    NgIf,
    RestaurantBasketItemComponent,
    OrderBasketItemComponent,
    NgClass
  ],
  templateUrl: './order-basket.component.html',
  styleUrl: './order-basket.component.css'
})
export class OrderBasketComponent implements OnInit{
  @Output() acceptOrder = new EventEmitter<void>();
  @Input() canOrder!: boolean;
  orderMenus: OrderMenu[] = [];
  ordersPrice: number = 0;
  deliveryPrice: string | null = null;
  totalPrice: number = 0;
  restaurantName: string | null = null;

  constructor(private languageService: LanguageService,
              private orderService: OrderService) {
  }

  ngOnInit() {
    this.orderMenus = this.orderService.getOrders();
    console.log(this.orderMenus);
    this.calculatePrices();
    this.restaurantName = sessionStorage.getItem("restaurantName");
  }

  orderAccepted() {

    this.acceptOrder.emit();
  }

  calculatePrices() {
    const { ordersPrice, deliveryPrice, totalPrice } = this.orderService.calculateOrderPrice(this.orderMenus);
    this.ordersPrice = ordersPrice;
    this.deliveryPrice = deliveryPrice;
    this.totalPrice = totalPrice;
  }
  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
