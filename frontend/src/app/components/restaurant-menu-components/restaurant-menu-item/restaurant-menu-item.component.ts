import {Component, Input} from '@angular/core';
import {DecimalPipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {Menu} from "../../../interfaces/menu";
import {FilterByCategoryPipe} from "../../../pipes/filter-by-category.pipe";
import {OptionService} from "../../../services/option.service";
import {CartService} from "../../../services/cart.service";

@Component({
  selector: 'app-restaurant-menu-item',
  standalone: true,
  imports: [
    NgForOf,
    FilterByCategoryPipe,
    NgIf,
    DecimalPipe,
    NgClass
  ],
  templateUrl: './restaurant-menu-item.component.html',
  styleUrl: './restaurant-menu-item.component.css'
})
export class RestaurantMenuItemComponent {
  @Input() menu!: Menu[];
  @Input() category!: string;
  constructor(private cartService: CartService) {}

  addToCart(item: Menu) {
    this.cartService.addToCart(item);
  }

}
