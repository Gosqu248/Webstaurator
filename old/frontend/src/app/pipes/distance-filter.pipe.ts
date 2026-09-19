import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'distanceFilter',
  standalone: true
})
export class DistanceFilterPipe implements PipeTransform {

  transform(distance: number): string {
    if (distance > 1) {
      return `${Math.round(distance * 10) / 10} km`;
    } else {
      return `${Math.round(distance * 1000)} m`;
    }
  }

}
