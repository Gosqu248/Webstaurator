import { Component } from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import { Router, RouterLink} from "@angular/router";
import {LanguageService} from "../../../services/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {AddressesService} from "../../../services/addresses.service";

@Component({
  selector: 'app-menu-add-address',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule,
    RouterLink,
    NgClass
  ],
  templateUrl: './menu-add-address.component.html',
  styleUrl: './menu-add-address.component.css'
})
export class MenuAddAddressComponent {
  addressForm: FormGroup;

  constructor(
    private languageService: LanguageService,
    private addressService: AddressesService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.addressForm = this.fb.group({
      street: ['', Validators.required],
      houseNumber: ['', Validators.required],
      floorNumber: [' '],
      accessCode: [' '],
      zipCode: ['', Validators.required],
      city: ['', Validators.required],
      phoneNumber: ['', Validators.required]
    });
  }

  addAddress() {
    if (this.addressForm.valid) {
      const jwt = localStorage.getItem('jwt');
      if (jwt) {
        this.addressService.addAddress(jwt, this.addressForm.value).subscribe({
          next: response => {
            console.log('Address added successfully', response);
            this.router.navigate(['/addresses']);
          },
          error: error => {
            console.log('Error adding address', error);
          }
        });
      }
    } else {
      console.error('Form is invalid');
    }
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }
}
