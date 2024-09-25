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
  filteredRestaurants: Restaurant[] = [];
  categories: string[] = [];


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
    this.optionService.selectedCategories$.subscribe(() => {
      this.filterRestaurants();
    })
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
    this.filterRestaurants();
  }

  private filterRestaurants() {
    this.categories = this.optionService.selectedCategories.value;
    this.categories.length > 0
      ? this.filteredRestaurants = this.restaurants.filter((restaurant) => this.categories.includes(restaurant.category))
      : this.filteredRestaurants = this.restaurants;

    this.searchRestaurant
      ? this.filteredRestaurants = this.filteredRestaurants.filter((restaurant) => restaurant.name.toLowerCase().includes(this.searchRestaurant.toLowerCase()))
      : null

  }


  onSearchChange(searchValue: string): void {
    this.searchRestaurant = searchValue;
    this.filterRestaurants();
  }

  getRestaurantQuantity() {
    return this.filteredRestaurants.length;
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k) {
    return this.languageService.getTranslation(key);
  }
}
