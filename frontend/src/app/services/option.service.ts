import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {FavouriteService} from "./favourite.service";

@Injectable({
  providedIn: 'root'
})
export class OptionService {

  constructor(private favouriteService: FavouriteService) {
  }

  selectedOption = new BehaviorSubject<string>('delivery');
  selectedOption$ = this.selectedOption.asObservable();

  selectedCategories = new BehaviorSubject<string[]>([]);
  selectedCategories$ = this.selectedCategories.asObservable();

  selectBasketDelivery = new BehaviorSubject<string>('delivery');

  selectedMenuCategory = new BehaviorSubject<string>('');
  selectedMenuCategory$ = this.selectedMenuCategory.asObservable();

  favouriteRestaurantIdsSource = new BehaviorSubject<number[]>([]);
  favouriteRestaurantIds$ = this.favouriteRestaurantIdsSource.asObservable();

  setSelectedOption(option: string) {
    this.selectedOption.next(option);
  }

  setSelectedCategories(categories: string[]) {
    this.selectedCategories.next(categories);
  }

  setSelectBasketDelivery(delivery: string) {
    this.selectBasketDelivery.next(delivery);
  }

  setSelectedMenuCategories(category: string) {
    this.selectedMenuCategory.next(category);
  }

  addFavourite(restaurantId: number) {
    const currentFavourites = this.favouriteRestaurantIdsSource.value;
    currentFavourites.push(restaurantId);
    this.favouriteRestaurantIdsSource.next(currentFavourites);
  }

  removeFavourite(restaurantId: number) {
    const currentFavourites = this.favouriteRestaurantIdsSource.value;
    const index = currentFavourites.indexOf(restaurantId);
    if (index > -1) {
      currentFavourites.splice(index, 1);
    }
    this.favouriteRestaurantIdsSource.next(currentFavourites);
  }


  fetchFavouritesFromDatabase(userId: number) {
    this.favouriteService.getUserFavourites(userId).subscribe(favourites => {
      this.favouriteRestaurantIdsSource.next(favourites.map(fav => fav.restaurantId));
    });
  }



  clearFavourites() {
    this.favouriteRestaurantIdsSource.next([]);

  }


}
