import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UserAddress } from '../../interfaces/user.address.interface';
import {BehaviorSubject, Observable} from 'rxjs';
import {Coordinates} from "../../interfaces/coordinates";

@Injectable({
  providedIn: 'root'
})
export class AddressesService {
  private apiUrl = environment.api + '/api/users/addresses';

  phoneNumber = new BehaviorSubject<string>('')
  phoneNumber$ = this.phoneNumber.asObservable();

  constructor(private http: HttpClient) {}

  getUserAddresses(token: string): Observable<UserAddress[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<UserAddress[]>(`${this.apiUrl}`, { headers });
  }

  addAddress(token: string, address: UserAddress): Observable<UserAddress> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post<UserAddress>(`${this.apiUrl}`, address, { headers });
  }

  getAddress(token: string, id: number): Observable<UserAddress> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<UserAddress>(`${this.apiUrl}/${id}`, { headers });
  }

  removeAddress(token: string, id: number): Observable<void> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
  }

  changeAddress(token: string, address: UserAddress): Observable<UserAddress> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put<UserAddress>(`${this.apiUrl}/${address.id}`, address, { headers });
  }

  getAvailableAddresses(token: string, coordinates: Coordinates): Observable<UserAddress[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<UserAddress[]>(`${this.apiUrl}/available?lat=${coordinates.lat}&lon=${coordinates.lon}`,{ headers });
  }

  setPhoneNumber(phoneNumber: string): void {
    this.phoneNumber.next(phoneNumber);
  }
}
