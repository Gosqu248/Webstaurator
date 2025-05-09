import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {OrderDTO} from "../../../interfaces/order";
import {DatePipe, NgForOf, NgIf, registerLocaleData} from "@angular/common";
import localePl from '@angular/common/locales/pl';
import {LanguageService} from "../../../services/state/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {OrderMenuItemComponent} from "../order-menu-item/order-menu-item.component";
import {MatDialog} from "@angular/material/dialog";
import {AddOpinionDialogComponent} from "../add-opinion-dialog/add-opinion-dialog.component";

@Component({
  selector: 'app-order-item',
  standalone: true,
  imports: [
    OrderMenuItemComponent,
    NgForOf,
    NgIf
  ],
  templateUrl: './order-item.component.html',
  styleUrl: './order-item.component.css',
  providers: [DatePipe]
})
export class OrderItemComponent implements OnInit {
  @Input() order!: OrderDTO;
  @Output() opinionAdded = new EventEmitter<void>();


  isVisible = false;
  isDelivery = false;

  constructor(private datePipe: DatePipe,
              private dialog: MatDialog,
              private languageService: LanguageService) {}

  ngOnInit() {
    registerLocaleData(localePl, 'pl-PL');
    this.getDelivery();
  }

  openAddOpinionDialog() {
    const dialogRef = this.dialog.open(AddOpinionDialogComponent, {
      maxWidth: '100%',
      width: '600px',
      height: '620px',
      data: { restaurantName: this.order.restaurantName, orderId: this.order.id }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'opinionAdded') {
        this.opinionAdded.emit();
      }
    });
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
