import {Component, OnDestroy, OnInit} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {LanguageService} from "../../../services/state/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {NgClass, NgForOf} from "@angular/common";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-home-second-section',
  standalone: true,
  imports: [
    NgForOf,
    NgClass
  ],
  templateUrl: './home-second-section.component.html',
  styleUrl: './home-second-section.component.css'
})
export class HomeSecondSectionComponent implements OnInit, OnDestroy{
  steps: { icon: string; title: string; description: string; }[] = [];
  activeStepIndex = 0;
  private languageSubscription: Subscription | undefined;

  constructor(private languageService: LanguageService) {}

  ngOnInit() {
    this.loadSteps();
    this.languageSubscription = this.languageService.languageChange$.subscribe(() => {
      this.loadSteps();
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

  loadSteps(): void {
    this.steps = [
      {
        icon: 'fa-solid fa-location-dot',
        title: this.getTranslation('step1_1'),
        description: this.getTranslation('step1_2')
      },
      {
        icon: 'fa-solid fa-burger',
        title: this.getTranslation('step2_1'),
        description: this.getTranslation('step2_2')},
      {
        icon: 'fa-solid fa-clipboard-check',
        title: this.getTranslation('step3_1'),
        description: this.getTranslation('step3_2')
      },
    ];
  }


  onScroll(event: any): void {
    const container = event.target;
    const stepWidth = container.scrollWidth / this.steps.length;
    this.activeStepIndex = Math.round(container.scrollLeft / stepWidth);
  }
}
