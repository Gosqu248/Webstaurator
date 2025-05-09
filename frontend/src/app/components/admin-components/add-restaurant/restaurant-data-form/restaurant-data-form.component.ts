import {Component, Input} from '@angular/core';
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {LanguageService} from "../../../../services/state/language.service";
import {FormGroup, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-restaurant-data-form',
  standalone: true,
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './restaurant-data-form.component.html',
  styleUrl: './restaurant-data-form.component.css'
})
export class RestaurantDataFormComponent {
  @Input() parentForm!: FormGroup;

  constructor(private languageService: LanguageService) {}


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
