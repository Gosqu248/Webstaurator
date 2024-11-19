import {Component, OnInit} from '@angular/core';
import {
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { FormsModule } from "@angular/forms";
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { LanguageService } from "../../../services/language.service";
import { LanguageTranslations } from "../../../interfaces/language.interface";
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import {RestaurantOpinionService} from "../../../services/restaurant-opinion.service";
import {RestaurantOpinion} from "../../../interfaces/restaurant-opinion";
import {DecimalPipe, NgClass, NgForOf} from "@angular/common";
import {OpinionItemComponent} from "../opinion-item/opinion-item.component";
import {InfoComponent} from "../info/info.component";


@Component({
  selector: 'app-info-dialog',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatTabsModule,
    NgClass,
    NgForOf,
    DecimalPipe,
    OpinionItemComponent,
    InfoComponent
  ],
  templateUrl: './info-dialog.component.html',
  styleUrls: ['./info-dialog.component.css']
})
export class InfoDialogComponent implements OnInit{
  restaurantId: number = 0;
  opinions: RestaurantOpinion[] = [];
  opinionLength: number = 0;
  restaurantName: string = '';
  rating: number = 0;

  constructor(
    private languageService: LanguageService,
    private restaurantOpinionService: RestaurantOpinionService,
    public dialogRef: MatDialogRef<InfoDialogComponent>,
  ) {}

  ngOnInit() {
    this.getRestaurant();
    this.getRestaurantOpinions();
    this.getAverageRating();
  }

  getRestaurantOpinions() {
    this.restaurantOpinionService.getRestaurantOpinions(this.restaurantId).subscribe({
        next: (opinions: RestaurantOpinion[]) => {
          this.opinions = opinions;
          this.opinionLength = opinions.length;
        },
      error: (err) => {
        console.error('Error fetching restaurant opinions:', err);
      }
    });
  }

  getAverageRating() {
    this.restaurantOpinionService.getRating(this.restaurantId).subscribe({
      next: (rating: number) => {
        this.rating = rating
      },
      error: (err) => {
        console.error('Error fetching average rating:', err);
      }
    });
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  getRestaurant() {
    const id = sessionStorage.getItem('restaurantId');
    const name = sessionStorage.getItem('restaurantName');
    this.restaurantId = id ? parseInt(id) : 0;
    this.restaurantName = name ? name : '';
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
