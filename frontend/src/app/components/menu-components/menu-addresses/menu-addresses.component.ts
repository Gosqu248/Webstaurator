import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {RouterLink} from "@angular/router";
import {AddressesService} from "../../../services/addresses.service";
import {UserAddress} from "../../../interfaces/user.address.interface";
import {MenuAddressesItemComponent} from "../menu-addresses-item/menu-addresses-item.component";
import {MenuAddressChangeComponent} from "../menu-address-change/menu-address-change.component";
import {FragmentsService} from "../../../services/fragments.service";

@Component({
  selector: 'app-menu-addresses',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule,
    RouterLink,
    MenuAddressesItemComponent,
    NgForOf,
    MenuAddressChangeComponent
  ],
  templateUrl: './menu-addresses.component.html',
  styleUrl: './menu-addresses.component.css'
})
export class MenuAddressesComponent implements OnInit{
  addresses: UserAddress[] = [];

  constructor(private languageService: LanguageService, private addressesService: AddressesService, private fragmentService:FragmentsService) {}

  ngOnInit() {
    this.getAddresses();
  }

  getAddresses() {
    const token = localStorage.getItem('jwt');

    if (token) {
      this.addressesService.getUserAddresses(token).subscribe({
        next: addresses => {
          this.addresses = addresses;
          console.log('Addresses fetched: ', this.addresses);
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

  removeFragment() {
    this.fragmentService.removeFragment();
  }

}
