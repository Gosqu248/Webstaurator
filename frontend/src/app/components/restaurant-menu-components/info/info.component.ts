import {Component, Input, OnInit} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import { LanguageService } from '../../../services/language.service';
import {DeliveryService} from "../../../services/delivery.service";
import {DeliveryHour} from "../../../interfaces/delivery.interface";
import {DecimalPipe, NgForOf} from "@angular/common";

@Component({
  selector: 'app-info',
  standalone: true,
  imports: [
    NgForOf,
    DecimalPipe
  ],
  templateUrl: './info.component.html',
  styleUrl: './info.component.css'
})
export class InfoComponent implements OnInit{
  @Input() restaurantId!: number;
  deliveryTime: DeliveryHour[] = [];
  deliveryPrice: number = 0;
  minimumPrice: number = 0;

  constructor(private languageService: LanguageService,
              private deliveryService: DeliveryService) {}

  ngOnInit() {
    this.getDeliveryTime();
    this.getDeliveryInfo();
  }

  getDeliveryTime(): void {
    this.deliveryService.getDeliveryTIme(this.restaurantId).subscribe((data) => {
      this.deliveryTime = data;
      console.log(this.deliveryTime);
    });
  }

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

  getDeliveryInfo() {
    const price = sessionStorage.getItem("deliveryPrice");
    this.deliveryPrice = price ? +price : 0;
    const minPrice = sessionStorage.getItem("minPrice");
    this.minimumPrice = minPrice ? +minPrice : 0;

  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}