import { Component, OnInit } from '@angular/core';
import { NgForOf, NgIf } from '@angular/common';
import { OrderDeliveryItemComponent } from '../order-delivery-item/order-delivery-item.component';
import { LanguageTranslations } from '../../../interfaces/language.interface';
import { LanguageService } from '../../../services/language.service';
import { Payment } from '../../../interfaces/payment';
import { PaymentService } from '../../../services/payment.service';
import { MatDialog } from '@angular/material/dialog';
import { EditPaymentComponent } from '../edit-payment-dialog/edit-payment.component';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-order-payment',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    OrderDeliveryItemComponent
  ],
  templateUrl: './order-payment.component.html',
  styleUrls: ['./order-payment.component.css']
})
export class OrderPaymentComponent implements OnInit {
  payments: Payment[] = [];
  selectedPayment: Payment | null = null;

  constructor(
    private languageService: LanguageService,
    private dialog: MatDialog,
    private paymentService: PaymentService
  ) {}

  ngOnInit() {
    this.getPayments();
  }

  getPayments(): void {
    const restaurantId = sessionStorage.getItem('restaurantId');
    const id = restaurantId ? parseInt(restaurantId) : null;

    if (id) {
      this.paymentService.getRestaurantPayments(id).subscribe((data) => {
        this.payments = data.map((payment) => {
          payment.image = environment.api + payment.image;
          return payment;
        });

        if(!this.selectedPayment) {
          const payment = sessionStorage.getItem('payment');
          payment ? this.selectedPayment = JSON.parse(payment) : this.selectedPayment = this.payments[0];
        }
      });
    }
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

  openEditDialog() {
    const dialogRef = this.dialog.open(EditPaymentComponent, {
      width: '600px',
      maxWidth: '100vw',
      data: { payments: this.payments, selectedPayment: this.selectedPayment }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.selectedPayment = result;
      }
    });
  }
}