import {EventEmitter, Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {User, UserDTO} from "../interfaces/user.interface";
import {BehaviorSubject, catchError, map, Observable, of, tap, throwError} from "rxjs";
import {isPlatformBrowser} from "@angular/common";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.api + '/api/auth';

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.isAuthenticated());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  loginEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {}

  register(user: User): Observable<any> {
    return this.http.post<{message: string}>(`${this.apiUrl}/register`, user)
  }

  login(email: string, password: string): Observable<boolean> {
    return this.http.post<boolean>(`${this.apiUrl}/login`, {email, password}).pipe(
      map(response => {
        if (response) {
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('email', email);
          }
          return true;
        }
        return false;
      }),
      map(() => true),
      catchError( (error: HttpErrorResponse) => {
        if (error.status === 423) {
          return throwError(() => new Error('Account is locked. Try again later.'));
        }
        return of(false);
      })
    );
  }

  verify2FA(code: string): Observable<boolean> {
      const email = localStorage.getItem('email');
      return this.http.post<{jwt: string}>(`${this.apiUrl}/verify-2fa`, {email, code}).pipe(
        tap(response => {
          localStorage.setItem('jwt', response.jwt);
          this.isAuthenticatedSubject.next(true);
          this.loginEvent.emit();
        }),
        map(() => true),
        catchError(() => of(false))
      );
  }

  resetPassword(email: string): Observable<any> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/reset-password`, {email}).pipe(
      catchError(error => {
        if (error.status === 401) {
          return of({error: 'Email not found'});
        }
        return throwError(error);
      })
    )
  };



  getUser(token: string): Observable<UserDTO> {
    const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
    return this.http.get<UserDTO>(`${this.apiUrl}/user`, {headers});
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
