import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {FavouriteService} from "./favourite.service";

@Injectable({
  providedIn: 'root'
})
export class OptionService {

  constructor(private favouriteService: FavouriteService) {
  }

  selectedOption = new BehaviorSubject<string>('delivery');
  selectedOption$ = this.selectedOption.asObservable();

  selectedCategories = new BehaviorSubject<string[]>([]);
  selectedCategories$ = this.selectedCategories.asObservable();

  selectBasketDelivery = new BehaviorSubject<string>(this.loadDeliveryOptionFromStorage());
  selectBasketDelivery$ = this.selectBasketDelivery.asObservable();

  selectedMenuCategory = new BehaviorSubject<string>('');
  selectedMenuCategory$ = this.selectedMenuCategory.asObservable();


  setSelectedOption(option: string) {
    this.selectedOption.next(option);
    this.selectBasketDelivery.next(option);
  }

  setSelectedCategories(categories: string[]) {
    this.selectedCategories.next(categories);
  }

  setSelectBasketDelivery(delivery: string) {
    this.selectBasketDelivery.next(delivery);
    localStorage.setItem('deliveryOption', delivery)

  }


  loadDeliveryOptionFromStorage(): string {
    if (typeof localStorage !== 'undefined') {
      const deliveryOption = localStorage.getItem('deliveryOption');
      return deliveryOption ? deliveryOption : this.selectedOption.value;
    }
    return this.selectedOption.value;

  }

  setSelectedMenuCategories(category: string) {
    this.selectedMenuCategory.next(category);
  }


}
