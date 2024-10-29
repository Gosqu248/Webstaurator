import {Component, Inject, OnInit} from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { LanguageService } from "../../../services/language.service";
import { AddressesService } from "../../../services/addresses.service";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { LanguageTranslations } from "../../../interfaces/language.interface";
import { UserAddress } from "../../../interfaces/user.address.interface";
import {MenuComponent} from "../menu/menu.component";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MenuAddressesComponent} from "../menu-addresses/menu-addresses.component";

@Component({
  selector: 'app-menu-address-change',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './menu-address-change.component.html',
  styleUrl: './menu-address-change.component.css'
})
export class MenuAddressChangeComponent implements OnInit {
  changeForm: FormGroup;
  addressId!: number;
  token: string | null = localStorage.getItem('jwt');

  constructor(
    private languageService: LanguageService,
    private addressesService: AddressesService,
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    public dialogRef: MatDialogRef<MenuAddressChangeComponent>,
    private route: ActivatedRoute,
    @Inject(MAT_DIALOG_DATA) public data: any
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
    this.addressId = data.address?.id;
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.addressId = Number(params['id']);

    })
    this.loadAddress();
  }

  loadAddress() {
    if (this.token) {
      this.addressesService.getUserAddresses(this.token).subscribe(addresses => {

        const address = addresses.find(addr => addr.id === this.addressId);

        if (address) {
          this.changeForm.patchValue(address);
        } else {
          console.error('Address not found');
        }
      });
    }
  }

  changeAddress() {
    if (this.changeForm.valid) {
      const updatedAddress: UserAddress = this.changeForm.value;
      updatedAddress.id = this.addressId;

      this.addressesService.changeAddress(this.token!, updatedAddress).subscribe(() => {
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
