import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Favourites} from "../../../interfaces/favourites";
import {DecimalPipe} from "@angular/common";
import {RatingUtil} from "../../../utils/rating-util";
import {FavouriteService} from "../../../services/favourite.service";
import {OptionService} from "../../../services/option.service";

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

  constructor(private favouriteService: FavouriteService, private optionService: OptionService) {}

  ngOnInit() {
    this.getAverageRating();
  }

  getAverageRating() {
    if (this.favourite.restaurantOpinion && this.favourite.restaurantOpinion.length > 0) {
      this.averageRating = RatingUtil.getAverageRating(this.favourite.restaurantOpinion);
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
