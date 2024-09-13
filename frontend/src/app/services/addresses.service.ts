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
}
