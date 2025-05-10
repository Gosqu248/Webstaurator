// Modyfikacja w menu-add-address.component.ts

import { Component } from '@angular/core';
import {NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {LanguageService} from "../../../../services/state/language.service";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {AddressesService} from "../../../../services/api/addresses.service";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MenuAddressesComponent} from "../menu-addresses.component";


@Component({
    selector: 'app-menu-add-address',
    imports: [
        NgIf,
        ReactiveFormsModule
    ],
    templateUrl: './menu-add-address.component.html',
    styleUrl: './menu-add-address.component.css'
})
export class MenuAddAddressComponent {
  addressForm: FormGroup;

  constructor(
    private languageService: LanguageService,
    private addressService: AddressesService,
    private dialog: MatDialog,
    public dialogRef: MatDialogRef<MenuAddAddressComponent>,
    private fb: FormBuilder,
  ) {
    this.addressForm = this.fb.group({
      street: ['', Validators.required],
      houseNumber: ['', Validators.required],
      floorNumber: [' '],
      accessCode: [' '],
      zipCode: ['', Validators.required],
      city: ['', Validators.required],
      phoneNumber: ['',
        [Validators.required,
          Validators.minLength(9),
          Validators.pattern(/^(\d{3}\s\d{3}\s\d{3}|\d{9})$/)
        ]]
    });

    this.addressForm.get('phoneNumber')?.valueChanges.subscribe(value => {
      if (value) {
        const digitsOnly = value.replace(/\D/g, '');

        if (digitsOnly.length === 9) {
          const formatted = this.formatPhoneNumber(digitsOnly);

          if (formatted !== value) {
            this.addressForm.get('phoneNumber')?.setValue(formatted, { emitEvent: false });
          }
        }
      }
    });
  }

  formatPhoneNumber(value: string): string {
    if (!value) return '';

    const digitsOnly = value.replace(/\D/g, '');

    if (digitsOnly.length <= 3) {
      return digitsOnly;
    } else if (digitsOnly.length <= 6) {
      return `${digitsOnly.slice(0, 3)} ${digitsOnly.slice(3)}`;
    } else {
      return `${digitsOnly.slice(0, 3)} ${digitsOnly.slice(3, 6)} ${digitsOnly.slice(6, 9)}`;
    }
  }

  addAddress() {
    if (this.addressForm.valid) {
      const jwt = localStorage.getItem('jwt');
      if (jwt) {
        const formData = {...this.addressForm.value};

        if (formData.phoneNumber) {
          formData.phoneNumber = formData.phoneNumber.replace(/\s/g, '');
        }

        this.addressService.addAddress(jwt, formData).subscribe({
          next: response => {
            console.log('Address added successfully', response);
            this.backToMenuAddressesDialog();
          },
          error: () => {
            alert('Prawdopodobnie podany adres jest nieprawidłowy. Spróbuj ponownie.');
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

  closeDialog() {
    this.dialogRef.close();
  }

  backToMenuAddressesDialog() {
    this.closeDialog();
    this.dialog.open(MenuAddressesComponent);
  }

  showErrorFor(controlName: string): boolean {
    const control = this.addressForm.get(controlName);
    if (controlName === 'phoneNumber') {
      return control ? control.invalid && (control.dirty || control.touched) : false;
    } else {
      return control ? control.invalid && (control.dirty || control.touched) : false;
    }
  }
}
