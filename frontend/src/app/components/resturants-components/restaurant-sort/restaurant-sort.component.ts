import {Component, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {SortItemComponent} from "../sort-item/sort-item.component";
import {NgClass, NgForOf} from "@angular/common";

@Component({
  selector: 'app-restaurant-sort',
  standalone: true,
  imports: [
    SortItemComponent,
    NgForOf,
    NgClass
  ],
  templateUrl: './restaurant-sort.component.html',
  styleUrl: './restaurant-sort.component.css'
})
export class RestaurantSortComponent implements OnInit, OnChanges{
  sortItems: { description: string, icon: string }[] = []

  selectedSortItem: { description: string, icon: string } = { description: '', icon: ''};
  constructor(private languageService:LanguageService) {
  }

  ngOnInit() {
    this.languageService.languageChange$.subscribe(() =>{
      this.updateSortItems();
    })
    this.updateSortItems();
  }

  ngOnChanges(changes: SimpleChanges) {
    changes['languageService'] ? this.updateSortItems() : null;
  }


  getTranslation<k extends keyof LanguageTranslations>(key: k) {
    return this.languageService.getTranslation(key);
  }

  updateSortItems() {
    this.sortItems = [
      { description: this.getTranslation('closeToMe'), icon: 'fa-regular fa-clock' },
      { description: this.getTranslation('grades'), icon: 'fa-regular fa-thumbs-up' },
      { description: this.getTranslation('cheapestDelivery'), icon: 'fa-regular fa-money-bill-1' },
    ];
  }



  selectSortItem(sort: { description: string, icon: string }) {
    return this.selectedSortItem === sort ? this.selectedSortItem = { description: '', icon: ''} : this.selectedSortItem = sort;
  }
}
