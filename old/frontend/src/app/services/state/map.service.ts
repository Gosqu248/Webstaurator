import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MapService {
  private showMapSubject = new BehaviorSubject<boolean>(false);
  showMap$ = this.showMapSubject.asObservable();
  constructor() { }

  toggleMapView() {
    this.showMapSubject.next(!this.showMapSubject.value);
  }
}
