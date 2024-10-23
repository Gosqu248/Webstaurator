import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Payment} from "../interfaces/payment";

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

}
