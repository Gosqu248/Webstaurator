import { Injectable } from '@angular/core';
import {Order, OrderRequest} from "../../interfaces/order";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {PaymentResponse} from "../../interfaces/paymentMethod";

@Injectable({
  providedIn: 'root'
})
export class PayUService {
  constructor(private http: HttpClient) { }

  private apiUrl = environment.api + '/api/payU';
  createPayUPayment(order: OrderRequest): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.apiUrl}/createPayment`, order);
  }

  getPaymentStatus(orderId: string): Observable<string> {
    return this.http.get(`${this.apiUrl}/getPaymentStatus?orderId=${orderId}`, { responseType: 'text' });
  }
}
