import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {OrderBasketComponent} from "../order-basket/order-basket.component";
import {OrderPersonalInfoComponent} from "../order-personal-info/order-personal-info.component";
import {OrderDeliveryComponent} from "../order-delivery/order-delivery.component";
import {UserAddress} from "../../../interfaces/user.address.interface";
import {AddressesService} from "../../../services/api/addresses.service";
import {OrderPaymentComponent} from "../order-payment/order-payment.component";
import {AuthService} from "../../../services/api/auth.service";
import {OrderMenuRequest, OrderRequest, OrderStatus} from "../../../interfaces/order";
import {UserDTO} from "../../../interfaces/user.interface";
import {OptionService} from "../../../services/state/option.service";
import {MenuLoginComponent} from "../../menu-components/menu-login/menu-login.component";
import {MatDialog} from "@angular/material/dialog";
import {PayUService} from "../../../services/api/pay-u.service";
import {OrderService} from "../../../services/api/order.service";
import {Router} from "@angular/router";
import {RestaurantAddressService} from "../../../services/api/restaurant-address.service";
import {Coordinates} from "../../../interfaces/coordinates";
import {PaymentResponse} from "../../../interfaces/paymentMethod";
import {SearchedRestaurantsService} from "../../../services/state/searched-restaurant.service";
import {SearchedRestaurant} from "../../../interfaces/searched-restaurant";

@Component({
    selector: 'app-order-home',
    imports: [
        OrderBasketComponent,
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
  user: UserDTO = {} as UserDTO;
  canOrder: boolean = false;
  userId: number | null = null;
  restaurant: SearchedRestaurant = {} as SearchedRestaurant;
  deliveryOption: string = '';
  payUPaymentId: number | null = null;
  coordinates: Coordinates = {} as Coordinates;

  constructor(protected authService: AuthService,
              private addressService: AddressesService,
              private optionService: OptionService,
              private restaurantAddressService: RestaurantAddressService,
              private searchedRestaurantService: SearchedRestaurantsService,
              private dialog: MatDialog,
              private router: Router,
              private payUService: PayUService,
              private orderService: OrderService) {}

  ngOnInit() {
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
    this.orderPersonalInfo.personalInfoChanged.subscribe(() => {
      this.updateCanOrder();
    });
  }

  subscribeToAuthChanges() {
    this.authService.isAuthenticated$.subscribe(isAuthenticated => {
      if (isAuthenticated) {
        const token = localStorage.getItem('jwt');
        this.getUserAddresses(token);
        this.getUser(token);
      } else {
        this.user = {} as UserDTO;
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
      && this.orderPersonalInfo.personalForm.valid
      && !!this.orderDelivery.selectedDeliveryOption;
  }

  acceptOrder() {
    const restaurantId = sessionStorage.getItem('restaurantId');
    const deliveryTime = this.orderDelivery.selectedDeliveryOption === "fastest" ? 'Jak najszybciej' : this.orderDelivery.selectedHour || '';
    const user = this.user;
    const userAddress = this.orderDelivery.selectedAddress ? this.orderDelivery.selectedAddress : null
    const deliveryOption = this.deliveryOption === 'delivery' ? 'dostawa' : 'odbiór osobisty';

    const orderMenusRequest: OrderMenuRequest[] = this.basket.orderMenus.map(item => {
      if (item.menu.id == null) {
        throw new Error('Brak menuId w elemencie koszyka');
      }
      const menuId: number = item.menu.id;

      const chooseAdditivesId: number[] = (item.chooseAdditives ?? [])
        .map(a => a.id)
        .filter((id): id is number => id != null);

      return {
        quantity: item.quantity,
        menuId,
        chooseAdditivesId
      };
    });

    if (restaurantId) {
      const order: OrderRequest = {
        userId: user.id,
        userAddressId: userAddress?.id || null,
        restaurantId: this.restaurant.restaurantId,
        orderMenus: orderMenusRequest,
        deliveryOption: deliveryOption,
        deliveryTime: deliveryTime,
        paymentId: null,
        paymentMethod: this.orderPayment.selectedPayment?.method || '',
        status: OrderStatus.niezaplacone,
        totalPrice: this.basket.totalPrice,
        comment: this.orderPersonalInfo.relevantInformation,
      }

      if (this.orderPayment.selectedPayment?.method === 'Gotówka') {
        this.orderService.createOrder(order);
        setTimeout(() => {
          this.router.navigate(['/orders-history'])

        }, 1000);

      } else {
        this.payUService.createPayUPayment(order).subscribe({
          next: (response: PaymentResponse) => {
            localStorage.setItem('payUPaymentId', response.orderId.toString());
            localStorage.setItem('paymentOrder', JSON.stringify(order));
            window.open(response.redirectUri, '_self');
          },
          error: (error) => {
            console.error('Błąd podczas tworzenia płatności:', error);
          }
        });
      }
    } else {
      console.error('No restaurant id');
    }
  }

  getRestaurant() {
    this.searchedRestaurantService.selectedRestaurant$.subscribe(restaurant => {
      this.restaurant = restaurant;
    });
    this.subscribeToAuthChanges();
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

  getUserAddresses(token: string | null) {
    if (token) {
      this.restaurantAddressService.getCoordinates(this.restaurant.restaurantId).subscribe(coordinates => {
        this.addressService.getAvailableAddresses(token, coordinates).subscribe(addresses => {
          this.addresses = addresses;
        });
      });
    } else {
      console.error('Token is not set');
    }
  }

  getUser(token: string | null) {
    if (token) {
      this.authService.getUser(token).subscribe(user => {
        this.user = user;
      });
    }
  }
}
