import {Component, OnInit} from '@angular/core';
import {CartService} from "../../../services/cart.service";
import {OrderService} from "../../../services/order.service";
import {Menu} from "../../../interfaces/menu";
import {OrderBasketComponent} from "../order-basket/order-basket.component";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {NgIf} from "@angular/common";
import {Router} from "@angular/router";
import {OrderPersonalInfoComponent} from "../order-personal-info/order-personal-info.component";
import {OrderDeliveryComponent} from "../order-delivery/order-delivery.component";
import {User} from "../../../interfaces/user.interface";
import {UserAddress} from "../../../interfaces/user.address.interface";
import {AddressesService} from "../../../services/addresses.service";

@Component({
  selector: 'app-order-home',
  standalone: true,
  imports: [
    OrderBasketComponent,
    NgIf,
    OrderPersonalInfoComponent,
    OrderDeliveryComponent
  ],
  templateUrl: './order-home.component.html',
  styleUrl: './order-home.component.css'
})
export class OrderHomeComponent implements OnInit{
  isAuthChecked: boolean = false;
  addresses: UserAddress[] = [];
  constructor(private languageService: LanguageService,
              private addressService: AddressesService,
              private router: Router) {}


  ngOnInit() {
    this.checkAuth();
    this.checkFragment();
    this.addressService.addresses$.subscribe(addresses => {
      this.addresses = addresses;
    });
  }

  checkAuth(): boolean {
    if (!this.isAuthChecked) {
      this.isAuthChecked = true;
      const token = localStorage.getItem('jwt');
      token ? this.addressService.loadAddresses(token) : null
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
