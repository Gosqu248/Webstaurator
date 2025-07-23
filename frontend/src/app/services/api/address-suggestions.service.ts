import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {Observable} from "rxjs";
import {Suggest} from "../../interfaces/suggest";
import {Coordinates} from "../../interfaces/coordinates";

@Injectable({
  providedIn: 'root'
})
export class AddressSuggestionsService {
  private apiUrl = environment.api + '/api/suggestions';

  constructor(private http: HttpClient) { }

  getSuggestions(partialName: string): Observable<Suggest[]> {
    return this.http.get<Suggest[]>(`${this.apiUrl}?partialName=${partialName}`);
  }

  getCoordinates(address: string): Observable<Coordinates> {
    return this.http.get<Coordinates>(`${this.apiUrl}/coordinates?address=${address}`);
  }
}
