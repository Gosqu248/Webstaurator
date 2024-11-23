import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { LanguageTranslations } from "../../../interfaces/language.interface";
import { LanguageService } from "../../../services/language.service";
import { MatDialog } from "@angular/material/dialog";
import { AddressesService } from "../../../services/addresses.service";

@Component({
  selector: 'app-add-address-dialog',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './add-address-dialog.component.html',
  styleUrl: './add-address-dialog.component.css'
})
export class AddAddressDialogComponent {
  addressForm: FormGroup;

  constructor(private languageService: LanguageService,
              private addressService: AddressesService,
              private fb: FormBuilder,
              private dialog: MatDialog) {
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

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

  closeDialog() {
    this.dialog.closeAll();
  }

  addAddress() {
    if (this.addressForm.valid) {
      const jwt = localStorage.getItem('jwt');
      if (jwt) {
        this.addressService.addAddress(jwt, this.addressForm.value).subscribe({
          next: response => {
          },
          error: error => {
            console.log('Error adding address', error);
          }
        });
      } else {
        const newAddress = this.addressForm.value;
        sessionStorage.setItem('logoutAddress', JSON.stringify(newAddress));

      }
      this.closeDialog();
    } else {
      console.error('Form is invalid');
    }
  }
}
