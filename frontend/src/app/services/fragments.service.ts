import { Injectable } from '@angular/core';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class FragmentsService {

  constructor(private router: Router) {}

  removeFragment() {
    this.router.navigate([], {
      fragment: undefined,
      queryParamsHandling: 'preserve'
    });
  }
}
