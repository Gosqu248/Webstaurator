import { Component, OnInit, OnChanges } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MenuComponent } from '../../menu-components/menu/menu.component';
import { NgForOf, NgIf } from '@angular/common';
import { ResturantCategoryComponent } from '../resturant-category/resturant-category.component';
import { FormsModule } from '@angular/forms';
import { LanguageService } from '../../../services/language.service';
import { LanguageTranslations } from '../../../interfaces/language.interface';
import { RestaurantSortComponent } from '../restaurant-sort/restaurant-sort.component';
import { RestaurantsService } from '../../../services/restaurants.service';
import { Restaurant } from '../../../interfaces/restaurant';
import { RestaurantItemComponent } from '../restaurant-item/restaurant-item.component';
import { OptionService } from '../../../services/option.service';

@Component({
  selector: 'app-restaurant',
  standalone: true,
  imports: [
    RouterOutlet,
    MenuComponent,
    NgIf,
    ResturantCategoryComponent,
    FormsModule,
    RestaurantSortComponent,
    RestaurantItemComponent,
    NgForOf
  ],
  templateUrl: './restaurant.component.html',
  styleUrl: './restaurant.component.css'
})
export class RestaurantComponent implements OnInit, OnChanges {
  searchRestaurant: any;
  deliveryRestaurants: Restaurant[] = [];
  pickupRestaurants: Restaurant[] = [];
  restaurants: Restaurant[] = [];

  constructor(
    private languageService: LanguageService,
    private restaurantService: RestaurantsService,
    private optionService: OptionService
  ) {}

  ngOnInit(): void {
    this.loadRestaurant();
    this.optionService.selectedOption$.subscribe(() => {
      this.updateRestaurant();
    });
  }

  ngOnChanges() {
    this.updateRestaurant();
  }

  private loadRestaurant() {
    this.restaurantService.getDeliveryRestaurants().subscribe((restaurant) => {
      this.deliveryRestaurants = restaurant;
      this.updateRestaurant();
    });
    this.restaurantService.getPickupRestaurants().subscribe((restaurant) => {
      this.pickupRestaurants = restaurant;
      this.updateRestaurant();
    });
  }

  private updateRestaurant() {
    const deliveryOption = this.optionService.selectedOption.value;
    this.restaurants = deliveryOption === 'delivery' ? this.deliveryRestaurants : this.pickupRestaurants;
  }

  getRestaurantQuantity() {
    return this.restaurants.length;
  }



  getTranslation<k extends keyof LanguageTranslations>(key: k) {
    return this.languageService.getTranslation(key);
  }
}
