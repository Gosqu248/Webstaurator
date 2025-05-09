import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import { CategoryItemComponent } from '../category-item/category-item.component';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {OptionService} from "../../../services/state/option.service";

@Component({
  selector: 'app-restaurant-category',
  standalone: true,
  imports: [
    CategoryItemComponent,
    NgForOf,
    NgClass,
    NgIf
  ],
  templateUrl: './restaurant-category.component.html',
  styleUrls: ['./restaurant-category.component.css']
})
export class RestaurantCategoryComponent implements OnInit, AfterViewInit {
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
    const observer = new MutationObserver(() => this.checkScrollButtons());
    const container = this.categoryContainer.nativeElement;

    observer.observe(container, {
      childList: true,
      subtree: true
    });

    window.addEventListener('resize', () => this.checkScrollButtons());
  }

  private loadCategories() {
    this.optionService.categories$.subscribe((categories) => {
      this.categories = categories;
      setTimeout(() => this.checkScrollButtons(), 0);
    });
  }

  checkScrollButtons() {
    if (!this.categoryContainer) return;

    const container = this.categoryContainer.nativeElement;

    requestAnimationFrame(() => {
      const isOverflowing = container.scrollWidth > container.clientWidth;

      this.showLeftScroll = isOverflowing && container.scrollLeft > 0;
      this.showRightScroll = isOverflowing &&
        (container.scrollWidth - container.scrollLeft > container.clientWidth);
    });
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
    this.checkScrollButtons();
  }
}
