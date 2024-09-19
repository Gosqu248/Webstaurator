import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { LanguageService } from "../../../services/language.service";
import { AddressesService } from "../../../services/addresses.service";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { LanguageTranslations } from "../../../interfaces/language.interface";
import { UserAddress } from "../../../interfaces/user.address.interface";
import { FragmentsService } from "../../../services/fragments.service";

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
    private fragmentService: FragmentsService,
    private route: ActivatedRoute
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
    this.route.queryParams.subscribe(params => {
      this.addressId = Number(params['id']);

    })
    this.loadAddress();
  }

  loadAddress() {
    if (this.token) {
      this.addressesService.getUserAddresses(this.token).subscribe(addresses => {
        console.log('Address ID: ', this.addressId);
        console.log('Addresses:', addresses); // Dodaj to, aby zobaczyć, jakie adresy są pobierane

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
        this.router.navigate([], {fragment: 'addresses'});
      });
    }
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key);
  }

  removeFragment() {
    this.fragmentService.removeFragment();
  }
}
