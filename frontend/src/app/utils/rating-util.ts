import {RestaurantOpinions} from "../interfaces/restaurant";

export class RatingUtil {
  static getAverageRating(opinions: RestaurantOpinions[]): number {
    if (opinions.length === 0) {
      return 0;
    }
    const totalRating = opinions
      .map((opinion: RestaurantOpinions) => opinion.qualityRating + opinion.deliveryRating)
      .reduce((acc:number, rating:number) => acc + rating, 0);

    return Math.round((totalRating / opinions.length / 2) * 10) / 10;
  }

}
