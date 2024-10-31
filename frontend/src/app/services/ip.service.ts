import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class IpService {
  private apiUrl = environment.api + '/api/ip';

  constructor(private http:HttpClient) {}

  getClientIp(): Observable<string> {
    return this.http.get(`${this.apiUrl}/get`, {responseType: 'text'});
  }

}
