import {ChangeDetectorRef, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {AuthService} from "../../../services/auth.service";

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
export class NavMenuProfileComponent implements OnInit {
  @Output() closeProfile = new EventEmitter<void>();
  name: string = '';
  email: string = '';
  originalName: string = this.name;
  isNameChanged: boolean = false;

  constructor(
    private languageService: LanguageService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}


  ngOnInit() {
    this.name = sessionStorage.getItem('name') || '';
    this.email = sessionStorage.getItem('email') || '';
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }

  backToMenu() {
    this.closeProfile.emit();
    this.cdr.detectChanges();
  }

  showNameButton() {
    this.isNameChanged = this.name !== this.originalName;
  }

  changeName() {
    const jwt = sessionStorage.getItem('jwt');
    if (jwt) {
      this.authService.changeUserName(jwt, this.name).subscribe({
          next: updatedName => {
          sessionStorage.setItem('name', updatedName);
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
