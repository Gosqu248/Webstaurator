import {Component, Input, OnInit} from '@angular/core';
import {DecimalPipe, NgIf} from "@angular/common";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/state/language.service";
import {Router} from "@angular/router";
import {RestaurantOpinion} from "../../../interfaces/restaurant-opinion";
import {RestaurantOpinionService} from "../../../services/api/restaurant-opinion.service";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {SearchedRestaurantsService} from "../../../services/state/searched-restaurant.service";
import {SearchedRestaurant} from "../../../interfaces/searched-restaurant";
import {DeliveryService} from "../../../services/api/delivery.service";


@Component({
    selector: 'app-restaurant-item',
    imports: [
        NgIf,
        DecimalPipe,
        MatProgressSpinner
    ],
    templateUrl: './restaurant-item.component.html',
    styleUrl: './restaurant-item.component.css'
})
export class RestaurantItemComponent implements OnInit{
  @Input() restaurantId!: number;
  restaurant: SearchedRestaurant = {} as SearchedRestaurant;
  isOpen: boolean = true;
  restaurantOpinions: RestaurantOpinion[] = [];
  rating: number = 0;

  constructor(private languageService:LanguageService,
              private searchedRestaurantService: SearchedRestaurantsService,
              private restaurantOpinionService: RestaurantOpinionService,
              private deliveryService: DeliveryService,
              private router:Router) {}

  ngOnInit() {
    this.getSearchedRestaurant();
    this.getRestaurantOpinions()
    this.isOpen = this.deliveryService.checkIfOpen(this.restaurant.deliveryHours);

    this.getAverageRating();
  }

  getSearchedRestaurant() {
    this.restaurant = this.searchedRestaurantService.getSearchedRestaurant(this.restaurantId);
  }

  getRestaurantOpinions() {
    this.restaurantOpinionService.getRestaurantOpinions(this.restaurantId).subscribe({
      next: (opinions: RestaurantOpinion[]) => {
        this.restaurantOpinions = opinions;
      },
      error: (err) => {
        console.error('Error fetching restaurant opinions:', err);
      }
    });
  }

  getAverageRating() {
    this.restaurantOpinionService.getRating(this.restaurantId).subscribe({
      next: (rating: number) => {
        this.rating = rating
      },
      error: (err) => {
        console.error('Error fetching average rating:', err);
      }
    });
  }

  getRatingLength(): number {
    if (typeof sessionStorage !== 'undefined') {
      sessionStorage.setItem('ratingLength', this.restaurantOpinions.length.toString());
    }
    return this.restaurantOpinions.length;
  }

  goToMenu(restaurant: SearchedRestaurant) {
    const formattedName = restaurant.name.replace(/[\s,]+/g, '-');
    this.searchedRestaurantService.setSelectedRestaurant(restaurant.restaurantId);

    this.router.navigate(['/menu', formattedName]);
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k) {
    return this.languageService.getTranslation(key);
  }

}
