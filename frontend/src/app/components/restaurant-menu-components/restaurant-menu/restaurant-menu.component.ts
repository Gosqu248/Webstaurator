import {Component, OnInit} from '@angular/core';
import {RestaurantBasketComponent} from "../restaurant-basket/restaurant-basket.component";
import {ActivatedRoute} from "@angular/router";
import { Restaurant } from '../../../interfaces/restaurant';
import {RestaurantsService} from "../../../services/restaurants.service";
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

  constructor(private route: ActivatedRoute, private restaurantService: RestaurantsService) {}

  ngOnInit(): void {
    this.getRestaurantId();
    this.getRestaurant();

  }

  getRestaurantId() {
    this.route.queryParams.subscribe(() => {
      const id = sessionStorage.getItem('restaurantId');
      this.restaurantId = id ? parseInt(id) : 0;
    });
  }

  getRestaurant() {
    if (this.restaurantId) {
      this.restaurantService.getRestaurantById(this.restaurantId).subscribe((data: Restaurant) => {
        this.restaurant = data;
        this.image = data.imageUrl;
      });
    }
  }
}
