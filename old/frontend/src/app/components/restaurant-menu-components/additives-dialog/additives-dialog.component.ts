import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Additives} from "../../../interfaces/menu";
import {DecimalPipe, NgClass, NgForOf, NgIf} from "@angular/common";
import { MatTabsModule } from '@angular/material/tabs';
import {AdditiveItemComponent} from "../additive-item/additive-item.component";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/state/language.service";
import {CartService} from "../../../services/state/cart.service";
import {OrderMenu} from "../../../interfaces/order";


@Component({
    selector: 'app-additives-dialog',
    imports: [
        DecimalPipe,
        MatTabsModule,
        NgIf,
        NgForOf,
        AdditiveItemComponent,
        NgClass
    ],
    templateUrl: './additives-dialog.component.html',
    styleUrl: './additives-dialog.component.css',
    styles: [`:host { width: 1200px; max-width: 100vw; }`]
})
export class AdditivesDialogComponent implements OnInit {
  quantity: number = 1;
  groupedAdditives: { [key: string]: Additives[] } = {};
  selectedAdditives: Additives[] = []; // Change to an array
  price: number = 0;
  productPrice: number = 0;
  additivesPrice: number = 0;
  protected readonly Object = Object;
  isButtonValid: boolean = false;
  orderMenu: OrderMenu = {} as OrderMenu;


  constructor(@Inject(MAT_DIALOG_DATA) public data: {orderMenu: OrderMenu},
              private cartService: CartService,
              private dialogRef: MatDialogRef<AdditivesDialogComponent>,
              private languageService: LanguageService) {}

  ngOnInit() {
    this.orderMenu = this.data.orderMenu;
    this.orderMenu.quantity ? this.quantity = this.orderMenu.quantity : null
    this.orderMenu.chooseAdditives ? this.selectedAdditives = this.orderMenu.chooseAdditives : null

    this.groupedAdditives = this.groupAdditivesByName(this.orderMenu.menu.additives || []);
    this.calculatePrice();

  }


  addToBasket() {
    if (this.isButtonValid) {
      this.cartService.removeProductFromCart(this.data.orderMenu);
      const orderMenu = { ...this.orderMenu };

      if (this.selectedAdditives.length > 0) {
        orderMenu.chooseAdditives = [...this.selectedAdditives];
      }

      this.cartService.addToCart(orderMenu, this.quantity);
      this.closeDialog();
    }

  }

  closeDialog() {
    this.dialogRef.close();
  }

  groupAdditivesByName(additives: Additives[]): { [key: string]: Additives[] } {
    return additives.reduce((acc, additive) => {
      if (!acc[additive.name]) {
        acc[additive.name] = [];
      }
      acc[additive.name].push(additive);
      return acc;
    }, {} as { [key: string]: Additives[] });
  }


  calculatePrice() {
    this.additivesPrice = this.cartService.calculateAdditivePrice(this.selectedAdditives);
    this.productPrice = this.data.orderMenu.menu.price + this.additivesPrice
    this.price = (this.data.orderMenu.menu.price+ this.additivesPrice) * this.quantity;
    this.isValid();

  }


  selectAdditive(additive: Additives) {
    const group = this.groupedAdditives[additive.name];
    this.selectedAdditives = this.selectedAdditives.filter(a => !group.includes(a));
    this.selectedAdditives.push(additive);
    this.isValid();
    this.calculatePrice();
  }

  isValid() {
    const grouped = Object.keys(this.groupedAdditives).length;
    const selectedGroups = new Set(this.selectedAdditives.map(a => a.name)).size;
    this.isButtonValid = grouped === selectedGroups;
  }


  isSelected(additive: Additives): boolean {
    return this.selectedAdditives.some(a => a.id === additive.id);
  }

  removeOne() {
    this.quantity > 1 ? this.quantity -= 1 : this.quantity = 1;
    this.calculatePrice();

  }

  addOne() {
    this.quantity += 1;
    this.calculatePrice();

  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

}
