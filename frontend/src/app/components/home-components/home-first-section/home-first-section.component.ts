import {Component, Input} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-home-first-section',
  standalone: true,
  imports: [],
  templateUrl: './home-first-section.component.html',
  styleUrl: './home-first-section.component.css'
})
export class HomeFirstSectionComponent {
  apiUrl = environment.api;
  background: string = '/img/background.webp';

  @Input() getTranslation!: <K extends keyof LanguageTranslations>(key: K) => LanguageTranslations[K];


}
