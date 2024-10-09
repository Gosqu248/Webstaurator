import {Component, Input, OnInit} from '@angular/core';
import {NgForOf, NgIf, NgOptimizedImage} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {MenuService} from "../../../services/menu.service";
import {Menu} from "../../../interfaces/menu";
import {MenuCategoryItemComponent} from "../menu-category-item/menu-category-item.component";
import {FilterByCategoryPipe} from "../../../pipes/filter-by-category.pipe";
import {RestaurantMenuItemComponent} from "../restaurant-menu-item/restaurant-menu-item.component";
import {OptionService} from "../../../services/option.service";
import {Router} from "@angular/router";
import {RatingUtil} from "../../../utils/rating-util";
import {Favourites} from "../../../interfaces/favourites";
import {FavouriteService} from "../../../services/favourite.service";

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
    NgOptimizedImage
  ],
  templateUrl: './restaurant-main.component.html',
  styleUrl: './restaurant-main.component.css'
})
export class RestaurantMainComponent implements OnInit{
  @Input() restaurant!: any;
  searchMenu: any;
  menu: Menu[] = [];
  categories: string[] = [];
  selectedCategory: string = '';
  filteredMenu: Menu[] = [];
  isAuthChecked: boolean = false;
  isFavorite: boolean =  false;
  userId: number = 0;
  loading: boolean = true;


  constructor(private languageService:LanguageService,
              private menuService: MenuService,
              private optionService: OptionService,
              private favouriteService: FavouriteService,
              private router: Router) {}

  ngOnInit() {
    this.checkIfFavourite();
  }

  toggleFavourite() {
    if (this.checkAuth()) {
        if (this.isFavorite) {
          this.favouriteService.deleteFavourite(this.userId, this.restaurant.id).subscribe({
            next: () => {
              this.isFavorite = false;
              console.log("Favourite deleted");
            },
            error: () => {
              console.log("Error deleting favourite");
            }
          });
        }  else {
          this.favouriteService.addFavourite(this.userId, this.restaurant.id).subscribe({
            next: () => {
              this.isFavorite = true;
              console.log("Favourite added");
            },
            error: () => {
              console.log("Error adding favourite");
            }
          });
        }
    } else {
      this.router.navigate([], {fragment: 'login'});
    }
  }

  checkAuth() {
    const token = localStorage.getItem('jwt');
    this.isAuthChecked = token != null;
    return this.isAuthChecked;
  }


  checkIfFavourite() {
    this.userId = parseInt(localStorage.getItem('userId') || '0', 10);
    if (this.userId !== 0) {
      this.favouriteService.getUserFavourites(this.userId).subscribe((favourites: Favourites[]) => {
        this.isFavorite = favourites.some((fav: Favourites) => fav.restaurantId === this.restaurant.id);
        this.loading = false;
        this.getMenu();
        this.getCategories();
        this.getSelected();
        this.filterMenu();
      });
    } else {
      this.loading = false;
    }
  }



  getMenu() {
    this.menuService.getMenuByRestaurantId(this.restaurant.id).subscribe(menu=> {
      this.menu = menu;
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
      this.filteredMenu = this.menu.filter(item =>
        item.name.toLowerCase().includes(this.searchMenu.toLowerCase()) &&
        (!this.selectedCategory || item.category === this.selectedCategory)
      );
    } else {
      this.filteredMenu = this.menu
    }
  }

  getAverageRating(): number {
    return RatingUtil.getAverageRating(this.restaurant.restaurantOpinions);
  }

  getRatingLength(): number {
    sessionStorage.setItem('ratingLength', this.restaurant.restaurantOpinions.length.toString());
    return this.restaurant.restaurantOpinions.length;
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }



}
