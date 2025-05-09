import { Component, Input, OnInit } from '@angular/core';
import { LanguageTranslations } from "../../../interfaces/language.interface";
import { DeliveryHour } from "../../../interfaces/delivery.interface";
import { DecimalPipe, NgForOf, NgIf } from "@angular/common";
import { environment } from "../../../../environments/environment";
import { PaymentMethod } from "../../../interfaces/paymentMethod";
import { InfoMapComponent } from "../info-map/info-map.component";
import { SearchedRestaurant } from "../../../interfaces/searched-restaurant";
import {LanguageService} from "../../../services/state/language.service";
import {SearchedRestaurantsService} from "../../../services/state/searched-restaurant.service";
import {PaymentMethodsService} from "../../../services/api/payment-methods.service";

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
export class InfoComponent implements OnInit {
  @Input() restaurantId!: number;
  @Input() restaurantName!: string;
  searchedRestaurant: SearchedRestaurant = {} as SearchedRestaurant;

  constructor(
    private languageService: LanguageService,
    private searchedRestaurantsService: SearchedRestaurantsService,
  ) {}

  ngOnInit() {
    this.getRestaurant();
  }

  getRestaurant(): void {
    this.searchedRestaurant = this.searchedRestaurantsService.getSearchedRestaurant(this.restaurantId);
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


  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
