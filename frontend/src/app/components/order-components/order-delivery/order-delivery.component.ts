import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import { LanguageTranslations } from '../../../interfaces/language.interface';
import { LanguageService } from '../../../services/language.service';
import { MatDialog } from '@angular/material/dialog';
import { UserAddress } from '../../../interfaces/user.address.interface';
import { OrderDeliveryItemComponent } from '../order-delivery-item/order-delivery-item.component';
import { AddAddressDialogComponent } from '../add-address-dialog/add-address-dialog.component';
import {AddressesService} from "../../../services/addresses.service";
import {DeliveryService} from "../../../services/delivery.service";
import {DeliveryHour} from "../../../interfaces/delivery.interface";
import {ChooseHourDialogComponent} from "../choose-hour-dialog/choose-hour-dialog.component";


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
  @Output() deliveryChanged = new EventEmitter<void>();


  selectedAddress: UserAddress | null = null;
  selectedDeliveryOption: string | null = null;
  minTime: number = 0;
  maxTime: number = 0;
  deliveryTime: DeliveryHour[] = [];
  selectedHour: string | null = null;


  constructor(private languageService: LanguageService,
              private addressService: AddressesService,
              private deliveryService: DeliveryService,
              private dialog: MatDialog) {}

  ngOnInit() {
    this.getDeliveryTime();
  }

  setAddress(address: UserAddress) {
    this.selectedAddress = address;
    this.addressService.setPhoneNumber(address.phoneNumber);
    this.deliveryChanged.emit();
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
    const id = sessionStorage.getItem('restaurantId');
    this.minTime = minTime ? parseInt(minTime) : 0;
    this.maxTime = maxTime ? parseInt(maxTime) : 0;
    const Id = id ? parseInt(id) : 0;

    this.deliveryService.getDeliveryTIme(Id).subscribe((data) => {
      this.deliveryTime = data;
    });
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

  setDelivery(option: string | null): void {
    this.selectedDeliveryOption = option;
    this.selectedHour = null;
    if (option === 'scheduled') {
      this.openHourDialog();
    }
    this.deliveryChanged.emit();

  }

  openHourDialog() {
    const dialogRef = this.dialog.open(ChooseHourDialogComponent, {
      width: '700px',
      maxWidth: '100%',
      height: '600px',
      data: {maxTime: this.maxTime, deliveryTime: this.deliveryTime}
    });

    dialogRef.afterClosed().subscribe(result => {
      if(result) {
        this.selectedHour = result;
      } else {
        this.setDelivery(null)
      }
    });
  }


  isDeliveryOptionSelected(option: string): boolean {
    return this.selectedDeliveryOption === option;
  }
}
