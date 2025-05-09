import {Component, Input} from '@angular/core';
import {OrderMenu} from "../../../interfaces/order";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/state/language.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-order-menu-item',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './order-menu-item.component.html',
  styleUrl: './order-menu-item.component.css'
})
export class OrderMenuItemComponent {
  @Input() menuItem!: OrderMenu;

  constructor(private languageService: LanguageService) {
  }

  getFormattedAdditives(): string {
    return this.menuItem.chooseAdditives.map(additive => additive.name + ": " + additive.value).join('<br>');
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
