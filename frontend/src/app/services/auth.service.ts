import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {User} from "../interfaces/user.interface";
import {BehaviorSubject, catchError, map, Observable, of, tap} from "rxjs";
import {isPlatformBrowser} from "@angular/common";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.api + '/api/auth';

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.isAuthenticated());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {}

  register(user: User): Observable<any> {
    return this.http.post<{message: string}>(`${this.apiUrl}/register`, user)
  }

  login(email: string, password: string): Observable<boolean> {
    return this.http.post<{jwt: string}>(`${this.apiUrl}/login`, {email, password}).pipe(
      tap(response => {
        if (isPlatformBrowser(this.platformId)) {
          if (response.jwt) {
            this.isAuthenticatedSubject.next(true);
            localStorage.setItem('jwt', response.jwt);
          } else {
            console.error('No JWT token in response: ', response)
          }
        }
      }),
      map(() => true),
      catchError( error => {
        console.log('Login error: ', error);
        return of(false);
      })
    );
  }

  getUser(token: string): Observable<User> {
    const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
    return this.http.get<User>(`${this.apiUrl}/user`, {headers});
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('jwt');
      localStorage.removeItem('name');
      localStorage.removeItem('email');

    }
    this.isAuthenticatedSubject.next(false);

  }

  changeUserName(token: string, name: string): Observable<string> {
    const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
    return this.http.put(`${this.apiUrl}/changeName`, name, {headers, responseType: 'text'});
  }

  changePassword(token: string, password: string, newPassword: string): Observable<boolean> {
    const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
    return this.http.put<boolean>(`${this.apiUrl}/changePassword`, {password, newPassword}, {headers});
  }

  isAuthenticated(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return !!localStorage.getItem('jwt');
    } else {
      return false;
    }
  }
}
