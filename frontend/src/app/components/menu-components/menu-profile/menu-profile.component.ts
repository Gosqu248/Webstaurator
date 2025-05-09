import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {LanguageService} from "../../../services/state/language.service";
import {AuthService} from "../../../services/api/auth.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {MenuComponent} from "../menu/menu.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MenuChangePasswordComponent} from "../menu-change-password/menu-change-password.component";

@Component({
  selector: 'app-menu-profile',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule,


  ],
  templateUrl: './menu-profile.component.html',
  styleUrl: './menu-profile.component.css'
})
export class MenuProfileComponent implements OnInit {
  name: string = '';
  email: string = '';
  originalName: string = this.name;
  isNameChanged: boolean = false;


  constructor(
    private languageService: LanguageService,
    private authService: AuthService,
    private dialog: MatDialog,
    public dialogRef: MatDialogRef<MenuProfileComponent>,
  ) {}


  ngOnInit() {
    this.name = localStorage.getItem('name') || '';
    this.email = localStorage.getItem('email') || '';
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }


  showNameButton() {
    this.isNameChanged = this.name !== this.originalName && this.name.length > 0;
  }

  changeName() {
    const jwt = localStorage.getItem('jwt');
    if (jwt) {
      this.authService.changeUserName(jwt, this.name).subscribe({
        next: updatedName => {
          localStorage.setItem('name', updatedName);
          this.originalName = updatedName;
          this.isNameChanged = false;
        },
        error: error => {
          console.error('Error changing name', error);
        }
      })
    }
  }

  closeDialog() {
    this.dialogRef.close();
  }

  backToMenuDialog() {
    this.closeDialog();
    this.dialog.open(MenuComponent);
  }

  goToChangePasswordDialog() {
    this.closeDialog();
    this.dialog.open(MenuChangePasswordComponent);
  }

}
