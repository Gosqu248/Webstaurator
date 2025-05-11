import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import {HomeFirstSectionComponent} from "../home-components/home-first-section/home-first-section.component";
import {HomeSecondSectionComponent} from "../home-components/home-second-section/home-second-section.component";
import {HomeThirdSectionComponent} from "../home-components/home-third-section/home-third-section.component";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    imports: [
        HomeFirstSectionComponent,
        HomeSecondSectionComponent,
        HomeThirdSectionComponent
    ],
    styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const error = params['error'];

      if (isPlatformBrowser(this.platformId)) {
        if (token) {
          localStorage.setItem('jwt', token);
          this.router.navigate([], {
            queryParams: {},
            replaceUrl: true
          });
        } else if (error) {
          console.error('Błąd logowania przez Google');
          this.router.navigate([], {
            queryParams: {},
            replaceUrl: true
          });
        }
      }
    });
  }
}
