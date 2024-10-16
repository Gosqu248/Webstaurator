 import {Component, Input, OnInit} from '@angular/core';
 import {LanguageTranslations} from "../../../interfaces/language.interface";
 import {LanguageService} from "../../../services/language.service";
 import {DecimalPipe, NgClass, NgForOf, NgIf} from "@angular/common";
 import {OptionService} from "../../../services/option.service";
 import {Menu} from "../../../interfaces/menu";
 import {RestaurantBasketItemComponent} from "../restaurant-basket-item/restaurant-basket-item.component";
 import {CartService} from "../../../services/cart.service";

@Component({
  selector: 'app-restaurant-basket',
  standalone: true,
  imports: [
    NgIf,
    NgClass,
    RestaurantBasketItemComponent,
    NgForOf,
    DecimalPipe
  ],
  templateUrl: './restaurant-basket.component.html',
  styleUrl: './restaurant-basket.component.css'
})
export class RestaurantBasketComponent implements OnInit{
  @Input() restaurant!: any;
  orders: Menu[] = [];
  selectedOption: string = 'delivery';
  deliveryOrder: string = "";
  pickupOrder: string = "";
  isNotPickUp: boolean = false;
  ordersPrice: number = 0;
  deliveryPrice: string | null = null;
  totalPrice: number = 0;
  minimumPrice: number = 0;
  isPriceValid: boolean = true;
  missingPrice: number = 0;


  constructor(private languageService: LanguageService, private optionService: OptionService, private cartService: CartService) {}

  ngOnInit() {
    this.deliveryOrder = this.restaurant.delivery.deliveryMinTime + "-" + this.restaurant.delivery.deliveryMaxTime + " min";
    this.getPickUp();
    this.cartService.setCurrentRestaurantId(this.restaurant.id)
    this.getCart();
  }

  getCart() {
    this.cartService.cart$.subscribe(order => {
      this.orders = order;
      this.calculateOrderPrice();
      this.getMinimumPrice();
    })
  }

  calculateOrderPrice() {
    this.ordersPrice = this.orders.reduce((total, order) => {
      const additivePrice = this.cartService.calculateAdditivePrice(order.chooseAdditives || []);
      return  total + ((order.price + additivePrice)  * (order.quantity || 1))
      }, 0);
    sessionStorage ? this.deliveryPrice = sessionStorage.getItem("deliveryPrice") : null;
    this.totalPrice = this.ordersPrice + (this.deliveryPrice ? parseFloat(this.deliveryPrice) : 0);
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

  getMinimumPrice() {
    const minPrice = sessionStorage.getItem("minPrice");
    this.minimumPrice = minPrice ? +minPrice : 0;

    this.minimumPrice > this.ordersPrice ? this.isPriceValid = false : this.isPriceValid = true;
    this.missingPrice = this.roundToTwoDecimals(this.minimumPrice - this.ordersPrice);
  }

  roundToTwoDecimals(value: number): number {
    return Math.round(value * 100) / 100;
  }

  selectOption(option: string) {
    this.selectedOption = option;
    this.optionService.setSelectBasketDelivery(option);
  }

  getPickUp() {
   const pickup = this.restaurant.delivery.pickupTime ;
   pickup > 0 ? this.pickupOrder = pickup + " min" : this.pickupOrder = this.getTranslation('notAvailable'); this.isNotPickUp = true;
  }

}
