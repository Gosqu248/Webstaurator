import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Payment} from "../../../interfaces/payment";

@Component({
  selector: 'app-payment-item',
  standalone: true,
  imports: [],
  templateUrl: './payment-item.component.html',
  styleUrl: './payment-item.component.css'
})
export class PaymentItemComponent {
  @Input() payment!: Payment;
  @Output() selectPayment = new EventEmitter<Payment>();

  setPayment() {
    sessionStorage.setItem('payment', JSON.stringify(this.payment));
    this.selectPayment.emit(this.payment);
  }

}
