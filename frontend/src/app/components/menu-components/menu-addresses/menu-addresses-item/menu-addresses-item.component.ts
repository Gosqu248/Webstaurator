import {Component, EventEmitter, Input, Output} from '@angular/core';
import {UserAddress} from "../../../../interfaces/user.address.interface";
import {LanguageService} from "../../../../services/state/language.service";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {AddressesService} from "../../../../services/api/addresses.service";
import {MenuAddressesComponent} from "../menu-addresses.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MenuAddressChangeComponent} from "../menu-address-change/menu-address-change.component";

@Component({
    selector: 'app-menu-addresses-item',
    imports: [],
    templateUrl: './menu-addresses-item.component.html',
    styleUrl: './menu-addresses-item.component.css'
})
export class MenuAddressesItemComponent {
  @Input() address?: UserAddress;
  @Output() reloadAddress = new EventEmitter<void>();

  constructor(private languageService: LanguageService,
              public dialogRef: MatDialogRef<MenuAddressesComponent>,
              private dialog: MatDialog,
              private addressesService: AddressesService) {}


  removeAddress() {
    const token = localStorage.getItem('jwt');
    if (token && this.address?.id !== undefined) {
      this.addressesService.removeAddress(token, this.address.id).subscribe({
        next: () => {
          this.reloadAddress.emit();
        },
        error: error => {
          console.error('Error removing address: ', error);
        }
      });
      }
  }

  goToChangeAddress() {
    this.dialogRef.close();
    this.dialog.open(MenuAddressChangeComponent, {
      data: { addressId: this.address?.id },
        width: '100%',
        maxWidth: '800px',
    });
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

}
