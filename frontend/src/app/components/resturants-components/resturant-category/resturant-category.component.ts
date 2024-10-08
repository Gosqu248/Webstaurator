import {Component, Input, OnChanges, OnInit} from '@angular/core';
import { CategoryItemComponent } from '../category-item/category-item.component';
import {NgClass, NgForOf} from "@angular/common";
import {RestaurantsService} from "../../../services/restaurants.service";
import {OptionService} from "../../../services/option.service";

@Component({
  selector: 'app-resturant-category',
  standalone: true,
  imports: [
    CategoryItemComponent,
    NgForOf,
    NgClass
  ],
  templateUrl: './resturant-category.component.html',
  styleUrls: ['./resturant-category.component.css']
})
export class ResturantCategoryComponent implements OnInit, OnChanges{
  @Input() selectedOption!: string;
  selectedCategories:  string[] = [];
  deliveryCategories: string[] = [];
  pickupCategories: string[] = [];
  options: string[] = [];

  constructor(private restaurantService: RestaurantsService, private optionService: OptionService) {}

  ngOnInit() {
    this.loadCategories();
  }

  ngOnChanges() {
    this.updateCategories();
  }

  private loadCategories() {
    this.restaurantService.getDeliveryCategories().subscribe((categories: string[]) => {
      this.deliveryCategories = categories;
      this.updateCategories();

    });
    this.restaurantService.getPickupCategories().subscribe((categories: string[]) => {
      this.pickupCategories = categories;
      this.updateCategories();

    });
  }

  private updateCategories() {
    this.options = this.selectedOption === 'delivery' ? this.deliveryCategories : this.pickupCategories;
  }


  selectCategory(category: string) {
    const index = this.selectedCategories.indexOf(category);
    (index > -1) ? this.selectedCategories.splice(index, 1) : this.selectedCategories.push(category);
    this.optionService.setSelectedCategories(this.selectedCategories);
  }

}
