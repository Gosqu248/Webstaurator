import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {NgIf, NgForOf, DecimalPipe} from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
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
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {Restaurant} from "../../../interfaces/restaurant";
import {RestaurantService} from "../../../services/restaurant.service";
import {AuthService} from "../../../services/auth.service";
import {MenuLoginComponent} from "../../menu-components/menu-login/menu-login.component";
import {Delivery} from "../../../interfaces/delivery.interface";
import {DeliveryService} from "../../../services/delivery.service";
import {RestaurantOpinion} from "../../../interfaces/restaurant-opinion";
import {RestaurantOpinionService} from "../../../services/restaurant-opinion.service";
import {Subscription} from "rxjs";

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
export class RestaurantMainComponent implements OnInit, OnDestroy {
  @Input() restaurantId!: number;
  restaurant: Restaurant = {} as Restaurant;
  delivery: Delivery = {} as Delivery;
  searchMenu: any;
  menu: Menu[] = [];
  categories: string[] = [];
  selectedCategory: string = '';
  filteredMenu: Menu[] = [];
  isFavorite: boolean = false;
  userId: number = 0;
  loading: boolean = true;
  restaurantOpinions: RestaurantOpinion[] = [];
  private loginSubscription!: Subscription;
  rating: number = 0;


  constructor(
    private languageService: LanguageService,
    private menuService: MenuService,
    private optionService: OptionService,
    private favouriteService: FavouriteService,
    private restaurantOpinionService: RestaurantOpinionService,
    private deliveryService: DeliveryService,
    private restaurantService: RestaurantService,
    private authService: AuthService,
    private dialog: MatDialog,
  ) {}

  ngOnInit() {
    this.loading = false;
      this.getRestaurant();
      this.checkIfFavourite();
      this.getCategories();
      this.getSelected();
      this.getMenu();
      this.filterMenu();
      this.getAverageRating();
    this.loginSubscription = this.authService.loginEvent.subscribe(() => {
      this.refreshFavourite();
    });
  }

  ngOnDestroy() {
    if (this.loginSubscription) {
      this.loginSubscription.unsubscribe();
    }
  }

  refreshFavourite() {
    this.isFavourite(this.restaurant.id);
    console.log('Odświeżam dane ulubionych!');
  }

  getRestaurant() {
    if (this.restaurantId) {
      this.restaurantService.getRestaurantById(this.restaurantId).subscribe((data: Restaurant) => {
        this.restaurant = data;
        this.getRestaurantOpinions(data.id);
        this.getDelivery(data.id);
      });
    }
  }


  isFavourite(restaurantId: number) {
    this.userId = parseInt(localStorage.getItem('userId') || '0', 10);

    this.favouriteService.isFavorite(this.userId, restaurantId).subscribe((isFavourite) => {
      this.isFavorite = isFavourite;
    });
  }
  getDelivery(restaurantId: number) {
    this.deliveryService.getDelivery(restaurantId).subscribe((delivery) => {
      this.delivery = delivery;
    });
  }

  getRestaurantOpinions(restaurantId: number) {
    this.restaurantOpinionService.getRestaurantOpinions(restaurantId).subscribe({
      next: (opinions: RestaurantOpinion[]) => {
        this.restaurantOpinions = opinions;
      },
      error: (err) => {
        console.error('Error fetching restaurant opinions:', err);
      }
    });
    }

  toggleFavourite() {
    if (this.authService.isAuthenticated()) {
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
      }
    } else {
      this.dialog.open(MenuLoginComponent)
    }
  }


  checkIfFavourite() {
    if (!this.authService.isAuthenticated()) {
      this.isFavorite = false;
    } else {
      this.userId = parseInt(localStorage.getItem('userId') || '0', 10);
      if (this.userId !== 0) {
        this.isFavourite(this.restaurantId);
      } else {
        this.isFavorite = false;
        this.loading = false;
      }

    }
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
    if (this.restaurant && this.restaurantOpinions) {
      this.restaurantOpinionService.getRating(this.restaurantId).subscribe({
        next: (rating: number) => {
          this.rating = rating
        },
        error: (err) => {
          console.error('Error fetching average rating:', err);
        }
      });
    }
    return 0;
  }

  getRatingLength(): number {
    if (this.restaurant && this.restaurantOpinions) {
      return this.restaurantOpinions.length;
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
