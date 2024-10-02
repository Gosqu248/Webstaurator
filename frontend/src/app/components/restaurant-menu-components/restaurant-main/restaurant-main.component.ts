import {Component, Input, OnInit} from '@angular/core';
import {RestaurantOpinions} from "../../../interfaces/restaurant";
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {MenuService} from "../../../services/menu.service";
import {Menu} from "../../../interfaces/menu";
import {MenuCategoryItemComponent} from "../menu-category-item/menu-category-item.component";
import {FilterByCategoryPipe} from "../../../pipes/filter-by-category.pipe";
import {RestaurantMenuItemComponent} from "../restaurant-menu-item/restaurant-menu-item.component";
import {OptionService} from "../../../services/option.service";

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
    RestaurantMenuItemComponent
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

  constructor(private languageService:LanguageService, private menuService: MenuService, private optionService: OptionService) {}

  ngOnInit() {
    this.getMenu();
    this.getCategories();
    this.getSelected();
    this.filterMenu();
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
    console.log(this.categories);
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
    if (this.restaurant.restaurantOpinions.length === 0) {
      return 0;
    }
    const totalRating = this.restaurant.restaurantOpinions
      .map((opinion: RestaurantOpinions) => opinion.qualityRating + opinion.deliveryRating)
      .reduce((acc:number, rating:number) => acc + rating, 0);

    return totalRating / this.restaurant.restaurantOpinions.length / 2;
  }

  getRatingLength(): number {
    sessionStorage.setItem('ratingLength', this.restaurant.restaurantOpinions.length.toString());
    return this.restaurant.restaurantOpinions.length;
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }



}
