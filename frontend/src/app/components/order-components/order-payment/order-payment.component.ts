import {Component, Input, OnInit} from '@angular/core';
import { NgIf } from '@angular/common';
import { LanguageTranslations } from '../../../interfaces/language.interface';
import { LanguageService } from '../../../services/state/language.service';
import { MatDialog } from '@angular/material/dialog';
import { EditPaymentComponent } from '../edit-payment-dialog/edit-payment.component';
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
export class OrderPaymentComponent  implements OnInit {
  @Input() paymentMethods!: PaymentMethod[] | undefined;
  selectedPayment: PaymentMethod | undefined = undefined;

  constructor(
    private languageService: LanguageService,
    private dialog: MatDialog,
  ) {}

  ngOnInit() {
    this.selectedPayment = this.paymentMethods?.[0];
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

  openEditDialog() {
    const dialogRef = this.dialog.open(EditPaymentComponent, {
      width: '600px',
      maxWidth: '100vw',
      data: { payments: this.paymentMethods, selectedPayment: this.selectedPayment }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.selectedPayment = result;
      }
    });
  }
}
