import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DecimalPipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {
    RestaurantBasketItemComponent
} from "../../restaurant-menu-components/restaurant-basket-item/restaurant-basket-item.component";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {OrderService} from "../../../services/order.service";
import {OrderBasketItemComponent} from "../order-basket-item/order-basket-item.component";
import {OrderMenu} from "../../../interfaces/order";
import {OptionService} from "../../../services/option.service";

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
  deliveryOption: string = '';
  serviceFee: number = 2;

  constructor(private languageService: LanguageService,
              private optionService: OptionService,
              private orderService: OrderService) {
  }

  ngOnInit() {
    this.getDeliveryOption();
    this.getOrderMenus();
    if (typeof sessionStorage !== 'undefined') {
    this.restaurantName = sessionStorage.getItem("restaurantName");
    }

  }

  orderAccepted() {
    this.acceptOrder.emit();
  }

  getOrderMenus() {
    this.orderService.orderMenus$.subscribe((orderMenus) => {
      this.orderMenus = orderMenus;
      this.calculatePrices();
    });
  }

  getDeliveryOption() {
    this.optionService.selectBasketDelivery$.subscribe(delivery => {
      this.deliveryOption = delivery;
    });

  }
  calculatePrices() {
    const { ordersPrice, deliveryPrice, totalPrice } = this.orderService.calculateOrderPrice(this.orderMenus, this.deliveryOption);
    this.ordersPrice = ordersPrice;
    this.deliveryPrice = deliveryPrice;
    this.totalPrice = totalPrice + this.serviceFee;
  }
  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
