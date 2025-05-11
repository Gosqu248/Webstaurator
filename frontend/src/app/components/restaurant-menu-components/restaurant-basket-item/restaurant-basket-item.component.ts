import {Component, Input, OnInit} from '@angular/core';
import {DecimalPipe, NgIf} from "@angular/common";
import {CartService} from "../../../services/state/cart.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/state/language.service";
import {AdditivesDialogComponent} from "../additives-dialog/additives-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {OrderMenu} from "../../../interfaces/order";

@Component({
    selector: 'app-restaurant-basket-item',
    imports: [
        DecimalPipe,
        NgIf
    ],
    templateUrl: './restaurant-basket-item.component.html',
    styleUrl: './restaurant-basket-item.component.css'
})
export class RestaurantBasketItemComponent implements OnInit{
  @Input() orderMenu!: OrderMenu;
  orderPrice: number = 0;
  constructor(private cartService: CartService,
              private dialog: MatDialog,
              private languageService: LanguageService) {}

  ngOnInit() {
    this.setOrderPrice();
  }

  formatAdditives(): string {
   return this.cartService.formatAdditives(this.orderMenu.chooseAdditives || [])
  }

  addOne() {
    this.cartService.addToCart(this.orderMenu);
    this.setOrderPrice();
  }

  removeOne() {
    this.cartService.removeFromCart(this.orderMenu)
    this.setOrderPrice();
  }

  setOrderPrice() {
    const additivePrice = this.cartService.calculateAdditivePrice(this.orderMenu.chooseAdditives || []);

    this.orderPrice = (this.orderMenu.menu.price + additivePrice) * (this.orderMenu.quantity || 1);
  }

  openDialogToChange(orderMenu: OrderMenu) {
    this.dialog.open(AdditivesDialogComponent, {
        width: '1000px',
        maxWidth: '100vw',
        height: '600px',
        data: {orderMenu}
      });

  }

  getTranslate<k extends keyof LanguageTranslations>(key: k): string{
    return this.languageService.getTranslation(key)
  }

  hasChooseAdditives(): boolean {
    return !!this.orderMenu.chooseAdditives && this.orderMenu.chooseAdditives.length > 0;
  }


}
