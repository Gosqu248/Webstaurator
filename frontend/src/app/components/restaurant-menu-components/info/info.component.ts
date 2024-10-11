import {Component, Input, OnInit} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import { LanguageService } from '../../../services/language.service';
import {DeliveryService} from "../../../services/delivery.service";
import {DeliveryHour} from "../../../interfaces/delivery.interface";
import {DecimalPipe, NgForOf} from "@angular/common";
import {RestaurantAddressService} from "../../../services/restaurant-address.service";
import {RestaurantAddress} from "../../../interfaces/restaurant-address";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-info',
  standalone: true,
  imports: [
    NgForOf,
    DecimalPipe
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
  payments: { src: string, name: string }[] = [];

  constructor(private languageService: LanguageService,
              private restaurantAddressService: RestaurantAddressService,
              private deliveryService: DeliveryService) {}

  ngOnInit() {
    this.getDeliveryTime();
    this.getDeliveryInfo();
    this.getRestaurantAddress();
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
    this.payments = [
      { src: environment.api + '/img/payU.png', name: this.getTranslation('payU') },
      { src: environment.api + '/img/creditCard.png', name: this.getTranslation('creditCard') },
      { src: environment.api + '/img/cash.png', name: this.getTranslation('cash') },
    ];
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
