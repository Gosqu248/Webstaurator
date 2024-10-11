import { Component, Input, OnInit } from '@angular/core';
import { RestaurantOpinion } from '../../../interfaces/restaurant-opinion';
import {DatePipe, NgClass, NgForOf} from '@angular/common';
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-opinion-item',
  standalone: true,
  imports: [
    NgClass,
    MatIcon,
    NgForOf
  ],
  templateUrl: './opinion-item.component.html',
  styleUrls: ['./opinion-item.component.css'],
  providers: [DatePipe]
})
export class OpinionItemComponent implements OnInit {
  @Input() opinion!: RestaurantOpinion;
  formattedDate: string = '';

  constructor(private datePipe: DatePipe,
              private languageService: LanguageService,
              ) {}

  ngOnInit(): void {
    const formattedDate = this.datePipe.transform(this.opinion.createdAt, 'dd.MM.yyyy');
    this.formattedDate = formattedDate ? formattedDate : '';
  }

  getTranslation<k extends keyof LanguageTranslations>(k: k): string {
    return this.languageService.getTranslation(k);
  }
}
