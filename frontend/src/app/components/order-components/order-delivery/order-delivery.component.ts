import {Component, Input, OnInit} from '@angular/core';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import { LanguageTranslations } from '../../../interfaces/language.interface';
import { LanguageService } from '../../../services/language.service';
import { MatDialog } from '@angular/material/dialog';
import { UserAddress } from '../../../interfaces/user.address.interface';
import { OrderDeliveryItemComponent } from '../order-delivery-item/order-delivery-item.component';
import { AddAddressDialogComponent } from '../add-address-dialog/add-address-dialog.component';
import {AddressesService} from "../../../services/addresses.service";
import {Restaurant} from "../../../interfaces/restaurant";
import {RestaurantsService} from "../../../services/restaurants.service";


@Component({
  selector: 'app-order-delivery',
  standalone: true,
  imports: [
    OrderDeliveryItemComponent,
    NgIf,
    NgForOf,
    NgClass
  ],
  templateUrl: './order-delivery.component.html',
  styleUrl: './order-delivery.component.css'
})
export class OrderDeliveryComponent implements OnInit{
  @Input() addresses!: UserAddress[];
  selectedAddress: UserAddress | null = null;
  selectedDeliveryOption: string | null = null;
  minTime: number = 0;
  maxTime: number = 0;


  constructor(private languageService: LanguageService,
              private addressService: AddressesService,
              private restaurantService: RestaurantsService,
              private dialog: MatDialog) {}

  ngOnInit() {
    this.getDeliveryTime();

  }

  setAddress(address: UserAddress) {
    this.selectedAddress = address;
  }

  isAddressSelected(address: UserAddress): boolean {
    if (this.addresses.length === 1) {
      this.selectedAddress = this.addresses[0];
    }
    return this.selectedAddress !== null && address.id === this.selectedAddress.id;
  }

  getDeliveryTime() {
    const minTime = sessionStorage.getItem('deliveryMin');
    const maxTime = sessionStorage.getItem('deliveryMax');
    console.log(minTime, maxTime)

    this.minTime = minTime ? parseInt(minTime) : 0;
    this.maxTime = maxTime ? parseInt(maxTime) : 0;
  }

  openAddAddress(): void {
    const dialog = this.dialog.open(AddAddressDialogComponent, {
      width: '1200px',
      height: '600px',
    });

    dialog.afterClosed().subscribe(() => {
      const token = localStorage.getItem('jwt');
      this.addressService.loadAddresses(token);
    });
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }

  setDelivery(option: string): void {
    this.selectedDeliveryOption = option;
  }

  isDeliveryOptionSelected(option: string): boolean {
    return this.selectedDeliveryOption === option;
  }
}
