import {Component, Input} from '@angular/core';
import {Restaurant} from "../../../../interfaces/restaurant";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {LanguageService} from "../../../../services/language.service";

@Component({
  selector: 'app-all-restaurants-item',
  standalone: true,
  imports: [],
  templateUrl: './all-restaurants-item.component.html',
  styleUrl: './all-restaurants-item.component.css'
})
export class AllRestaurantsItemComponent {
  @Input() restaurant!: Restaurant;

  constructor(private languageService: LanguageService) {}
  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

  editRestaurant() {

  }

  deleteRestaurant() {

  }
}
