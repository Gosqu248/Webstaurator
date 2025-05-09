import {Component, Inject, OnInit} from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { LanguageService } from "../../../../services/state/language.service";
import { AddressesService } from "../../../../services/api/addresses.service";
import { LanguageTranslations } from "../../../../interfaces/language.interface";
import { UserAddress } from "../../../../interfaces/user.address.interface";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MenuAddressesComponent} from "../menu-addresses.component";

@Component({
  selector: 'app-menu-address-change',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './menu-address-change.component.html',
  styleUrl: './menu-address-change.component.css'
})
export class MenuAddressChangeComponent implements OnInit {
  changeForm: FormGroup;

  constructor(
    private languageService: LanguageService,
    private addressesService: AddressesService,
    private fb: FormBuilder,
    private dialog: MatDialog,
    public dialogRef: MatDialogRef<MenuAddressChangeComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { addressId: number }
  ) {
    this.changeForm = this.fb.group({
      street: ['', Validators.required],
      houseNumber: ['', Validators.required],
      floorNumber: [' '],
      accessCode: [' '],
      zipCode: ['', Validators.required],
      city: ['', Validators.required],
      phoneNumber: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadAddress();
  }

  loadAddress() {
    const token = localStorage.getItem('jwt');

    if (token) {
      this.addressesService.getAddress(token, this.data.addressId).subscribe(address => {
        console.log(address);

        if (address) {
          this.changeForm.patchValue(address);
        } else {
          console.error('Address not found');
        }
      });
    } else {
      console.error('No token found');
    }
  }

  changeAddress() {
    if (this.changeForm.valid) {
      const updatedAddress: UserAddress = this.changeForm.value;
      updatedAddress.id = this.data.addressId;

      const token = localStorage.getItem('jwt');

      if (!token) {
        console.error('No token found');
        return;
      }


      this.addressesService.changeAddress(token, updatedAddress).subscribe(() => {
        this.backToMenuAddressesDialog();
      });
    }
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key);
  }


  closeDialog() {
    this.dialogRef.close();
  }

  backToMenuAddressesDialog() {
    this.closeDialog();
    this.dialog.open(MenuAddressesComponent);
  }
}
