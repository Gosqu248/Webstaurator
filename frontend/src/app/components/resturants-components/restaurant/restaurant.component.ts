import { Component, OnInit, OnChanges } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MenuComponent } from '../../menu-components/menu/menu.component';
import {isPlatformBrowser, NgForOf, NgIf} from '@angular/common';
import { ResturantCategoryComponent } from '../resturant-category/resturant-category.component';
import { FormsModule } from '@angular/forms';
import { LanguageService } from '../../../services/language.service';
import { LanguageTranslations } from '../../../interfaces/language.interface';
import { RestaurantSortComponent } from '../restaurant-sort/restaurant-sort.component';
import { RestaurantsService } from '../../../services/restaurants.service';
import { Restaurant } from '../../../interfaces/restaurant';
import { RestaurantItemComponent } from '../restaurant-item/restaurant-item.component';
import { OptionService } from '../../../services/option.service';
import {RestaurantAddressService} from "../../../services/restaurant-address.service";
import {SearchedRestaurant} from "../../../interfaces/searched-restaurant";

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
  searchedRestaurants: SearchedRestaurant[] = [];
  searchRestaurant: string = '';
  deliveredRestaurant: SearchedRestaurant[] = [];
  restaurants: SearchedRestaurant[] = [];
  filteredRestaurants: SearchedRestaurant[] = [];
  categories: string[] = [];


  constructor(
    private languageService: LanguageService,
    private restaurantAddressService: RestaurantAddressService,
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
    const address = sessionStorage.getItem('searchAddress');

    if (!address) {
      throw new Error('No address found');
    }
    this.restaurantAddressService.searchedRestaurant(address).subscribe((restaurant) => {
      this.searchedRestaurants = restaurant;
      this.updateRestaurant();
    });

  }

  private updateRestaurant() {
    const deliveryOption = this.optionService.selectedOption.value;
     deliveryOption === 'pickup'
       ? this.deliveredRestaurant = this.searchedRestaurants.filter(restaurant => restaurant.pickup)
       : this.deliveredRestaurant = this.searchedRestaurants;
    this.filterRestaurants();
  }

  private filterRestaurants() {
    this.categories = this.optionService.selectedCategories.value;

    this.searchRestaurant
        ? this.filteredRestaurants = this.deliveredRestaurant.filter((restaurant) => restaurant.name.toLowerCase().includes(this.searchRestaurant.toLowerCase()))
       : this.filteredRestaurants = this.deliveredRestaurant;

    if (this.categories.length > 0) {
      this.filteredRestaurants = this.filteredRestaurants.filter((restaurant) =>
        this.categories.some(category => restaurant.category === category)
      );
    }

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
