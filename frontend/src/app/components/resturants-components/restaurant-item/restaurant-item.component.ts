import {Component, Input, OnInit} from '@angular/core';
import {Restaurant, RestaurantOpinions} from "../../../interfaces/restaurant";
import {DeliveryService} from "../../../services/delivery.service";
import {DeliveryHour} from "../../../interfaces/delivery.interface";
import {NgIf} from "@angular/common";
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
    NgIf
  ],
  templateUrl: './restaurant-item.component.html',
  styleUrl: './restaurant-item.component.css'
})
export class RestaurantItemComponent implements OnInit{
  @Input() restaurant!: Restaurant;
  deliveryTime: DeliveryHour[] = [];
  isOpen: boolean = true;
  isFavorite: boolean = false;

  constructor(private deliveryService: DeliveryService,
              private languageService:LanguageService,
              private router:Router) {}

  ngOnInit() {
    this.getDeliveryTime();
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
      this.checkIfOpen();
    });
  }

  checkIfOpen(): void {
    const currentTime = new Date();
    const currentDay = currentTime.getDay(); // 0: Sunday, 1: Monday, ..., 6: Saturday
    const currentHour = currentTime.getHours();
    const currentMinutes = currentTime.getMinutes();
    const currentTotalMinutes = currentHour * 60 + currentMinutes;
    console.log(currentDay)


    const todayDeliveryHour = this.deliveryTime.find(d => d.dayOfWeek === currentDay); // Adjust for Sunday
    console.log(this.deliveryTime)

    if (todayDeliveryHour) {
      const openingTime = todayDeliveryHour.openTime.split(':');
      const closingTime = todayDeliveryHour.closeTime.split(':');


      const openingTotalMinutes = parseInt(openingTime[0]) * 60 + parseInt(openingTime[1]);
      const closingTotalMinutes = parseInt(closingTime[0]) * 60 + parseInt(closingTime[1]);

      this.isOpen = currentTotalMinutes >= openingTotalMinutes && currentTotalMinutes <= closingTotalMinutes;
    } else {
      this.isOpen = false;
    }
  }

  goToMenu(restaurant: Restaurant) {
    const formattedName = restaurant.name.replace(/[\s,]+/g, '-');
    const id = restaurant.id;
    this.router.navigate(['/menu', formattedName], { queryParams: { id } });
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k) {
    return this.languageService.getTranslation(key);
  }

}
