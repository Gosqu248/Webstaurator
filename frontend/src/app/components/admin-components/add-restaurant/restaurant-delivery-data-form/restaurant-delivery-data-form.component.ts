import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {LanguageService} from "../../../../services/state/language.service";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {Delivery} from "../../../../interfaces/delivery.interface";

@Component({
  selector: 'app-restaurant-delivery-data-form',
  standalone: true,
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './restaurant-delivery-data-form.component.html',
  styleUrl: './restaurant-delivery-data-form.component.css'
})
export class RestaurantDeliveryDataFormComponent {
  @Input() parentForm!: FormGroup;
  delivery: Delivery = {} as  Delivery;

  constructor(private languageService: LanguageService) {}


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
