import {Component, OnInit} from '@angular/core';
import {HomeFirstSectionComponent} from "../home-components/home-first-section/home-first-section.component";
import {HomeSecondSectionComponent} from "../home-components/home-second-section/home-second-section.component";
import {HomeThirdSectionComponent} from "../home-components/home-third-section/home-third-section.component";
import {ActivatedRoute, NavigationEnd, Router, RouterOutlet} from "@angular/router";
import {filter} from "rxjs";
import {NgClass, NgIf} from "@angular/common";
import {MenuComponent} from "../menu-components/menu/menu.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    HomeFirstSectionComponent,
    HomeSecondSectionComponent,
    HomeThirdSectionComponent,
    RouterOutlet,
    NgClass,
    MenuComponent,
    NgIf
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  constructor() {}


}
