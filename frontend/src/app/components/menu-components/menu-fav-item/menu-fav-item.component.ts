import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Favourites} from "../../../interfaces/favourites";
import {DecimalPipe} from "@angular/common";
import {FavouriteService} from "../../../services/favourite.service";
import {RestaurantOpinionService} from "../../../services/restaurant-opinion.service";

@Component({
  selector: 'app-menu-fav-item',
  standalone: true,
  imports: [
    DecimalPipe
  ],
  templateUrl: './menu-fav-item.component.html',
  styleUrl: './menu-fav-item.component.css'
})
export class MenuFavItemComponent implements OnInit{
  @Input() favourite!: Favourites;
  @Input() userId!: number;
  @Output() getFavourites = new EventEmitter<unknown>();
  averageRating: number = 0;
  ratingLength: number = 0;

  constructor(private favouriteService: FavouriteService, private restaurantOpinionService: RestaurantOpinionService) {}

  ngOnInit() {
    this.getAverageRating();
  }

  getAverageRating() {
    if (this.favourite.restaurantOpinion && this.favourite.restaurantOpinion.length > 0) {
       const restaurantId = sessionStorage.getItem('restaurantId');

       if(!restaurantId) {
         throw new Error('Restaurant ID not found in session storage');
       }

      this.restaurantOpinionService.getRating(parseFloat(restaurantId)).subscribe({
        next: (rating: number) => {
          this.averageRating =  rating;
        },
        error: (err) => {
          console.error('Error fetching average rating:', err);
        }
      });
      this.ratingLength = this.favourite.restaurantOpinion.length;
    } else {
      this.averageRating = 0;
      this.ratingLength = 0;
    }
  }

  removeFavourite() {
    this.favouriteService.deleteFavourite(this.userId, this.favourite.restaurantId).subscribe(() => {
      this.getFavourites.emit();
    });
  }
}
