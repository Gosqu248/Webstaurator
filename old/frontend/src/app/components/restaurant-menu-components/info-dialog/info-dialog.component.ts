import {Component, OnInit} from '@angular/core';
import {
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { FormsModule } from "@angular/forms";
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { LanguageService } from "../../../services/state/language.service";
import { LanguageTranslations } from "../../../interfaces/language.interface";
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import {RestaurantOpinionService} from "../../../services/api/restaurant-opinion.service";
import {RestaurantOpinion} from "../../../interfaces/restaurant-opinion";
import {DecimalPipe, NgClass, NgForOf} from "@angular/common";
import {OpinionItemComponent} from "../opinion-item/opinion-item.component";
import {InfoComponent} from "../info/info.component";
import {SearchedRestaurantsService} from "../../../services/state/searched-restaurant.service";
import {SearchedRestaurant} from "../../../interfaces/searched-restaurant";


@Component({
    selector: 'app-info-dialog',
    imports: [
        MatDialogTitle,
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
  opinions: RestaurantOpinion[] = [];
  opinionLength: number = 0;
  restaurantName: string = '';
  rating: number = 0;
  searchedRestaurant: SearchedRestaurant = {} as SearchedRestaurant;

  constructor(
    private languageService: LanguageService,
    private searchedRestaurantsService: SearchedRestaurantsService,
    private restaurantOpinionService: RestaurantOpinionService,
    public dialogRef: MatDialogRef<InfoDialogComponent>,
  ) {}

  ngOnInit() {
    this.getRestaurant();
    this.getRestaurantOpinions();
    this.getAverageRating();
  }

  getRestaurant(): void {
    this.searchedRestaurantsService.selectedRestaurant$.subscribe(restaurant => {
      this.searchedRestaurant = restaurant;
    });
  }

  getRestaurantOpinions() {
    this.restaurantOpinionService.getRestaurantOpinions(this.searchedRestaurant.restaurantId).subscribe({
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
    this.restaurantOpinionService.getRating(this.searchedRestaurant.restaurantId).subscribe({
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

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
