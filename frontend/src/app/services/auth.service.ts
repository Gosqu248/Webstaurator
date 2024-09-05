import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {User} from "../interfaces/user.interface";
import {catchError, map, Observable, of, tap} from "rxjs";
import {response} from "express";
import {isPlatformBrowser} from "@angular/common";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = environment.api + '/auth';

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {}

  register(user: User): Observable<any> {
    return this.http.post<{message: string}>(`${this.apiUrl}/register`, user)
  }

  login(email: string, password: string): Observable<boolean> {
    return this.http.post<{jwt: string}>(`${this.apiUrl}/login`, {email, password}).pipe(
      tap(response => {
        if (isPlatformBrowser(this.platformId)) {
          sessionStorage.setItem('jwt', response.jwt);
        }
      }),
      map(() => true),
      catchError( error => {
        console.log('Login error: ', error);
        return of(false);
      })
    );
  }

}
