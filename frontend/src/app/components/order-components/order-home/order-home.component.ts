import {Component, OnInit} from '@angular/core';
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
import {UserInfoOrder} from "../../../interfaces/user-info-order";
import {AuthService} from "../../../services/auth.service";

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
export class OrderHomeComponent implements OnInit{
  isAuthChecked: boolean = false;
  addresses: UserAddress[] = [];
  userInfo: UserInfoOrder | null = null;
  token = localStorage.getItem('jwt');

  constructor(private languageService: LanguageService,
              private authService: AuthService,
              private addressService: AddressesService,
              private router: Router) {}


  ngOnInit() {
    this.checkAuth();
    this.checkFragment();
    this.addressService.addresses$.subscribe(addresses => {
      this.addresses = addresses;
    });
    this.authService.userInfo$.subscribe(userInfo => {
      this.userInfo = userInfo;
    });
  }

  checkAuth(): boolean {
    if (!this.isAuthChecked) {
      this.isAuthChecked = true;

      if (this.token) {
        this.addressService.loadAddresses(this.token);
      }
    }

    return !!localStorage.getItem('jwt');
  }



  login() {
    this.router.navigate([], { fragment: 'login' });
  }

  checkFragment(): void {
    const fragment = this.router.url.split('#')[1];
    if (fragment === 'login') {
      const token = localStorage.getItem('jwt');
      token ? this.addressService.loadAddresses(token) : null
    }
  }

    getTranslation<k extends keyof LanguageTranslations>(key: k): string {
      return this.languageService.getTranslation(key);
    }
}
