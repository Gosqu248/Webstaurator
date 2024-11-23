import {Component, Input, OnInit} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import { LanguageService } from '../../../services/language.service';
import {DeliveryService} from "../../../services/delivery.service";
import {DeliveryHour} from "../../../interfaces/delivery.interface";
import {DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {RestaurantAddressService} from "../../../services/restaurant-address.service";
import {RestaurantAddress} from "../../../interfaces/restaurant-address";
import {environment} from "../../../../environments/environment";
import {PaymentMethod} from "../../../interfaces/paymentMethod";
import {PaymentMethodsService} from "../../../services/payment-methods.service";
import {InfoMapComponent} from "../info-map/info-map.component";

@Component({
  selector: 'app-info',
  standalone: true,
  imports: [
    NgForOf,
    DecimalPipe,
    InfoMapComponent,
    NgIf
  ],
  templateUrl: './info.component.html',
  styleUrl: './info.component.css'
})
export class InfoComponent implements OnInit{
  @Input() restaurantId!: number;
  @Input() restaurantName!: string;
  deliveryTime: DeliveryHour[] = [];
  deliveryPrice: number = 0;
  minimumPrice: number = 0;
  restaurantAddress: RestaurantAddress = {} as RestaurantAddress;
  payments: PaymentMethod[] = [];

  constructor(private languageService: LanguageService,
              private restaurantAddressService: RestaurantAddressService,
              private paymentService: PaymentMethodsService,
              private deliveryService: DeliveryService) {}

  ngOnInit() {
    this.getRestaurantAddress();
    this.getDeliveryTime();
    this.getDeliveryInfo();
    this.setPayments();
  }

  getDeliveryTime(): void {
    this.deliveryService.getDeliveryTIme(this.restaurantId).subscribe((data) => {
      this.deliveryTime = data;
    });
  }

  getRestaurantAddress(): void {
    this.restaurantAddressService.getRestaurantAddress(this.restaurantId).subscribe((data) => {
      this.restaurantAddress = data;
    });
  }

  getDayName(dayOfWeek: number): string {
    const days = [
      this.getTranslation('sunday'),
      this.getTranslation('monday'),
      this.getTranslation('tuesday'),
      this.getTranslation('wednesday'),
      this.getTranslation('thursday'),
      this.getTranslation('friday'),
      this.getTranslation('saturday')
    ];
    return days[dayOfWeek];
  }

  getDeliveryInfo() {
    const price = sessionStorage.getItem("deliveryPrice");
    this.deliveryPrice = price ? +price : 0;
    const minPrice = sessionStorage.getItem("minPrice");
    this.minimumPrice = minPrice ? +minPrice : 0;
  }

  setPayments() {
    this.paymentService.getRestaurantPaymentMethods(this.restaurantId).subscribe((data) => {
      this.payments = data.map((payment) => {
        payment.image = environment.api + payment.image;
        return payment;
      });
    });
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
