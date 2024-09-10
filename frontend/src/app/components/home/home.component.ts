import {Component, OnInit} from '@angular/core';
import {HomeFirstSectionComponent} from "../home-components/home-first-section/home-first-section.component";
import {HomeSecondSectionComponent} from "../home-components/home-second-section/home-second-section.component";
import {HomeThirdSectionComponent} from "../home-components/home-third-section/home-third-section.component";
import {NavigationEnd, Router, RouterOutlet} from "@angular/router";
import {filter} from "rxjs";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    HomeFirstSectionComponent,
    HomeSecondSectionComponent,
    HomeThirdSectionComponent,
    RouterOutlet,
    NgClass
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{
  isDimmed: boolean = false;

  constructor(private router: Router) {}

  ngOnInit() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.isDimmed = event.url != '/';
      }
    )
  }

}
