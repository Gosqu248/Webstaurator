import {Component, Input} from '@angular/core';
import {DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {Menu} from "../../../interfaces/menu";
import {FilterByCategoryPipe} from "../../../pipes/filter-by-category.pipe";

@Component({
  selector: 'app-restaurant-menu-item',
  standalone: true,
  imports: [
    NgForOf,
    FilterByCategoryPipe,
    NgIf,
    DecimalPipe
  ],
  templateUrl: './restaurant-menu-item.component.html',
  styleUrl: './restaurant-menu-item.component.css'
})
export class RestaurantMenuItemComponent {
  @Input() menu!: Menu[];
  @Input() category!: string;
  constructor() {
  }

}
