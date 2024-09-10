import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgClass, NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {AuthService} from "../../../services/auth.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";

@Component({
  selector: 'app-menu-profile',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule,
    NgClass,
  ],
  templateUrl: './menu-profile.component.html',
  styleUrl: './menu-profile.component.css'
})
export class MenuProfileComponent implements OnInit {
  @Output() closeProfile = new EventEmitter<void>();
  @Output() profileClosedAndReload = new EventEmitter<void>();
  name: string = '';
  email: string = '';
  originalName: string = this.name;
  isNameChanged: boolean = false;


  constructor(
    private languageService: LanguageService,
    private authService: AuthService,
  ) {}


  ngOnInit() {
    this.name = localStorage.getItem('name') || '';
    this.email = localStorage.getItem('email') || '';
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }

  backToMenu() {
    this.closeProfile.emit();

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


}
