import {Component, Input} from '@angular/core';
import {DecimalPipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {Menu} from "../../../interfaces/menu";
import {FilterByCategoryPipe} from "../../../pipes/filter-by-category.pipe";
import {CartService} from "../../../services/state/cart.service";
import {MatDialog} from "@angular/material/dialog";
import {AdditivesDialogComponent} from "../additives-dialog/additives-dialog.component";
import {OrderMenu} from "../../../interfaces/order";

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
  constructor(private cartService: CartService,
              private dialog: MatDialog ) {}

  addToCart(menu: Menu) {
    const orderMenu: OrderMenu = {
      menu: menu,
      chooseAdditives: [],
      quantity: 1
    };

    if (menu.additives?.length === 0) {
      this.cartService.addToCart(orderMenu);
    } else {
      const dialogRef = this.dialog.open(AdditivesDialogComponent, {
        width: '1000px',
        maxWidth: '100vw',
        height: '600px',
        data: {orderMenu: orderMenu}
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          orderMenu.chooseAdditives = result.chooseAdditives;
          this.cartService.addToCart(orderMenu);
        }
        console.log('Dialog was closed');
      });
    }
  }

}
