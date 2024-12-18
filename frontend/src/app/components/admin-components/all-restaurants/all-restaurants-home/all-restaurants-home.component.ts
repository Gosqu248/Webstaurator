import {Component, OnInit} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";
import {RestaurantService} from "../../../../services/restaurant.service";
import {Restaurant} from "../../../../interfaces/restaurant";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {NgForOf, NgIf} from "@angular/common";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {AllRestaurantsItemComponent} from "../all-restaurants-item/all-restaurants-item.component";

@Component({
  selector: 'app-all-restaurants-home',
  standalone: true,
  imports: [
    MatProgressSpinner,
    NgIf,
    NgForOf,
    AllRestaurantsItemComponent
  ],
  templateUrl: './all-restaurants-home.component.html',
  styleUrl: './all-restaurants-home.component.css'
})
export class AllRestaurantsHomeComponent implements OnInit{
  restaurants:Restaurant[] = [];
  isLoading: boolean = true;
  constructor(private languageService:LanguageService,
              private restaurantService: RestaurantService) {}

  ngOnInit() {
    this.restaurantService.getAllRestaurants().subscribe({
      next: (restaurants) => {
        this.restaurants = restaurants;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error getting restaurants:', error);
      }
    });
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
