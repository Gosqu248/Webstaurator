import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {MenuAddressesItemComponent} from "../menu-addresses-item/menu-addresses-item.component";
import {RouterLink} from "@angular/router";
import {FavouriteService} from "../../../services/favourite.service";
import {Favourites} from "../../../interfaces/favourites";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {FragmentsService} from "../../../services/fragments.service";
import {MenuFavItemComponent} from "../menu-fav-item/menu-fav-item.component";

@Component({
  selector: 'app-menu-favourite',
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
    MenuAddressesItemComponent,
    RouterLink,
    MenuFavItemComponent
  ],
  templateUrl: './menu-favourite.component.html',
  styleUrl: './menu-favourite.component.css'
})
export class MenuFavouriteComponent  implements OnInit{
  userId: number = 0;
  favourites: Favourites[] = [];

  constructor(private favouriteService: FavouriteService, private languageService: LanguageService, private fragmentService: FragmentsService) {}

  ngOnInit() {
    this.getFavourites();
  }

  getFavourites() {
    this.userId = parseInt(localStorage.getItem('userId') || '0', 10);
    this.favouriteService.getUserFavourites(this.userId).subscribe((favourites: any) => {
      this.favourites = favourites;
      console.log('Favourites fetched: ', this.favourites);
    });
  }


  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }

  removeFragment() {
    this.fragmentService.removeFragment();
  }
}
