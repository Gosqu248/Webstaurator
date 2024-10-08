import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {UserAddress} from "../interfaces/user.address.interface";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AddressesService {

  private apiUrl = environment.api + '/api/address';
  constructor(private http: HttpClient) {}

  addAddress(token: string, address: UserAddress): Observable<UserAddress> {
    const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
    return this.http.post<UserAddress>(`${this.apiUrl}/add`, address, {headers});
  }

  getUserAddresses(token: string): Observable<UserAddress[]> {
    const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
    return this.http.get<UserAddress[]>(`${this.apiUrl}/all`, {headers});
  }

  removeAddress(token: string, id: number): Observable<void> {
    const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {headers});
  }

  changeAddress(token: string, address: UserAddress): Observable<UserAddress> {
    const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
    return this.http.put<UserAddress>(`${this.apiUrl}/${address.id}`, address, {headers});
  }
}
