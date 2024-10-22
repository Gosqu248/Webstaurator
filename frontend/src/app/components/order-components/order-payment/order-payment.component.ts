import { Component } from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {OrderDeliveryItemComponent} from "../order-delivery-item/order-delivery-item.component";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {AddressesService} from "../../../services/addresses.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-order-payment',
  standalone: true,
    imports: [
        NgForOf,
        NgIf,
        OrderDeliveryItemComponent
    ],
  templateUrl: './order-payment.component.html',
  styleUrl: './order-payment.component.css'
})
export class OrderPaymentComponent {

  constructor(private languageService: LanguageService) {}


  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
