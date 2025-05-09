import {Component, Input} from '@angular/core';
import {Restaurant} from "../../../../interfaces/restaurant";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {LanguageService} from "../../../../services/state/language.service";
import {ActivatedRoute, Router} from "@angular/router";
import {RestaurantService} from "../../../../services/api/restaurant.service";

@Component({
  selector: 'app-all-restaurants-item',
  standalone: true,
  imports: [],
  templateUrl: './all-restaurants-item.component.html',
  styleUrl: './all-restaurants-item.component.css'
})
export class AllRestaurantsItemComponent {
  @Input() restaurant!: Restaurant;

  constructor(private languageService: LanguageService,
              private restaurantService: RestaurantService,
              private route: ActivatedRoute,
              private router: Router) {}
  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

  editRestaurant() {
    this.router.navigate(['/edit-restaurant', this.restaurant.id], { relativeTo: this.route });
  }

  deleteRestaurant() {
    this.restaurantService.removeRestaurant(this.restaurant.id)

  }
}
