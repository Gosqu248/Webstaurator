import { Component, Inject, OnInit } from '@angular/core';
import { DeliveryHour } from "../../../interfaces/delivery.interface";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import {NgForOf, NgIf} from "@angular/common";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {OptionService} from "../../../services/option.service";

@Component({
  selector: 'app-choose-hour-dialog',
  standalone: true,
  imports: [
    NgForOf,
    NgIf
  ],
  templateUrl: './choose-hour-dialog.component.html',
  styleUrl: './choose-hour-dialog.component.css'
})
export class ChooseHourDialogComponent implements OnInit {
  selectedHour: string | null = null;
  availableHours: string[] = [];
  deliveryOption: string = '';

  constructor(private languageService: LanguageService,
              private optionService: OptionService,
              public dialogRef: MatDialogRef<ChooseHourDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { maxTime: number, deliveryTime: DeliveryHour[] }
              ) {}

  ngOnInit() {
    this.generateAvailableHours();
    this.getDeliveryOption();
  }

  generateAvailableHours() {
    const currentTime = new Date();
    const currentMinutes = currentTime.getMinutes();

    currentTime.setMinutes(currentMinutes + this.data.maxTime);

    const minutes = currentTime.getMinutes();
    const remainder = minutes % 15;
    if (remainder !== 0) {
      currentTime.setMinutes(minutes + (15 - remainder));
    }

    const todayDeliveryHour = this.data.deliveryTime.find((d: DeliveryHour) => d.dayOfWeek === currentTime.getDay());
    if (todayDeliveryHour) {
      const closingTime = todayDeliveryHour.closeTime.split(':');
      const closingHour = parseInt(closingTime[0]);
      const closingMinutes = parseInt(closingTime[1]);

      while (currentTime.getHours() < closingHour || (currentTime.getHours() === closingHour && currentTime.getMinutes() <= closingMinutes)) {
        this.availableHours.push(currentTime.toTimeString().substring(0, 5));
        currentTime.setMinutes(currentTime.getMinutes() + 15);
      }
    }
  }

  selectHour(hour: string) {
    this.selectedHour = hour;
  }

  getDeliveryOption() {
    this.deliveryOption = this.optionService.selectBasketDelivery.value;
  }

  confirmSelection() {
    this.dialogRef.close(this.selectedHour);
  }

  cancel() {
    this.dialogRef.close();
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
