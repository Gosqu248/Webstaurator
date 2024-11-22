import { Component, OnInit, OnChanges } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MenuComponent } from '../../menu-components/menu/menu.component';
import { NgForOf, NgIf} from '@angular/common';
import { RestaurantCategoryComponent } from '../restaurant-category/restaurant-category.component';
import { FormsModule } from '@angular/forms';
import { LanguageService } from '../../../services/language.service';
import { LanguageTranslations } from '../../../interfaces/language.interface';
import { RestaurantSortComponent } from '../restaurant-sort/restaurant-sort.component';

import { RestaurantItemComponent } from '../restaurant-item/restaurant-item.component';
import { OptionService } from '../../../services/option.service';
import {RestaurantAddressService} from "../../../services/restaurant-address.service";
import {SearchedRestaurant} from "../../../interfaces/searched-restaurant";
import {RestaurantMapComponent} from "../restaurant-map/restaurant-map.component";
import {MapService} from "../../../services/map.service";

@Component({
  selector: 'app-restaurant',
  standalone: true,
  imports: [
    RouterOutlet,
    MenuComponent,
    NgIf,
    RestaurantCategoryComponent,
    FormsModule,
    RestaurantSortComponent,
    RestaurantItemComponent,
    NgForOf,
    RestaurantMapComponent
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
  showMap = false;
  address: string = '';


  constructor(
    private languageService: LanguageService,
    private restaurantAddressService: RestaurantAddressService,
    private mapService: MapService,
    private optionService: OptionService
  ) {}

  ngOnInit(): void {
    this.loadRestaurant();
    this.optionService.selectedOption$.subscribe(() => {
      this.updateRestaurant();
    });
    this.optionService.selectedCategories$.subscribe(() => {
      this.filterRestaurants();
    });
    this.mapService.showMap$.subscribe(showMap =>
      this.showMap = showMap
    );
  }

  ngOnChanges() {
    this.updateRestaurant();
  }

  private loadRestaurant() {
    const address = sessionStorage.getItem('searchAddress');

    if (!address) {
      throw new Error('No address found');
    }
    this.address = address;


    this.restaurantAddressService.searchedRestaurants$.subscribe((restaurants) => {
      this.searchedRestaurants = restaurants;
      this.updateRestaurant();
    });

  }


  private updateRestaurant() {
    const deliveryOption = this.optionService.selectedOption.value;
     if (deliveryOption === 'pickup') {
       this.deliveredRestaurant = this.searchedRestaurants.filter(restaurant => restaurant.pickup)
       this.optionService.setCategories(this.deliveredRestaurant.map(restaurant => restaurant.category));
     } else {
       this.deliveredRestaurant = this.searchedRestaurants;
       this.optionService.setCategories(this.deliveredRestaurant.map(restaurant => restaurant.category));
     }
    this.filterRestaurants();
  }

  private filterRestaurants() {
    let currentFiltered = [...this.deliveredRestaurant];

    if (this.searchRestaurant) {
      currentFiltered = currentFiltered.filter((restaurant) =>
        restaurant.name.toLowerCase().includes(this.searchRestaurant.toLowerCase())
      );
    }

    this.categories = this.optionService.selectedCategories.value;
    if (this.categories.length > 0) {
      currentFiltered = currentFiltered.filter((restaurant) =>
        this.categories.some(category => restaurant.category === category)
      );
    }


    this.optionService.selectedSortItem$.subscribe((sortType) => {
      this.filteredRestaurants = [...currentFiltered];

      if (sortType === this.getTranslation('closeToMe')) {
        this.filteredRestaurants.sort((a, b) => a.distance - b.distance);
      } else if (sortType === this.getTranslation('grades')) {
        this.filteredRestaurants.sort((a, b) => b.rating - a.rating);
      } else if (sortType === this.getTranslation('cheapestDelivery')) {
        this.filteredRestaurants.sort((a, b) => a.deliveryPrice - b.deliveryPrice);
      }
    });

    this.filteredRestaurants = currentFiltered;
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
