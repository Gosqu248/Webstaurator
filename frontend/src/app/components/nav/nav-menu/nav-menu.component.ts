import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {NgIf} from "@angular/common";
import {NavMenuLanguageComponent} from "../nav-menu-language/nav-menu-language.component";
import {NavMenuRegisterComponent} from "../nav-menu-register/nav-menu-register.component";

@Component({
  selector: 'app-nav-menu',
  standalone: true,
  imports: [
    NgIf,
    NavMenuLanguageComponent,
    NavMenuRegisterComponent
  ],
  templateUrl: './nav-menu.component.html',
  styleUrl: './nav-menu.component.css'
})
export class NavMenuComponent implements OnInit{
  @Output() menuClosed = new EventEmitter<void>();

  showLanguageOption: boolean = false;
  showLanguageMenu: boolean = false;
  showRegister: boolean = false;

  constructor(private languageService: LanguageService) {}

  ngOnInit() {
    this.checkWindowWidth();
    window.addEventListener('resize', this.checkWindowWidth.bind(this));
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key)
  }
  closeMenu() {
    this.menuClosed.emit();
  }

  checkWindowWidth() {
    this.showLanguageOption = window.innerWidth < 768;
  }

  toggleLanguageMenu() {
    this.showLanguageMenu = !this.showLanguageMenu;
  }

  toggleRegister() {
    this.showRegister = !this.showRegister;
  }


}
