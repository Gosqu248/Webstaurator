import { Component } from '@angular/core';
import {LanguageService} from "../../services/language.service";
import {LanguageTranslations} from "../../interfaces/language.interface";
import {environment} from "../../../environments/environment";
import {HomeFirstSectionComponent} from "../home-components/home-first-section/home-first-section.component";
import {HomeSecondSectionComponent} from "../home-components/home-second-section/home-second-section.component";
import {HomeThirdSectionComponent} from "../home-components/home-third-section/home-third-section.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    HomeFirstSectionComponent,
    HomeSecondSectionComponent,
    HomeThirdSectionComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {


}
