import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {OrderBasketComponent} from "../order-basket/order-basket.component";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {NgIf} from "@angular/common";
import {Router} from "@angular/router";
import {OrderPersonalInfoComponent} from "../order-personal-info/order-personal-info.component";
import {OrderDeliveryComponent} from "../order-delivery/order-delivery.component";
import {UserAddress} from "../../../interfaces/user.address.interface";
import {AddressesService} from "../../../services/addresses.service";
import {OrderPaymentComponent} from "../order-payment/order-payment.component";
import {AuthService} from "../../../services/auth.service";
import {Order, OrderStatus} from "../../../interfaces/order";
import {OrderService} from "../../../services/order.service";
import {RestaurantsService} from "../../../services/restaurants.service";
import {Restaurant} from "../../../interfaces/restaurant";
import {User} from "../../../interfaces/user.interface";

@Component({
  selector: 'app-order-home',
  standalone: true,
  imports: [
    OrderBasketComponent,
    NgIf,
    OrderPersonalInfoComponent,
    OrderDeliveryComponent,
    OrderPaymentComponent
  ],
  templateUrl: './order-home.component.html',
  styleUrl: './order-home.component.css'
})
export class OrderHomeComponent implements OnInit, AfterViewInit{
  @ViewChild(OrderDeliveryComponent) orderDelivery!: OrderDeliveryComponent;
  @ViewChild(OrderPersonalInfoComponent) orderPersonalInfo!: OrderPersonalInfoComponent;
  @ViewChild(OrderPaymentComponent) orderPayment!: OrderPaymentComponent;
  @ViewChild(OrderBasketComponent) basket!: OrderBasketComponent;

  isAuthChecked: boolean = false;
  addresses: UserAddress[] = [];
  user: User = {} as User;
  token = localStorage.getItem('jwt');
  canOrder: boolean = false;
  userId: number | null = null;
  restaurant: Restaurant = {} as Restaurant;
  deliveryOption: string = '';

  constructor(private languageService: LanguageService,
              protected authService: AuthService,
              private addressService: AddressesService,
              private orderService: OrderService,
              private restaurantService: RestaurantsService,
              private router: Router) {}


  ngOnInit() {
    this.subscribeToAuthChanges();
    this.getRestaurant();

  }

  ngAfterViewInit() {
    this.updateCanOrder();

    this.orderDelivery.deliveryChanged.subscribe(() => {
      this.updateCanOrder();
    });

  }

  subscribeToAuthChanges() {
    this.authService.isAuthenticated$.subscribe(isAuthenticated => {
      if (isAuthenticated) {
        this.getUserAddresses();
        this.authService.userInfo$.subscribe(user => {
          this.user = user;
        });
      } else {
        this.clearAddresses();
      }
    });
  }

  clearAddresses() {
    this.addresses = [];
  }



  updateCanOrder() {
    if (this.orderDelivery && this.orderPersonalInfo) {
      this.canOrder = this.checkCanOrder();
    }
  }

  checkCanOrder(): boolean {
    return !!this.orderDelivery.selectedAddress
      && !!this.orderDelivery.selectedDeliveryOption
      && this.orderPersonalInfo.personalForm.valid;
  }

  acceptOrder() {
    const restaurantId = sessionStorage.getItem('restaurantId');
    const deliveryTime = this.orderDelivery.selectedDeliveryOption === "fastest" ? 'Jak najszybciej' : this.orderDelivery.selectedHour || '';
    const user = this.user ;
    const delOpt = sessionStorage.getItem("deliveryOption");
    if (delOpt) {
      delOpt === "delivery" ? this.deliveryOption = "dostawa" : this.deliveryOption = "odbiór";
    }


    if (restaurantId) {
      const order: Order = {
        paymentMethod: this.orderPayment.selectedPayment?.method || '',
        status: OrderStatus.niezaplacone,
        totalPrice: this.basket.totalPrice,
        deliveryTime: deliveryTime,
        deliveryOption: this.deliveryOption,
        comment: this.orderPersonalInfo.relevantInformation,
        restaurant: this.restaurant,
        orderMenus: this.basket.orderMenus,
        userAddress: this.orderDelivery.selectedAddress,
        user: user
      }
      this.orderService.createOrder(order);
      this.router.navigate(['restaurants']);
    } else {
      console.error('No restaurant id');
    }

  }

  login() {
    this.router.navigate([], { fragment: 'login' });
  }

  getRestaurant() {
    const restaurantId = sessionStorage.getItem('restaurantId');

    if (restaurantId) {
      this.restaurantService.getRestaurantById(parseInt(restaurantId)).subscribe((data: Restaurant) => {
        this.restaurant = data;
      });
    }
  }


  getUserAddresses() {
    if(this.token)
    this.addressService.getUserAddresses(this.token).subscribe(addresses => {
      this.addresses = addresses;
    });
  }

    getTranslation<k extends keyof LanguageTranslations>(key: k): string {
      return this.languageService.getTranslation(key);
    }
}
