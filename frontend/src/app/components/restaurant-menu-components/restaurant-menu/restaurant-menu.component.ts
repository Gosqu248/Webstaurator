import {Component, OnInit} from '@angular/core';
import {RestaurantBasketComponent} from "../restaurant-basket/restaurant-basket.component";
import { Restaurant } from '../../../interfaces/restaurant';
import {RestaurantService} from "../../../services/api/restaurant.service";
import {RestaurantMainComponent} from "../restaurant-main/restaurant-main.component";
import {SearchedRestaurantsService} from "../../../services/state/searched-restaurant.service";
import {SearchedRestaurant} from "../../../interfaces/searched-restaurant";

@Component({
  selector: 'app-restaurant-menu',
  standalone: true,
  imports: [
    RestaurantBasketComponent,
    RestaurantMainComponent
  ],
  templateUrl: './restaurant-menu.component.html',
  styleUrl: './restaurant-menu.component.css'
})
export class RestaurantMenuComponent implements OnInit {
  restaurant: SearchedRestaurant = {} as SearchedRestaurant;

  constructor(private searchedRestaurantService: SearchedRestaurantsService) {}

  ngOnInit(): void {
    this.getRestaurant();

  }

  getRestaurant() {
    if (typeof sessionStorage !== 'undefined') {
      const restaurantId = sessionStorage.getItem('restaurantId');
      if (restaurantId) {
        const id = parseInt(restaurantId);
        this.restaurant = this.searchedRestaurantService.getSearchedRestaurant(id)
      }
    }
  }
}
