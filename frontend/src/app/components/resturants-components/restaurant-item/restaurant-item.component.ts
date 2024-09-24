import {Component, Input} from '@angular/core';
import {Restaurant, RestaurantOpinions} from "../../../interfaces/restaurant";

@Component({
  selector: 'app-restaurant-item',
  standalone: true,
  imports: [],
  templateUrl: './restaurant-item.component.html',
  styleUrl: './restaurant-item.component.css'
})
export class RestaurantItemComponent {
  @Input() restaurant!: Restaurant;

  getAverageRating(): number {
    if (this.restaurant.restaurantOpinions.length === 0) {
      return 0;
    }

    const totalRating = this.restaurant.restaurantOpinions
      .map((opinion: RestaurantOpinions) => opinion.qualityRating + opinion.deliveryRating)
      .reduce((acc:number, rating:number) => acc + rating, 0);

    return totalRating / this.restaurant.restaurantOpinions.length / 2;
  }

  getRatingLength(): number {
    sessionStorage.setItem('ratingLength', this.restaurant.restaurantOpinions.length.toString());
    return this.restaurant.restaurantOpinions.length;
  }


}
