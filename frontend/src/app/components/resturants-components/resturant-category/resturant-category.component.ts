import { Component } from '@angular/core';
import { CategoryItemComponent } from '../category-item/category-item.component';
import {NgClass, NgForOf} from "@angular/common";

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
export class ResturantCategoryComponent {
  url: string = 'http://localhost:8080/img/';
  selectedCategories: any[] = [];

  categories: { name: string, image: string }[] = [
    { name: 'Amerykańska', image: this.url + 'american.jpg' },
    { name: 'Polska', image: this.url + 'polish.jpg' },
    { name: 'Kebaby', image: this.url + 'kebab.jpg' },
    { name: 'Burgery', image: this.url + 'burger.jpg' },
    { name: 'Pizza', image: this.url + 'pizza.jpg' },
    { name: 'Sushi', image: this.url + 'sushi.jpg' },
    { name: 'Włoska', image: this.url + 'italian.jpg' },

  ];

  selectCategory(category: any) {
    const index = this.selectedCategories.indexOf(category);
    if (index > -1) {
      this.selectedCategories.splice(index, 1);
    } else {
      this.selectedCategories.push(category);
    }
  }
}
