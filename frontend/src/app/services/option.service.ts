import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {Menu} from "../interfaces/menu";

@Injectable({
  providedIn: 'root'
})
export class OptionService {

  selectedOption = new BehaviorSubject<string>('delivery');
  selectedOption$ = this.selectedOption.asObservable();

  selectedCategories = new BehaviorSubject<string[]>([]);
  selectedCategories$ = this.selectedCategories.asObservable();

  selectBasketDelivery = new BehaviorSubject<string>('delivery');
  selectBasketDelivery$ = this.selectBasketDelivery.asObservable();

  selectedMenuCategory = new BehaviorSubject<string>('');
  selectedMenuCategory$ = this.selectedMenuCategory.asObservable();

  setSelectedOption(option: string) {
    this.selectedOption.next(option);
  }

  setSelectedCategories(categories: string[]) {
    this.selectedCategories.next(categories);
  }

  setSelectBasketDelivery(delivery: string) {
    this.selectBasketDelivery.next(delivery);
  }

  setSelectedMenuCategories(category: string) {
    this.selectedMenuCategory.next(category);
  }



}
