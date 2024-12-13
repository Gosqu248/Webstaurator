import {Component, Input, OnInit} from '@angular/core';
import {DatePipe, NgForOf, NgIf, registerLocaleData} from "@angular/common";
import {OrderMenuItemComponent} from "../../../user-orders-components/order-menu-item/order-menu-item.component";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {AdminOrderDTO} from "../../../../interfaces/order";
import localePl from '@angular/common/locales/pl';
import {LanguageService} from "../../../../services/language.service";


@Component({
  selector: 'app-monitor-order-item',
  standalone: true,
    imports: [
        NgForOf,
        NgIf,
        OrderMenuItemComponent
    ],
  templateUrl: './monitor-order-item.component.html',
  styleUrl: './monitor-order-item.component.css',
  providers: [DatePipe]

})
export class MonitorOrderItemComponent implements OnInit{
  @Input() order!: AdminOrderDTO;
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

  getStatus() {
    return this.order.status === 'niezapłacone' ? this.getTranslation('notPaid') : this.getTranslation('paid');
  }

  getOrderTime() {
    return this.order.deliveryTime === "Jak najszybciej" ? this.getTranslation('asSoonAsPossible') : this.order.deliveryTime;
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
