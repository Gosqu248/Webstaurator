import {Component, Input, OnInit} from '@angular/core';
import {Restaurant, RestaurantOpinions} from "../../../interfaces/restaurant";
import {DeliveryService} from "../../../services/delivery.service";
import {DeliveryHour} from "../../../interfaces/delivery.interface";
import {DecimalPipe, NgIf} from "@angular/common";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {Router} from "@angular/router";
import {RatingUtil} from "../../../utils/rating-util";
import {FavouriteService} from "../../../services/favourite.service";
import {Favourites} from "../../../interfaces/favourites";

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
  @Input() restaurant!: Restaurant;
  deliveryTime: DeliveryHour[] = [];
  isOpen: boolean = true;

  constructor(private deliveryService: DeliveryService,
              private languageService:LanguageService,
              private router:Router) {}

  ngOnInit() {
    this.getDeliveryTime();
    this.setSessionRestaurant();
  }

  getAverageRating(): number {
    return RatingUtil.getAverageRating(this.restaurant.restaurantOpinions);
  }

  getRatingLength(): number {
    sessionStorage.setItem('ratingLength', this.restaurant.restaurantOpinions.length.toString());
    return this.restaurant.restaurantOpinions.length;
  }

  getDeliveryTime(): void {
    this.deliveryService.getDeliveryTIme(this.restaurant.id).subscribe((data) => {
      this.deliveryTime = data;
      this.isOpen = this.deliveryService.checkIfOpen(this.deliveryTime);
    });
  }

  setSessionRestaurant() {
    if (this.restaurant && this.restaurant.delivery) {
      sessionStorage.setItem('restaurantName', this.restaurant.name);
      sessionStorage.setItem('deliveryMin', this.restaurant.delivery.deliveryMinTime.toString());
      sessionStorage.setItem('deliveryMax', this.restaurant.delivery.deliveryMaxTime.toString());
      sessionStorage.setItem('deliveryPrice', this.restaurant.delivery.deliveryPrice.toString());
      sessionStorage.setItem("minPrice", this.restaurant.delivery.minimumPrice.toString());
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
