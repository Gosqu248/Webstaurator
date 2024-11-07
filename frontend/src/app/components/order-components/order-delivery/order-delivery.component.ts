import {Component, EventEmitter, Inject, Input, OnInit, Output, PLATFORM_ID} from '@angular/core';
import {isPlatformBrowser, NgClass, NgForOf, NgIf} from '@angular/common';
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
import {AuthService} from "../../../services/auth.service";
import {OptionService} from "../../../services/option.service";


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
  @Output() deliveryChanged = new EventEmitter<void>();
  @Output() openLoginDialog = new EventEmitter<unknown>();
  @Input() addresses!: UserAddress[];
  selectedAddress: UserAddress = {} as UserAddress;
  selectedDeliveryOption: string | null = null;
  minTime: number = 0;
  maxTime: number = 0;
  pickUpTime: number = 0;
  deliveryTime: DeliveryHour[] = [];
  selectedHour: string | null = null;
  deliveryOption: string = '';
  token: string | null = null;

  constructor(private languageService: LanguageService,
              @Inject(PLATFORM_ID) private platformId: Object,
              private addressService: AddressesService,
              private deliveryService: DeliveryService,
              protected authService: AuthService,
              private dialog: MatDialog) {}

  ngOnInit() {
    this.getDeliveryOption();
    if (isPlatformBrowser(this.platformId)) {
      this.token = localStorage.getItem('jwt');
    }
    this.getDeliveryTime();
  }


  getDeliveryOption() {
    if (typeof localStorage !== 'undefined') {
      const deliveryOption = localStorage.getItem('deliveryOption');

      if (deliveryOption) {
        this.deliveryOption = deliveryOption;
      }
    }
  }

  getUserAddresses() {
    if(this.token)
      this.addressService.getUserAddresses(this.token).subscribe(addresses => {
        this.addresses = addresses;
      });
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
    if (isPlatformBrowser(this.platformId)) {
      const minTime = sessionStorage.getItem('deliveryMin');
      const maxTime = sessionStorage.getItem('deliveryMax');
      const id = sessionStorage.getItem('restaurantId');
      const pickUpTime = sessionStorage.getItem('pickupTime');

      this.minTime = minTime ? parseInt(minTime) : 0;
      this.maxTime = maxTime ? parseInt(maxTime) : 0;
      this.pickUpTime = pickUpTime ? parseInt(pickUpTime) : 0;
      const Id = id ? parseInt(id) : 0;

      this.deliveryService.getDeliveryTIme(Id).subscribe((data) => {
        this.deliveryTime = data;
      });
    }


  }


  openAddAddress(): void {
    if (this.authService.isAuthenticated()) {

      const dialog = this.dialog.open(AddAddressDialogComponent, {
        width: '1200px',
        height: '600px',
      });

      dialog.afterClosed().subscribe(() => {
        this.getUserAddresses();
        this.deliveryChanged.emit();
      });

    } else {
      this.openLogin();
    }
  }

  openLogin() {
    this.openLoginDialog.emit();
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
    const maxTime = this.deliveryOption === 'delivery' ? this.maxTime : this.pickUpTime;

    const dialogRef = this.dialog.open(ChooseHourDialogComponent, {
      width: '700px',
      maxWidth: '100%',
      height: '600px',
      data: {maxTime: maxTime, deliveryTime: this.deliveryTime}
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
