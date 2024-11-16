import {Component, Input, OnInit} from '@angular/core';
import {Restaurant} from "../../../interfaces/restaurant";
import {DeliveryService} from "../../../services/delivery.service";
import {Delivery, DeliveryHour} from "../../../interfaces/delivery.interface";
import {DecimalPipe, NgIf} from "@angular/common";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {Router} from "@angular/router";
import {RatingUtil} from "../../../utils/rating-util";
import {RestaurantOpinion} from "../../../interfaces/restaurant-opinion";
import {RestaurantOpinionService} from "../../../services/restaurant-opinion.service";
import {RestaurantsService} from "../../../services/restaurants.service";


@Component({
  selector: 'app-restaurant-item',
  standalone: true,
    imports: [
        NgIf,
        DecimalPipe
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

  constructor(private deliveryService: DeliveryService,
              private languageService:LanguageService,
              private restaurantService: RestaurantsService,
              private restaurantOpinionService: RestaurantOpinionService,
              private router:Router) {}

  ngOnInit() {
    this.getRestaurant();
    this.getDelivery()
    this.getDeliveryTime();
    this.getRestaurantOpinions()
  }

  getRestaurant() {
    this.restaurantService.getRestaurantById(this.restaurantId).subscribe((restaurant) => {
      this.restaurant = restaurant;
    });
  }

  getDelivery() {
    this.deliveryService.getDelivery(this.restaurantId).subscribe((delivery) => {
      this.restaurantDelivery = delivery;
      this.setSessionRestaurant()
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

  getAverageRating(): number {
    return RatingUtil.getAverageRating(this.restaurantOpinions);
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

  setSessionRestaurant() {
    if (this.restaurantId && this.restaurantDelivery && typeof sessionStorage !== 'undefined') {
      sessionStorage.setItem('restaurantName', this.restaurant.name)
      sessionStorage.setItem('deliveryMin', this.restaurantDelivery.deliveryMinTime.toString());
      sessionStorage.setItem('deliveryMax', this.restaurantDelivery.deliveryMaxTime.toString());
      sessionStorage.setItem('deliveryPrice', this.restaurantDelivery.deliveryPrice.toString());
      sessionStorage.setItem("minPrice", this.restaurantDelivery.minimumPrice.toString());
      sessionStorage.setItem("pickupTime", this.restaurantDelivery.pickupTime.toString());
    }
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
