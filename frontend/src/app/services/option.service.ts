import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class OptionService {
  selectedOption = new BehaviorSubject<string>('delivery');
  selectedOption$ = this.selectedOption.asObservable();

  setSelectedOption(option: string) {
    this.selectedOption.next(option);
  }
}
