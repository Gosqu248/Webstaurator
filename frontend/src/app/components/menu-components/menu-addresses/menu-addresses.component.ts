import { Component } from '@angular/core';
import {NgIf} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-menu-addresses',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './menu-addresses.component.html',
  styleUrl: './menu-addresses.component.css'
})
export class MenuAddressesComponent {

  constructor(private languageService: LanguageService) {}

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }
}
