import {Component} from '@angular/core';
import {NgIf} from "@angular/common";
import {environment} from "../../../../environments/environment";
import {LanguageService} from "../../../services/state/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {MenuComponent} from "../menu/menu.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-menu-language',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './menu-language.component.html',
  styleUrl: './menu-language.component.css'
})
export class MenuLanguageComponent {
  apiUrl = environment.api;
  ukFlag = '/img/uk-logo.png';
  plFlag = '/img/poland-logo.png';

  constructor(private languageService: LanguageService,
              private dialog: MatDialog,
              public dialogRef: MatDialogRef<MenuLanguageComponent>) {}


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key)
  }

  setLanguage(language: string) {
    if(this.getCurrentLanguage() != language) {
      this.languageService.switchLanguage();
    }
  }

  getCurrentLanguage() {
    return this.languageService.getCurrentLanguage();
  }

  closeDialog() {
    this.dialogRef.close();
  }

  backToMenuDialog() {
    this.closeDialog();
    this.dialog.open(MenuComponent);
  }


}
