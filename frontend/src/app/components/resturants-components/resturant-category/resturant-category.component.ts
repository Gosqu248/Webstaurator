import {AfterViewInit, Component, ElementRef, Input, OnChanges, OnInit, ViewChild} from '@angular/core';
import { CategoryItemComponent } from '../category-item/category-item.component';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {OptionService} from "../../../services/option.service";

@Component({
  selector: 'app-resturant-category',
  standalone: true,
  imports: [
    CategoryItemComponent,
    NgForOf,
    NgClass,
    NgIf
  ],
  templateUrl: './resturant-category.component.html',
  styleUrls: ['./resturant-category.component.css']
})
export class ResturantCategoryComponent implements OnInit, AfterViewInit {
  @Input() selectedOption!: string;
  @ViewChild('categoryContainer') categoryContainer!: ElementRef;

  selectedCategories: string[] = [];
  categories: string[] = [];
  showLeftScroll = false;
  showRightScroll = false;
  isShow: boolean = false;

  constructor(private optionService: OptionService) {}

  ngOnInit() {
    this.loadCategories();
  }

  ngAfterViewInit() {
    this.checkScrollButtons();
  }

  private loadCategories() {
    this.optionService.categories$.subscribe((categories) => {
      this.categories = categories;
      setTimeout(() => this.checkScrollButtons(), 0);
    });
  }

  checkScrollButtons() {
    const container = this.categoryContainer.nativeElement;
    this.showLeftScroll = container.scrollLeft > 0;
    this.showRightScroll =
      container.scrollWidth > container.clientWidth &&
      container.scrollLeft + container.clientWidth < container.scrollWidth;
  }

  scrollCategories(direction: 'left' | 'right') {
    const container = this.categoryContainer.nativeElement;
    const scrollAmount = 250;

    if (direction === 'left') {
      container.scrollLeft -= scrollAmount;
    } else {
      container.scrollLeft += scrollAmount;
    }

    this.checkScrollButtons();
  }

  selectCategory(category: string) {
    const index = this.selectedCategories.indexOf(category);
    (index > -1) ? this.selectedCategories.splice(index, 1) : this.selectedCategories.push(category);
    this.optionService.setSelectedCategories(this.selectedCategories);
  }


  changeShow() {
    this.isShow = !this.isShow;
  }
}
