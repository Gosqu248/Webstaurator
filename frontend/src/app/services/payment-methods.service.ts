import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {PaymentMethod} from "../interfaces/paymentMethod";


@Injectable({
  providedIn: 'root'
})
export class PaymentMethodsService {
  private apiUrl = environment.api + '/api/paymentMethods';
  constructor(private http: HttpClient) {}

  getRestaurantPaymentMethods(id: number): Observable<PaymentMethod[]>{
    return this.http.get<PaymentMethod[]>(`${this.apiUrl}/getRestaurantPayments?restaurantId=${id}`);
  }

  getPaymentMethods(): Observable<PaymentMethod[]>{
    return this.http.get<PaymentMethod[]>(`${this.apiUrl}/getAllPayments`);
  }



}
