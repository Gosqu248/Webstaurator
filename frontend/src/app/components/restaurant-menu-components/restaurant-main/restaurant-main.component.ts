import { Component, Input, OnInit} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {NgIf, NgForOf, DecimalPipe} from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LanguageService } from '../../../services/language.service';
import { MenuService } from '../../../services/menu.service';
import { OptionService } from '../../../services/option.service';
import { FavouriteService } from '../../../services/favourite.service';
import { InfoDialogComponent } from '../info-dialog/info-dialog.component';
import { MenuCategoryItemComponent } from '../menu-category-item/menu-category-item.component';
import { FilterByCategoryPipe } from '../../../pipes/filter-by-category.pipe';
import { RestaurantMenuItemComponent } from '../restaurant-menu-item/restaurant-menu-item.component';
import { NgOptimizedImage } from '@angular/common';
import {Menu} from "../../../interfaces/menu";
import {Favourites} from "../../../interfaces/favourites";
import {RatingUtil} from "../../../utils/rating-util";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {Restaurant} from "../../../interfaces/restaurant";
import {RestaurantsService} from "../../../services/restaurants.service";

@Component({
  selector: 'app-restaurant-main',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule,
    FormsModule,
    MenuCategoryItemComponent,
    NgForOf,
    FilterByCategoryPipe,
    RestaurantMenuItemComponent,
    NgOptimizedImage,
    InfoDialogComponent,
    DecimalPipe
  ],
  templateUrl: './restaurant-main.component.html',
  styleUrls: ['./restaurant-main.component.css']
})
export class RestaurantMainComponent implements OnInit {
  @Input() restaurantId!: number;
  restaurant: Restaurant = {} as Restaurant;
  searchMenu: any;
  menu: Menu[] = [];
  categories: string[] = [];
  selectedCategory: string = '';
  filteredMenu: Menu[] = [];
  isAuthChecked: boolean = false;
  isFavorite: boolean = false;
  userId: number = 0;
  loading: boolean = true;

  constructor(
    private languageService: LanguageService,
    private menuService: MenuService,
    private optionService: OptionService,
    private favouriteService: FavouriteService,
    private restaurantService: RestaurantsService,
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngOnInit() {
    this.loading = false;
      this.getRestaurant();
      this.checkIfFavourite();
      this.getCategories();
      this.getSelected();
      this.getMenu();
      this.filterMenu();
      this.optionService.favouriteRestaurantIds$.subscribe((restaurantIds: number[]) => {
        this.isFavorite = restaurantIds.includes(this.restaurant.id);
      });

  }

  getRestaurant() {
    if (this.restaurantId) {
      this.restaurantService.getRestaurantById(this.restaurantId).subscribe((data: Restaurant) => {
        this.restaurant = data;
      });
    }
  }
  toggleFavourite() {
    if (this.checkAuth()) {
      if (this.isFavorite) {
        this.favouriteService.deleteFavourite(this.userId, this.restaurant.id).subscribe({
          next: () => {
            this.isFavorite = false;

            console.log('Favourite deleted');
          },
          error: () => {
            console.log('Error deleting favourite');
          }
        });
        this.optionService.removeFavourite(this.restaurant.id)
      } else {
        this.favouriteService.addFavourite(this.userId, this.restaurant.id).subscribe({
          next: () => {
            this.isFavorite = true;
            console.log('Favourite added');
          },
          error: () => {
            console.log('Error adding favourite');
          }
        });
        this.optionService.addFavourite(this.restaurant.id)
      }
    } else {
      this.router.navigate([], { fragment: 'login' });
    }
  }

  checkAuth() {
    const token = localStorage.getItem('jwt');
    this.isAuthChecked = token != null;
    return this.isAuthChecked;
  }

  checkIfFavourite() {
    if (!this.checkAuth()) {
      this.isFavorite = false;
    } else {
      this.userId = parseInt(localStorage.getItem('userId') || '0', 10);
      if (this.userId !== 0) {
        this.favouriteService.getUserFavourites(this.userId).subscribe((favourites: Favourites[]) => {
          this.isFavorite = favourites.some((fav: Favourites) => fav.restaurantId === this.restaurant.id);
        });
      } else {
        this.isFavorite = false; // Set isFavorite to false if the user is not logged in
        this.loading = false;
      }

    }

    console.log('Is favourite: ', this.isFavorite);
  }
  getMenu() {
    if (this.restaurantId) {
      this.menuService.getMenuByRestaurantId(this.restaurantId).subscribe(menu => {
        this.menu = menu;
        this.filterMenu();
      });
    }
  }

  getCategories() {
    if (this.restaurantId) {
      this.menuService.getCategories(this.restaurantId).subscribe(categories => {
        this.categories = categories;
      });
    }
  }
  getSelected() {
    this.optionService.selectedMenuCategory$.subscribe(category => {
      this.selectedCategory = category;
      this.filterMenu();
    });
  }



  selectCategory(category: string) {
    if (this.selectedCategory === category) {
      this.selectedCategory = '';
      this.optionService.setSelectedMenuCategories('');
    } else {
      this.selectedCategory = category;
      this.optionService.setSelectedMenuCategories(category);
    }
    this.filterMenu();
  }

  filterMenu() {
    if (this.searchMenu) {
      this.filteredMenu = this.menu.filter(
        item =>
          item.name.toLowerCase().includes(this.searchMenu.toLowerCase()) &&
          (!this.selectedCategory || item.category === this.selectedCategory)
      );
    } else {
      this.filteredMenu = this.menu;
    }
  }

  getAverageRating(): number {
    if (this.restaurant && this.restaurant.restaurantOpinions) {
      return RatingUtil.getAverageRating(this.restaurant.restaurantOpinions);
    }
    return 0;
  }

  getRatingLength(): number {
    if (this.restaurant && this.restaurant.restaurantOpinions) {
      return this.restaurant.restaurantOpinions.length;
    }
    return 0;
  }

  openInfo(): void {
    this.dialog.open(InfoDialogComponent, {
      width: '1200px',  // Adjusted width
      height: '90vh',  // Adjusted height
    });
  }


  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

}
