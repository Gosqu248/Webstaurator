import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {LanguageService} from "../../../../services/state/language.service";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {NgForOf} from "@angular/common";
import {DeliveryHour} from "../../../../interfaces/delivery.interface";

@Component({
  selector: 'app-restaurant-delivery-hours-form',
  standalone: true,
  imports: [
    NgForOf,
    ReactiveFormsModule
  ],
  templateUrl: './restaurant-delivery-hours-form.component.html',
  styleUrl: './restaurant-delivery-hours-form.component.css'
})
export class RestaurantDeliveryHoursFormComponent {
  @Input() parentForm!: FormGroup;
  @Output() deliveryHoursChange = new EventEmitter<DeliveryHour[]>();

  daysOfWeek = [
    { name: this.getTranslation('monday'), openControl: 'mondayOpen', closeControl: 'mondayClose', dayOfWeek: 1 },
    { name: this.getTranslation('tuesday'), openControl: 'tuesdayOpen', closeControl: 'tuesdayClose', dayOfWeek: 2 },
    { name: this.getTranslation('wednesday'), openControl: 'wednesdayOpen', closeControl: 'wednesdayClose', dayOfWeek: 3 },
    { name: this.getTranslation('thursday'), openControl: 'thursdayOpen', closeControl: 'thursdayClose', dayOfWeek: 4 },
    { name: this.getTranslation('friday'), openControl: 'fridayOpen', closeControl: 'fridayClose', dayOfWeek: 5 },
    { name: this.getTranslation('saturday'), openControl: 'saturdayOpen', closeControl: 'saturdayClose', dayOfWeek: 6 },
    { name: this.getTranslation('sunday'), openControl: 'sundayOpen', closeControl: 'sundayClose', dayOfWeek: 0 }
  ];

  constructor(private languageService: LanguageService) {}


  emitDaysOfWeek() {
    const deliveryHours = this.daysOfWeek.map(day => ({
      dayOfWeek: day.dayOfWeek,
      openTime: this.parentForm.get(day.openControl)?.value,
      closeTime: this.parentForm.get(day.closeControl)?.value
    }));
    this.deliveryHoursChange.emit(deliveryHours);
  }


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
