import {Component, Input, OnInit} from '@angular/core';
import {OrderDTO, OrderMenu} from "../../../interfaces/order";
import {DatePipe, NgClass, NgForOf, NgIf, registerLocaleData} from "@angular/common";
import localePl from '@angular/common/locales/pl';
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {OrderMenuItemComponent} from "../order-menu-item/order-menu-item.component";

@Component({
  selector: 'app-order-item',
  standalone: true,
  imports: [
    OrderMenuItemComponent,
    NgForOf,
    NgIf,
    NgClass
  ],
  templateUrl: './order-item.component.html',
  styleUrl: './order-item.component.css',
  providers: [DatePipe]
})
export class OrderItemComponent implements OnInit {
  @Input() order!: OrderDTO;
  isVisible = false;
  isDelivery = false;

  constructor(private datePipe: DatePipe,
              private languageService: LanguageService) {}

  ngOnInit() {
    registerLocaleData(localePl, 'pl-PL');
    this.getDelivery();
  }

  getFormattedDate(date: string): string | null {
    const language = this.languageService.getCurrentLanguage();

    const locale = language === 'pl' ? 'pl-PL' : 'en-US';
    return this.datePipe.transform(date, 'd MMMM yyyy', undefined, locale);
  }

  getFormattedTime(date: string): string | null {
    const language = this.languageService.getCurrentLanguage();

    const locale = language === 'pl' ? 'pl-PL' : 'en-US';
    return this.datePipe.transform(date, 'HH:mm', undefined, locale);
  }

  toggleVisibility() {
    this.isVisible = !this.isVisible;
  }

  getFormattedPrice(price: number): string {
    return price.toFixed(2);
  }

  getDeliveryOption() {
    return this.order.deliveryOption === 'dostawa' ? this.getTranslation('delivery') : this.getTranslation('pickup');
  }

  getPaymentMethod() {
    return this.order.paymentMethod === 'PayU' ? this.getTranslation('payU') : this.getTranslation('cash');
  }

  getDelivery() {
    this.order.deliveryOption === 'dostawa' ? this.isDelivery = true : this.isDelivery = false;
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
