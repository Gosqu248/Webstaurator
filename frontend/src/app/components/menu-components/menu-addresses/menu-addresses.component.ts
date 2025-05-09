import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {LanguageService} from "../../../services/state/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {RouterLink} from "@angular/router";
import {AddressesService} from "../../../services/api/addresses.service";
import {UserAddress} from "../../../interfaces/user.address.interface";
import {MenuAddressesItemComponent} from "../menu-addresses-item/menu-addresses-item.component";
import {MenuAddressChangeComponent} from "../menu-address-change/menu-address-change.component";
import {MenuComponent} from "../menu/menu.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MenuAddAddressComponent} from "../menu-add-address/menu-add-address.component";

@Component({
  selector: 'app-menu-addresses',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MenuAddressesItemComponent,
    NgForOf
  ],
  templateUrl: './menu-addresses.component.html',
  styleUrl: './menu-addresses.component.css'
})
export class MenuAddressesComponent implements OnInit{
  addresses: UserAddress[] = [];

  constructor(private languageService: LanguageService,
              private addressesService: AddressesService,
              private dialog: MatDialog,
              public dialogRef: MatDialogRef<MenuAddressesComponent>) {}

  ngOnInit() {
    this.getAddresses();
  }

  getAddresses() {
    const token = localStorage.getItem('jwt');

    if (token) {
      this.addressesService.getUserAddresses(token).subscribe({
        next: addresses => {
          this.addresses = addresses;
        },
        error: error => {
          console.error('Error fetching addresses: ', error);
        }
      });
      } else {
        console.error('No token found');
      }
    }

    onAddressRemoved() {
      this.getAddresses();
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

  openAddAddressDialog() {
    this.closeDialog();
    this.dialog.open(MenuAddAddressComponent);
  }

}
