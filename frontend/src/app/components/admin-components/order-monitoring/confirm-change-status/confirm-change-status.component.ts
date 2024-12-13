import { Component } from '@angular/core';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {LanguageService} from "../../../../services/language.service";

@Component({
  selector: 'app-confirm-change-status',
  standalone: true,
  imports: [
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    MatButton
  ],
  templateUrl: './confirm-change-status.component.html',
  styleUrl: './confirm-change-status.component.css'
})
export class ConfirmChangeStatusComponent {
  constructor(public dialogRef: MatDialogRef<ConfirmChangeStatusComponent>,
              private languageService: LanguageService) {}

  onCancel() {
    this.dialogRef.close(false);

  }
  onConfirm() {
    this.dialogRef.close(true);
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
