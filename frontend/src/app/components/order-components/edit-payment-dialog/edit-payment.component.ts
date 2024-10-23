import {Component, Inject} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {PaymentItemComponent} from "../payment-item/payment-item.component";
import {NgForOf} from "@angular/common";
import {Payment} from "../../../interfaces/payment";


@Component({
  selector: 'app-edit-payment',
  standalone: true,
  imports: [
    PaymentItemComponent,
    NgForOf
  ],
  templateUrl: './edit-payment.component.html',
  styleUrl: './edit-payment.component.css'
})
export class EditPaymentComponent {
  payments: Payment[] = [];
  selectedPayment: Payment | null = null;
  constructor(private languageService: LanguageService,
              public dialogRef: MatDialogRef<EditPaymentComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { payments: Payment[], selectedPayment: Payment }
              ) {
    this.payments = data.payments;
    this.selectedPayment = data.selectedPayment;
  }

  changeSelectedPayment(payment: Payment) {
    this.selectedPayment = payment;
    this.closeDialog();
  }



  closeDialog() {
    this.dialogRef.close(this.selectedPayment);
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
