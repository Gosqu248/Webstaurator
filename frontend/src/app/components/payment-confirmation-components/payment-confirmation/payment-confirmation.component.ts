import {Component, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";
import {PayUService} from "../../../services/pay-u.service";
import {environment} from "../../../../environments/environment";
import {OrderService} from "../../../services/order.service";
import {OrderRequest, OrderStatus} from "../../../interfaces/order";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {Router, RouterLink} from "@angular/router";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@Component({
  selector: 'app-payment-confirmation',
  standalone: true,
  imports: [
    NgIf,
    RouterLink,
    MatProgressSpinner
  ],
  templateUrl: './payment-confirmation.component.html',
  styleUrl: './payment-confirmation.component.css'
})
export class PaymentConfirmationComponent implements OnInit{
  paymentId: string = ' ';
  status: string = '';
  background = environment.api + '/img/Pay-conf-back.webp';
  order: OrderRequest = {} as OrderRequest;
  isLoading: boolean = true;

  constructor(private payUService: PayUService,
              private languageService: LanguageService,
              private router: Router,
              private orderService: OrderService) {}

  ngOnInit() {
    this.resetState();
    if (typeof window !== 'undefined' && window.localStorage) {

      const id = localStorage.getItem('payUPaymentId');


      if (id) {
        this.paymentId = id;

        setTimeout(() => {
          this.getStatus(this.paymentId);
        }, 800);
      }
    }
  }

  getStatus(paymentId: string) {
    this.payUService.getPaymentStatus(paymentId).subscribe({
      next: (status: string) => {
        this.isLoading = false;

        if (status === 'COMPLETED') {
          this.status = status

          const order = localStorage.getItem('paymentOrder');
          if (order) {
            this.order = JSON.parse(order);
            this.order.paymentId = paymentId;
            this.order.status = OrderStatus.zaplacone;

            this.orderService.createOrder(this.order);
            localStorage.removeItem("paymentOrder")
          } else {
            console.error('Brak zamówienia w localStorage');
          }

        } else {
          this.status = status;

          console.log('Płatność zakończona niepowodzeniem');
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Błąd podczas sprawdzania statusu płatności:', error);
      }
    });
  }

  goToOrders() {
    this.removeLocalStorage();
    this.router.navigate(['/orders-history']);
  }

  backToCheckout() {
    this.removeLocalStorage();
    this.router.navigate(['/checkout']);
  }

  removeLocalStorage() {
    localStorage.removeItem('payUPaymentId');
    localStorage.removeItem('paymentOrder');
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

  resetState() {
    this.paymentId = '';
    this.status = '';
    this.order = {} as OrderRequest;
  }
}
