import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormGroup, FormsModule} from "@angular/forms";
import {LanguageService} from "../../../../services/language.service";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {NgForOf} from "@angular/common";
import {Menu} from "../../../../interfaces/menu";
import {PaymentMethod} from "../../../../interfaces/paymentMethod";

@Component({
  selector: 'app-menu-form',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf
  ],
  templateUrl: './menu-form.component.html',
  styleUrl: './menu-form.component.css'
})
export class MenuFormComponent {
  @Output() menuItemsChange = new EventEmitter<Menu[]>();
  menuItems: Menu[] = [];

  constructor(private languageService: LanguageService) {}

  emitMenuItems() {
    this.menuItemsChange.emit(this.menuItems);
  }

  addMenuItem() {
    this.menuItems.push({
      category: '',
      name: '',
      image: '',
      ingredients: '',
      price: 0,
      additives: []
    });
  }

  removeMenuItem(index: number) {
    this.menuItems.splice(index, 1);
  }

  addAdditive(menuIndex: number) {
    const menuItem = this.menuItems[menuIndex];
    if (menuItem) {
      if (!menuItem.additives) {
        menuItem.additives = [];
      }
      menuItem.additives.push({
        name: '',
        price: 0,
        value: ''
      });
    } else {
      console.error(`Menu item at index ${menuIndex} does not exist.`);
    }
  }

  removeAdditive(menuIndex: number, additiveIndex: number) {
    const menuItem = this.menuItems[menuIndex];
    if (menuItem && menuItem.additives) {
      if (additiveIndex >= 0 && additiveIndex < menuItem.additives.length) {
        menuItem.additives.splice(additiveIndex, 1);
      } else {
        console.error(`Invalid additive index: ${additiveIndex} for menu item at index: ${menuIndex}`);
      }
    } else {
      console.error(`Menu item or additives at index ${menuIndex} does not exist.`);
    }
  }


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
