 import {Component, Input, OnInit} from '@angular/core';
 import {LanguageTranslations} from "../../../interfaces/language.interface";
 import {LanguageService} from "../../../services/language.service";
 import {NgClass, NgIf} from "@angular/common";
 import {OptionService} from "../../../services/option.service";

@Component({
  selector: 'app-restaurant-basket',
  standalone: true,
  imports: [
    NgIf,
    NgClass
  ],
  templateUrl: './restaurant-basket.component.html',
  styleUrl: './restaurant-basket.component.css'
})
export class RestaurantBasketComponent implements OnInit{
  @Input() restaurant!: any;
  order: any = null;
  selectedOption: string = 'delivery';
  deliveryOrder: string = "";
  pickupOrder: string = "";
  isNotPickUp: boolean = false;

  constructor(private languageService: LanguageService, private optionService: OptionService) {
  }

  ngOnInit() {
    this.deliveryOrder = this.restaurant.delivery.deliveryMinTime + "-" + this.restaurant.delivery.deliveryMaxTime + " min";
    this.getPickUp();
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

  selectOption(option: string) {
    this.selectedOption = option;
    this.optionService.setSelectBasketDelivery(option);
  }

  getPickUp() {
   const pickup = this.restaurant.delivery.pickupTime ;
   pickup > 0 ? this.pickupOrder = pickup + " min" : this.pickupOrder = this.getTranslation('notAvailable'); this.isNotPickUp = true;
  }

}
