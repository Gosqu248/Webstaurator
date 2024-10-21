import { Component } from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-order-personal-info',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './order-personal-info.component.html',
  styleUrl: './order-personal-info.component.css'
})
export class OrderPersonalInfoComponent {
  personalForm: FormGroup;
  relevantInformation: string = '';

  constructor(private languageService: LanguageService,
              private fb: FormBuilder) {
    this.personalForm = this.fb.group({
      name: [''],
      email: [''],
      phoneNumber: [''],
    });
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
