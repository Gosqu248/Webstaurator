 import {Component, Input, OnInit} from '@angular/core';
 import {LanguageTranslations} from "../../../interfaces/language.interface";
 import {LanguageService} from "../../../services/state/language.service";
 import {DecimalPipe, NgClass, NgForOf, NgIf} from "@angular/common";
 import {OptionService} from "../../../services/state/option.service";
 import {RestaurantBasketItemComponent} from "../restaurant-basket-item/restaurant-basket-item.component";
 import {CartService} from "../../../services/state/cart.service";
 import {Router} from "@angular/router";
 import {OrderService} from "../../../services/api/order.service";
 import {OrderMenu} from "../../../interfaces/order";
 import {SearchedRestaurant} from "../../../interfaces/searched-restaurant";

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
  @Input() restaurant!: SearchedRestaurant;
  orderMenus: OrderMenu[] = [];
  selectedOption: string = "";
  deliveryOrder: string = "";
  pickupOrder: string = "";
  isNotPickUp: boolean = false;
  ordersPrice: number = 0;
  deliveryPrice: string | null = null;
  totalPrice: number = 0;
  minimumPrice: number = 0;
  isPriceValid: boolean = true;
  missingPrice: number = 0;
  serviceFee: number = 2;
  isOpen: boolean = true;

  constructor(private languageService: LanguageService,
              private optionService: OptionService,
              private router: Router,
              private orderService: OrderService,
              private cartService: CartService) {}

  ngOnInit() {
    this.getIsOpen()
    this.getDelivery();
    this.getPickUp();
    this.cartService.setCurrentRestaurantId(this.restaurant.restaurantId);
    this.selectedOption = this.optionService.selectedOption.value;
    this.getCart();
  }

  getIsOpen() {
    const is = sessionStorage.getItem("isOpen");
    if (is) {
      this.isOpen = is === "true";
    }
  }

  getCart() {
    this.cartService.orderMenus$.subscribe(cart => {
      this.orderMenus = cart;
      this.calculateOrderPrice(this.selectedOption);
      this.getMinimumPrice();
    })
  }

  goToOrder() {
    this.orderService.updateOrderMenus(this.orderMenus);
    this.router.navigate(['/checkout']);
  }

  calculateOrderPrice(option: string) {
    const { ordersPrice, deliveryPrice, totalPrice } = this.orderService.calculateOrderPrice(this.orderMenus, option);
    this.ordersPrice = ordersPrice;
    this.deliveryPrice = deliveryPrice;
    this.totalPrice = totalPrice  + this.serviceFee;
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

  getMinimumPrice() {
    const minPrice = this.restaurant.delivery?.minimumPrice;
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
    this.calculateOrderPrice(option);
  }

  getDelivery() {
    this.deliveryOrder = this.restaurant.delivery?.deliveryMinTime + " - " + this.restaurant.delivery?.deliveryMaxTime + " min";
    this.selectedOption = 'delivery';
  }

  getPickUp() {
   const pickup = this.restaurant.delivery?.pickupTime ;
   if (pickup && pickup > 0) {
     this.pickupOrder = pickup + " min"
   } else {
      this.pickupOrder = this.getTranslation('notAvailable');
      this.isNotPickUp = true;
      this.selectedOption = 'delivery';
   }
  }

}
