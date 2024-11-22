import {Component, OnInit} from '@angular/core';
import {RestaurantBasketComponent} from "../restaurant-basket/restaurant-basket.component";
import { Restaurant } from '../../../interfaces/restaurant';
import {RestaurantService} from "../../../services/restaurant.service";
import {RestaurantMainComponent} from "../restaurant-main/restaurant-main.component";

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
  restaurantId: number = 0;
  restaurant: Restaurant = {} as Restaurant;
  image: string = '';

  constructor(private restaurantService: RestaurantService) {}

  ngOnInit(): void {
    this.getRestaurant();

  }

  getRestaurant() {
    if (typeof sessionStorage !== 'undefined') {


      const restaurantId = sessionStorage.getItem('restaurantId');
      if (restaurantId) {
        const id = parseInt(restaurantId);
        this.restaurantId = id;
        this.restaurantService.getRestaurantById(id).subscribe((data: Restaurant) => {
          this.restaurant = data;
          this.image = data.imageUrl;
        });
      }
    }
  }
}
