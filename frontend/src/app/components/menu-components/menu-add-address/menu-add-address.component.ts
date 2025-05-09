import { Component } from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {RouterLink} from "@angular/router";
import {LanguageService} from "../../../services/state/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {AddressesService} from "../../../services/api/addresses.service";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MenuAddressesComponent} from "../menu-addresses/menu-addresses.component";


@Component({
  selector: 'app-menu-add-address',
  standalone: true,
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
        Validators.minLength(8),
        Validators.pattern(/^\d{9}$/)
      ]]
    });
  }

  addAddress() {
    if (this.addressForm.valid) {
      const jwt = localStorage.getItem('jwt');
      if (jwt) {
        this.addressService.addAddress(jwt, this.addressForm.value).subscribe({
          next: response => {
            console.log('Address added successfully', response);
            this.backToMenuAddressesDialog();
          },
          error: error => {
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
      const phoneNumberPattern = /^\d{3} \d{3} \d{3}$/;
      return control ? control.invalid && (control.dirty || control.touched) && !phoneNumberPattern.test(control.value) : false;
    } else {
      return control ? control.invalid && (control.dirty || control.touched) : false;
    }
  }
}
