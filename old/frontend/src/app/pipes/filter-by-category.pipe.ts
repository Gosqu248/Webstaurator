import { Pipe, PipeTransform } from '@angular/core';
import {Menu} from "../interfaces/menu";

@Pipe({
  name: 'filterByCategory',
  standalone: true
})
export class FilterByCategoryPipe implements PipeTransform {

  transform(menu: Menu[], category: string): Menu[] {
    return menu.filter(item => item.category === category)
  }

}
