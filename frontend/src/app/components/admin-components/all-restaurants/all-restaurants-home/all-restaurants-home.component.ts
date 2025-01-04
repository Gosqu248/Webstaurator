import {Component, OnInit} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";
import {RestaurantService} from "../../../../services/restaurant.service";
import {Restaurant} from "../../../../interfaces/restaurant";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {NgForOf, NgIf} from "@angular/common";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {AllRestaurantsItemComponent} from "../all-restaurants-item/all-restaurants-item.component";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-all-restaurants-home',
  standalone: true,
  imports: [
    MatProgressSpinner,
    NgIf,
    NgForOf,
    AllRestaurantsItemComponent,
    FormsModule
  ],
  templateUrl: './all-restaurants-home.component.html',
  styleUrls: ['./all-restaurants-home.component.css']
})
export class AllRestaurantsHomeComponent implements OnInit{
  restaurants: Restaurant[] = [];
  isLoading: boolean = true;
  searchRestaurant: string = '';
  filteredRestaurants: Restaurant[] = [];

  constructor(private languageService: LanguageService,
              private restaurantService: RestaurantService) {}

  ngOnInit() {
    this.restaurantService.getAllRestaurants().subscribe({
      next: (restaurants) => {
        this.restaurants = restaurants;
        this.filterRestaurants();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error getting restaurants:', error);
      }
    });
  }

  filterRestaurants() {
    if (this.searchRestaurant.trim().length === 0) {
      this.filteredRestaurants = this.restaurants;
    } else {
      const searchQuery = this.searchRestaurant.toLowerCase();
      this.filteredRestaurants = this.restaurants.filter(restaurant => {
        return restaurant.name.toLowerCase().includes(searchQuery);
      });
    }
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
