import {Component, Input} from '@angular/core';
import {FormArray, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {LanguageService} from "../../../../services/state/language.service";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {PaymentMethod} from "../../../../interfaces/paymentMethod";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-restaurant-payment-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './restaurant-payment-form.component.html',
  styleUrl: './restaurant-payment-form.component.css'
})
export class RestaurantPaymentFormComponent {
  @Input() parentForm!: FormGroup;
  @Input() paymentMethods!: PaymentMethod[];


  constructor(private languageService: LanguageService) {}

  get paymentMethodsArray(): FormArray | null {
    const array = this.parentForm?.get('paymentMethods');
    return array instanceof FormArray ? array : null;
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
