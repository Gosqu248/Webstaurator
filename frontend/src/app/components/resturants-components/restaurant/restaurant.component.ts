import {Component} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {MenuComponent} from "../../menu-components/menu/menu.component";
import {NgIf} from "@angular/common";
import {ResturantCategoryComponent} from "../resturant-category/resturant-category.component";

@Component({
  selector: 'app-restaurant',
  standalone: true,
  imports: [
    RouterOutlet,
    MenuComponent,
    NgIf,
    ResturantCategoryComponent
  ],
  templateUrl: './restaurant.component.html',
  styleUrl: './restaurant.component.css'
})
export class RestaurantComponent {
  constructor() {}


}
