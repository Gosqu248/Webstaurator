import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {MenuAddressesItemComponent} from "../menu-addresses-item/menu-addresses-item.component";
import {RouterLink} from "@angular/router";
import {FavouriteService} from "../../../services/favourite.service";
import {Favourites} from "../../../interfaces/favourites";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {MenuFavItemComponent} from "../menu-fav-item/menu-fav-item.component";
import {MenuComponent} from "../menu/menu.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";

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

  constructor(private favouriteService: FavouriteService,
              private languageService: LanguageService,
              private dialog: MatDialog,
              public dialogRef: MatDialogRef<MenuFavouriteComponent>
              ) {}

  ngOnInit() {
    this.getFavourites();
  }

  getFavourites() {
    this.userId = parseInt(localStorage.getItem('userId') || '0', 10);
    this.favouriteService.getUserFavourites(this.userId).subscribe((favourites: any) => {
      this.favourites = favourites;
    });
  }


  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }

  closeDialog() {
    this.dialogRef.close();
  }

  backToMenuDialog() {
    this.closeDialog();
    this.dialog.open(MenuComponent);
  }
}
