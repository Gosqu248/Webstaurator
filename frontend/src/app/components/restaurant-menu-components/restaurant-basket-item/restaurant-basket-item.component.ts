import {Component, Input, OnInit} from '@angular/core';
import {Menu} from "../../../interfaces/menu";
import {DecimalPipe, NgIf} from "@angular/common";
import {CartService} from "../../../services/cart.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {AdditivesDialogComponent} from "../additives-dialog/additives-dialog.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-restaurant-basket-item',
  standalone: true,
  imports: [
    DecimalPipe,
    NgIf
  ],
  templateUrl: './restaurant-basket-item.component.html',
  styleUrl: './restaurant-basket-item.component.css'
})
export class RestaurantBasketItemComponent implements OnInit{
  @Input() order!: Menu;
  orderPrice: number = 0;
  constructor(private cartService: CartService,
              private dialog: MatDialog,
              private languageService: LanguageService) {}

  ngOnInit() {
    this.setOrderPrice();
  }

  formatAdditives(): string {
   return this.cartService.formatAdditives(this.order.chooseAdditives || [])
  }

  addOne() {
    this.cartService.addToCart(this.order);
    this.setOrderPrice();
  }

  removeOne() {
    this.cartService.removeFromCart(this.order)
    this.setOrderPrice();
  }

  setOrderPrice() {
    const additivePrice = this.cartService.calculateAdditivePrice(this.order.chooseAdditives || []);

    this.orderPrice = (this.order.price + additivePrice) * (this.order.quantity || 1);
  }

  openDialogToChange(item: Menu) {
    this.dialog.open(AdditivesDialogComponent, {
        width: '1000px',
        maxWidth: '100vw',
        height: '600px',
        data: {item}
      });

  }

  getTranslate<k extends keyof LanguageTranslations>(key: k): string{
    return this.languageService.getTranslation(key)
  }

  hasChooseAdditives(): boolean {
    return !!this.order.chooseAdditives && this.order.chooseAdditives.length > 0;
  }


}
