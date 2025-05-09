import { Component, Input, OnInit } from '@angular/core';
import { LanguageTranslations } from "../../../interfaces/language.interface";
import { DecimalPipe, NgForOf, NgIf } from "@angular/common";
import { InfoMapComponent } from "../info-map/info-map.component";
import { SearchedRestaurant } from "../../../interfaces/searched-restaurant";
import {LanguageService} from "../../../services/state/language.service";

@Component({
  selector: 'app-info',
  standalone: true,
  imports: [
    NgForOf,
    DecimalPipe,
    InfoMapComponent,
    NgIf
  ],
  templateUrl: './info.component.html',
  styleUrl: './info.component.css'
})
export class InfoComponent {
  @Input() restaurantName!: string;
  @Input() searchedRestaurant!: SearchedRestaurant;

  constructor(
    private languageService: LanguageService,
  ) {}

  getDayName(dayOfWeek: number): string {
    const days = [
      this.getTranslation('sunday'),
      this.getTranslation('monday'),
      this.getTranslation('tuesday'),
      this.getTranslation('wednesday'),
      this.getTranslation('thursday'),
      this.getTranslation('friday'),
      this.getTranslation('saturday')
    ];
    return days[dayOfWeek];
  }


  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
