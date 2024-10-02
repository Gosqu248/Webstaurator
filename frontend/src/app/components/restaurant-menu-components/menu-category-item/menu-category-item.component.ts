import {Component, Input, OnInit} from '@angular/core';
import {OptionService} from "../../../services/option.service";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-menu-category-item',
  standalone: true,
  imports: [
    NgClass
  ],
  templateUrl: './menu-category-item.component.html',
  styleUrl: './menu-category-item.component.css'
})
export class MenuCategoryItemComponent implements OnInit{
  @Input() category!: string;
  selectedCategory: string = '';

  constructor(private optionService: OptionService) {}

  ngOnInit() {
    this.optionService.selectedMenuCategory$.subscribe(category => {
      this.selectedCategory = category;
    });
  }

}
