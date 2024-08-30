import {Component, OnDestroy, OnInit} from '@angular/core';
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {NgClass, NgForOf} from "@angular/common";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-home-third-section',
  standalone: true,
  imports: [
    NgClass,
    NgForOf
  ],
  templateUrl: './home-third-section.component.html',
  styleUrl: './home-third-section.component.css'
})
export class HomeThirdSectionComponent implements OnInit, OnDestroy{
  activeWhyIndex = 0;
  whyItems: { icon: string; title: string; description: string; }[] = [];
  private languageSubscription: Subscription | undefined;

  constructor(private languageService: LanguageService) {}

  ngOnInit() {
    this.loadWhyItems();
    this.languageSubscription = this.languageService.languageChange$.subscribe(() => {
      this.loadWhyItems();
    })
  }

  ngOnDestroy() {
    if(this.languageSubscription) {
      this.languageSubscription.unsubscribe();
    }
  }


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

  loadWhyItems(): void {
    this.whyItems = [
      {
        icon: 'fa-solid fa-truck-fast',
        title: this.getTranslation('why1_1'),
        description: this.getTranslation('why1_2')
      },
      {icon: 'fa-solid fa-list', title: this.getTranslation('why2_1'), description: this.getTranslation('why2_2')},
      {icon: 'fa-solid fa-award', title: this.getTranslation('why3_1'), description: this.getTranslation('why3_2')},
      {
        icon: 'fa-solid fa-user-shield',
        title: this.getTranslation('why4_1'),
        description: this.getTranslation('why4_2')
      },
    ];
  }


  onScroll(event: any): void {
    const container = event.target;
    const whyWidth = container.scrollWidth / this.whyItems.length;
    this.activeWhyIndex = Math.round(container.scrollLeft / whyWidth);
  }
}
