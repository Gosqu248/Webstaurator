import { Component } from '@angular/core';
import {FormsModule} from "@angular/forms";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-menu-change-password',
  standalone: true,
  imports: [
    FormsModule,
    NgClass
  ],
  templateUrl: './menu-change-password.component.html',
  styleUrl: './menu-change-password.component.css'
})
export class MenuChangePasswordComponent {
  isVisibleOldPassword: boolean = false;
  isVisibleNewPassword: boolean = false;
  isVisibleConfirm: boolean = false;
  oldPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';

  constructor(private languageService: LanguageService) {}

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key);
  }

  togglePasswordVisibility(fieldId: string) {
    const inputField = document.getElementById(fieldId) as HTMLInputElement;
    if (inputField.type === 'password') {
      inputField.type = 'text';
    } else {
      inputField.type = 'password';
    }
    fieldId === 'oldPassword' ? this.isVisibleOldPassword = !this.isVisibleOldPassword : fieldId === 'newPassword' ? this.isVisibleNewPassword = !this.isVisibleNewPassword : this.isVisibleConfirm = !this.isVisibleConfirm;

  }
}
