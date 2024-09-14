import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {LanguageService} from "../../../services/language.service";
import {AddressesService} from "../../../services/addresses.service";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {UserAddress} from "../../../interfaces/user.address.interface";

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
export class MenuAddressChangeComponent implements OnInit{
  changeForm: FormGroup;
  addressId!: number;
  token: string | null = localStorage.getItem('jwt');

  constructor(
    private languageService: LanguageService,
    private addressesService: AddressesService,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder
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
    this.route.paramMap.subscribe(params => {
      this.addressId = +params.get('id')!;
      this.loadAddress();
    })
  }

  loadAddress() {

    if (this.token) {
      this.addressesService.getUserAddresses(this.token).subscribe(addresses => {
        const address = addresses.find(addr => addr.id === this.addressId);

        if (address) {
          this.changeForm.patchValue(address);
        }
      })
    }
  }

  changeAddress() {
    if (this.changeForm.valid) {
      const updatedAddress: UserAddress = this.changeForm.value;
      updatedAddress.id = this.addressId;

      this.addressesService.changeAddress(this.token!, updatedAddress).subscribe(() => {
        this.router.navigate(['/addresses']);
      });
    }
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key)
  }
}
