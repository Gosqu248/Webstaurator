import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {catchError, map, Observable, throwError} from "rxjs";
import {Payment, PaymentResponse} from "../interfaces/payment";
import {Order} from "../interfaces/order";


@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = environment.api + '/api/payment';
  constructor(private http: HttpClient) {}

  getRestaurantPayments(id: number): Observable<Payment[]>{
    return this.http.get<Payment[]>(`${this.apiUrl}/getRestaurantPayments?restaurantId=${id}`);
  }

  getAll(): Observable<Payment[]>{
    return this.http.get<Payment[]>(`${this.apiUrl}/getPayments`);
  }

  createPayUPayment(order: Order): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.apiUrl}/createPayUPayment`, order);
  }

  createPayUOrder(): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.apiUrl}/createPayUOrder`, {});
  }

}
