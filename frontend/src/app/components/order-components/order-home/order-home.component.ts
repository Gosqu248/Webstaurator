import {AfterViewInit, Component, OnInit, ViewChild, Inject, PLATFORM_ID} from '@angular/core';
import {OrderBasketComponent} from "../order-basket/order-basket.component";
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
import {OptionService} from "../../../services/option.service";
import {MenuLoginComponent} from "../../menu-components/menu-login/menu-login.component";
import {MatDialog} from "@angular/material/dialog";
import {isPlatformBrowser} from "@angular/common";

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
export class OrderHomeComponent implements OnInit, AfterViewInit {
  @ViewChild(OrderDeliveryComponent) orderDelivery!: OrderDeliveryComponent;
  @ViewChild(OrderPersonalInfoComponent) orderPersonalInfo!: OrderPersonalInfoComponent;
  @ViewChild(OrderPaymentComponent) orderPayment!: OrderPaymentComponent;
  @ViewChild(OrderBasketComponent) basket!: OrderBasketComponent;

  addresses: UserAddress[] = [];
  user: User = {} as User;
  token: string | null = null;
  canOrder: boolean = false;
  userId: number | null = null;
  restaurant: Restaurant = {} as Restaurant;
  deliveryOption: string = '';

  constructor(protected authService: AuthService,
              private addressService: AddressesService,
              private orderService: OrderService,
              private optionService: OptionService,
              private restaurantService: RestaurantsService,
              private dialog: MatDialog,
              private router: Router,
              @Inject(PLATFORM_ID) private platformId: Object) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.token = localStorage.getItem('jwt');
    }

    this.subscribeToAuthChanges();

    this.getRestaurant();

    this.optionService.selectBasketDelivery$.subscribe(delivery => {
      this.deliveryOption = delivery;
    });
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
        this.token = localStorage.getItem('jwt');
        this.getUserAddresses();
        this.getUser();
      } else {
        this.user = {} as User;
        this.addresses = [];
      }
    });
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
    const user = this.user;

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

  getRestaurant() {
    if (isPlatformBrowser(this.platformId)) {
      const restaurantId = sessionStorage.getItem('restaurantId');

      if (restaurantId) {
        this.restaurantService.getRestaurantById(parseInt(restaurantId)).subscribe((data: Restaurant) => {
          this.restaurant = data;
        });
      }
    }
  }

  openLoginDialog(): void {
    const dialogRef = this.dialog.open(MenuLoginComponent, {
      width: '400px',
      height: '400px',
    });

    dialogRef.afterClosed().subscribe(() => {
      this.subscribeToAuthChanges();
    });
  }

  getUserAddresses() {
    if (this.token) {
      this.addressService.getUserAddresses(this.token).subscribe(addresses => {
        this.addresses = addresses;
      });
    }
  }

  getUser() {
    if (this.token) {
      this.authService.getUser(this.token).subscribe(user => {
        this.user = user;
        console.log('User data fetched: ', user);
      });
    }
  }
}
