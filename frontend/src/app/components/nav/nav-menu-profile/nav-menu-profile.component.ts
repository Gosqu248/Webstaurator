import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";

@Component({
  selector: 'app-nav-menu-profile',
  standalone: true,
    imports: [
        FormsModule,
        NgIf,
        ReactiveFormsModule
    ],
  templateUrl: './nav-menu-profile.component.html',
  styleUrl: './nav-menu-profile.component.css'
})
export class NavMenuProfileComponent {
  @Output() closeProfile = new EventEmitter<void>();
  name: string = sessionStorage.getItem('name') || '';
  email: string = sessionStorage.getItem('email') || '';
  originalName: string = this.name;
  isNameChanged: boolean = false;

  constructor(private languageService: LanguageService) {}

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }

  backToMenu() {
    this.closeProfile.emit();
  }

  showNameButton() {
    this.isNameChanged = this.name !== this.originalName;
  }

}
