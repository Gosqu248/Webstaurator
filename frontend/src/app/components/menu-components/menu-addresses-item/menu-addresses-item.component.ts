import {Component, EventEmitter, Input, Output} from '@angular/core';
import {UserAddress} from "../../../interfaces/user.address.interface";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {AddressesService} from "../../../services/addresses.service";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-menu-addresses-item',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './menu-addresses-item.component.html',
  styleUrl: './menu-addresses-item.component.css'
})
export class MenuAddressesItemComponent {
  @Input() address?: UserAddress;
  @Output() reloadAddress = new EventEmitter<void>();

  constructor(private languageService: LanguageService, private addressesService: AddressesService) {}


  removeAddress() {
    const token = localStorage.getItem('jwt');
    if (token && this.address?.id !== undefined) {
      this.addressesService.removeAddress(token, this.address.id).subscribe({
        next: () => {
          console.log('Address removed');
          this.reloadAddress.emit();
        },
        error: error => {
          console.error('Error removing address: ', error);
        }
      });
      }
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

}
