import { Component, OnInit } from '@angular/core';
import { NgIf } from '@angular/common';
import { LanguageTranslations } from '../../../interfaces/language.interface';
import { LanguageService } from '../../../services/state/language.service';
import { PaymentMethodsService } from '../../../services/api/payment-methods.service';
import { MatDialog } from '@angular/material/dialog';
import { EditPaymentComponent } from '../edit-payment-dialog/edit-payment.component';
import { environment } from '../../../../environments/environment';
import {PaymentMethod} from "../../../interfaces/paymentMethod";

@Component({
  selector: 'app-order-payment',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './order-payment.component.html',
  styleUrls: ['./order-payment.component.css']
})
export class OrderPaymentComponent implements OnInit {
  payments: PaymentMethod[] = [];
  selectedPayment: PaymentMethod | null = null;

  constructor(
    private languageService: LanguageService,
    private dialog: MatDialog,
    private paymentService: PaymentMethodsService
  ) {}

  ngOnInit() {
    this.getPayments();
  }

  getPayments(): void {
    if (typeof sessionStorage !== 'undefined') {
      const restaurantId = sessionStorage.getItem('restaurantId');
      const id = restaurantId ? parseInt(restaurantId) : null;

      if (id) {
        this.paymentService.getRestaurantPaymentMethods(id).subscribe((data) => {
          this.payments = data.map((payment) => {
            payment.image = environment.api + payment.image;
            return payment;
          });

          if (!this.selectedPayment) {
            const payment = sessionStorage.getItem('payment');
            payment ? this.selectedPayment = JSON.parse(payment) : this.selectedPayment = this.payments[0];
          }
        });
      }
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
