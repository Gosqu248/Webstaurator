import {Component, Input, OnInit} from '@angular/core';
import {Restaurant} from "../../../interfaces/restaurant";
import {DeliveryService} from "../../../services/api/delivery.service";
import {Delivery, DeliveryHour} from "../../../interfaces/delivery.interface";
import {DecimalPipe, NgIf} from "@angular/common";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/state/language.service";
import {Router} from "@angular/router";
import {RestaurantOpinion} from "../../../interfaces/restaurant-opinion";
import {RestaurantOpinionService} from "../../../services/api/restaurant-opinion.service";
import {RestaurantService} from "../../../services/api/restaurant.service";
import {MatProgressSpinner} from "@angular/material/progress-spinner";


@Component({
  selector: 'app-restaurant-item',
  standalone: true,
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
  restaurant: Restaurant = {} as Restaurant;
  restaurantDelivery: Delivery = {} as Delivery;
  deliveryTime: DeliveryHour[] = [];
  isOpen: boolean = true;
  restaurantOpinions: RestaurantOpinion[] = [];
  rating: number = 0;

  constructor(private deliveryService: DeliveryService,
              private languageService:LanguageService,
              private restaurantService: RestaurantService,
              private restaurantOpinionService: RestaurantOpinionService,
              private router:Router) {}

  ngOnInit() {
    this.getRestaurant();
    this.getRestaurantOpinions()
    this.getDelivery()
    this.getDeliveryTime();
    this.getAverageRating();
  }

  getRestaurant() {
    this.restaurantService.getRestaurantById(this.restaurantId).subscribe((restaurant) => {
      this.restaurant = restaurant;
    });
  }

  getDelivery() {
    this.deliveryService.getDelivery(this.restaurantId).subscribe((delivery) => {
      this.restaurantDelivery = delivery;
    });
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

  getDeliveryTime(): void {
    this.deliveryService.getDeliveryTIme(this.restaurantId).subscribe((data) => {
      this.deliveryTime = data;
      this.isOpen = this.deliveryService.checkIfOpen(this.deliveryTime);
    });
  }


  goToMenu(restaurant: Restaurant) {
    const formattedName = restaurant.name.replace(/[\s,]+/g, '-');
    sessionStorage.setItem('restaurantId', restaurant.id.toString());

    this.router.navigate(['/menu', formattedName]);
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k) {
    return this.languageService.getTranslation(key);
  }

}
