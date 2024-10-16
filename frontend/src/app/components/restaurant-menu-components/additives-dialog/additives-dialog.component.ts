import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Additives, Menu} from "../../../interfaces/menu";
import {DecimalPipe, NgClass, NgForOf, NgIf} from "@angular/common";
import { MatTabsModule } from '@angular/material/tabs';
import {InfoComponent} from "../info/info.component";
import {AdditiveItemComponent} from "../additive-item/additive-item.component";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {CartService} from "../../../services/cart.service";


@Component({
  selector: 'app-additives-dialog',
  standalone: true,
  imports: [
    DecimalPipe,
    MatTabsModule,
    NgIf,
    InfoComponent,
    NgForOf,
    AdditiveItemComponent,
    NgClass
  ],
  templateUrl: './additives-dialog.component.html',
  styleUrl: './additives-dialog.component.css',
  styles: [`:host { width: 1200px; max-width: 100vw; }`],
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
  order: Menu = {} as Menu;


  constructor(@Inject(MAT_DIALOG_DATA) public data: {item: Menu},
              private cartService: CartService,
              private dialogRef: MatDialogRef<AdditivesDialogComponent>,
              private languageService: LanguageService) {}

  ngOnInit() {
    this.order = this.data.item;
    this.data.item.quantity ? this.quantity = this.data.item.quantity : null
    this.data.item.chooseAdditives ? this.selectedAdditives = this.data.item.chooseAdditives : null

    this.groupedAdditives = this.groupAdditivesByName(this.order.additives || []);
    this.calculatePrice();

  }


  addToBasket() {
    if (this.isButtonValid) {
      this.cartService.removeProductFromCart(this.data.item);
      const menu = { ...this.order };

      if (this.selectedAdditives.length > 0) {
        menu.chooseAdditives = [...this.selectedAdditives];
      }

      this.cartService.addToCart(menu, this.quantity);
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
    this.productPrice = this.data.item.price+ this.additivesPrice
    this.price = (this.data.item.price+ this.additivesPrice) * this.quantity;
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
