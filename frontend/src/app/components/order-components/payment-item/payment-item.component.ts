import {Component, EventEmitter, Input, Output} from '@angular/core';
import {PaymentMethod} from "../../../interfaces/paymentMethod";

@Component({
  selector: 'app-payment-item',
  standalone: true,
  imports: [],
  templateUrl: './payment-item.component.html',
  styleUrl: './payment-item.component.css'
})
export class PaymentItemComponent {
  @Input() payment!: PaymentMethod;
  @Output() selectPayment = new EventEmitter<PaymentMethod>();

  setPayment() {
    sessionStorage.setItem('payment', JSON.stringify(this.payment));
    this.selectPayment.emit(this.payment);
  }

}
