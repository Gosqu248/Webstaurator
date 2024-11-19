import {Component, Input, OnChanges, OnInit} from '@angular/core';
import { CategoryItemComponent } from '../category-item/category-item.component';
import {NgClass, NgForOf} from "@angular/common";
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
  categories: string[] = [];
  constructor(private optionService: OptionService) {}

  ngOnInit() {
    this.loadCategories();
  }

  ngOnChanges() {
  }

  private loadCategories() {
    this.optionService.categories$.subscribe((categories) => {
      this.categories = categories;
    });
  }



  selectCategory(category: string) {
    const index = this.selectedCategories.indexOf(category);
    (index > -1) ? this.selectedCategories.splice(index, 1) : this.selectedCategories.push(category);
    this.optionService.setSelectedCategories(this.selectedCategories);
  }

}
