import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {catchError, map, Observable, throwError} from "rxjs";
import {Payment} from "../interfaces/payment";
import {Order} from "../interfaces/order";

interface PaymentResponse {
  redirectUrl: string;
}
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

  createPayment(order: any): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.apiUrl}/createPayment`, order);
  }
}
