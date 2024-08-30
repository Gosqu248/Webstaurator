import {Component, EventEmitter, Input, Output} from '@angular/core';
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";

@Component({
  selector: 'app-nav-menu',
  standalone: true,
  imports: [],
  templateUrl: './nav-menu.component.html',
  styleUrl: './nav-menu.component.css'
})
export class NavMenuComponent {
  @Output() menuClosed = new EventEmitter<void>();

  constructor(private languageService: LanguageService) {}

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key)
  }

  closeMenu() {
    this.menuClosed.emit();
  }
}
