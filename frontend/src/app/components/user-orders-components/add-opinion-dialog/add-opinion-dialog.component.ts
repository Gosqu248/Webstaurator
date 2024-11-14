import {Component, Inject} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {NgClass, NgForOf} from "@angular/common";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {FormsModule} from "@angular/forms";
import {RestaurantOpinionService} from "../../../services/restaurant-opinion.service";
import {RestaurantOpinionDTO} from "../../../interfaces/restaurant-opinion";

@Component({
  selector: 'app-add-opinion-dialog',
  standalone: true,
  imports: [
    NgForOf,
    NgClass,
    FormsModule
  ],
  templateUrl: './add-opinion-dialog.component.html',
  styleUrl: './add-opinion-dialog.component.css'
})
export class AddOpinionDialogComponent {
  qualityRating: number = 0;
  deliveryRating: number = 0;
  qualityStars: boolean[] = Array(5).fill(false);
  deliveryStars: boolean[] = Array(5).fill(false);
  comment: any;

  constructor(private languageService: LanguageService,
              private opinionService: RestaurantOpinionService,
              @Inject(MAT_DIALOG_DATA) public data: {restaurantName: string, restaurantId: number}) {}


  addOpinion() {
    const opinion: RestaurantOpinionDTO = {
      qualityRating: this.qualityRating,
      deliveryRating: this.deliveryRating,
      comment: this.comment
    }
    const userId = localStorage.getItem('userId');

    if (!userId) {
      console.error('No user id found');
      return;
    }


    this.opinionService.addOpinion(opinion, this.data.restaurantId, +userId).subscribe({
        next: (opinion) => {
          console.log('Opinion added');
        },
        error: (error) => {
          console.error('Error adding ospinion:', error);
        }
      });
    }

  rateQuality(star: number) {
    this.qualityRating = star;
  }
  rateDelivery(star: number) {
    this.deliveryRating = star;
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

}
