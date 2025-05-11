import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {LanguageService} from "../../../../services/state/language.service";
import {LanguageTranslations} from "../../../../interfaces/language.interface";

@Component({
    selector: 'app-restaurant-address-form',
    imports: [
        ReactiveFormsModule
    ],
    templateUrl: './restaurant-address-form.component.html',
    styleUrl: './restaurant-address-form.component.css'
})
export class RestaurantAddressFormComponent {
  @Input() parentForm!: FormGroup;

  constructor(private languageService: LanguageService) {}


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
