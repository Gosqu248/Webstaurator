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
  @Input() restaurant!: any;
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
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngOnInit() {
    this.loading = false;
    this.getMenu();
    this.getCategories();
    this.getSelected();
    this.filterMenu();
    this.setSessionRestaurant();
    this.checkIfFavourite();
    this.optionService.favouriteRestaurantIds$.subscribe((restaurantIds: number[]) => {
      this.isFavorite = restaurantIds.includes(this.restaurant.id);
    });
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
    this.menuService.getMenuByRestaurantId(this.restaurant.id).subscribe(menu => {
      this.menu = menu;
      console.log('Menu: ', this.menu)
      this.filterMenu();
    });
  }

  getSelected() {
    this.optionService.selectedMenuCategory$.subscribe(category => {
      this.selectedCategory = category;
      this.filterMenu();
    });
  }

  getCategories() {
    this.menuService.getCategories(this.restaurant.id).subscribe(categories => {
      this.categories = categories;
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
    return RatingUtil.getAverageRating(this.restaurant.restaurantOpinions);
  }

  getRatingLength(): number {
    return this.restaurant.restaurantOpinions.length;
  }

  openInfo(): void {
    const dialogRef = this.dialog.open(InfoDialogComponent, {
      width: '1200px',  // Adjusted width
      height: '90vh',  // Adjusted height
    });

    dialogRef.afterClosed().subscribe(() => {
      console.log('Dialog was closed');
    });
  }

  setSessionRestaurant() {
    sessionStorage.setItem('restaurantId', this.restaurant.id.toString());
    sessionStorage.setItem('restaurantName', this.restaurant.name);
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

}
