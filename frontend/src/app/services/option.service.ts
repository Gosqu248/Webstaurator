import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class OptionService {
  selectedOption = new BehaviorSubject<string>('delivery');
  selectedOption$ = this.selectedOption.asObservable();

  selectedCategories = new BehaviorSubject<string[]>([]);
  selectedCategories$ = this.selectedCategories.asObservable();

  setSelectedOption(option: string) {
    this.selectedOption.next(option);
  }

  setSelectedCategories(categories: string[]) {
    this.selectedCategories.next(categories);
  }
}
