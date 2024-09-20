import { Injectable } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class FragmentsService {

  constructor(private router: Router, private route: ActivatedRoute) {}

  removeFragment() {
    const queryParams = this.route.snapshot.queryParams;
    this.router.navigate([], {
      queryParams: queryParams,
      fragment: undefined
    });
  }
}
