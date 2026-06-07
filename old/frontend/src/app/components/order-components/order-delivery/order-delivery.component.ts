import {Component, EventEmitter, Inject, Input, OnInit, Output, PLATFORM_ID} from '@angular/core';
import {isPlatformBrowser, NgClass, NgForOf, NgIf} from '@angular/common';
import { LanguageTranslations } from '../../../interfaces/language.interface';
import { LanguageService } from '../../../services/state/language.service';
import { MatDialog } from '@angular/material/dialog';
import { UserAddress } from '../../../interfaces/user.address.interface';
import { OrderDeliveryItemComponent } from '../order-delivery-item/order-delivery-item.component';
import { AddAddressDialogComponent } from '../add-address-dialog/add-address-dialog.component';
import {AddressesService} from "../../../services/api/addresses.service";
import {ChooseHourDialogComponent} from "../choose-hour-dialog/choose-hour-dialog.component";
import {AuthService} from "../../../services/api/auth.service";
import {Coordinates} from "../../../interfaces/coordinates";
import {SearchedRestaurant} from "../../../interfaces/searched-restaurant";


@Component({
    selector: 'app-order-delivery',
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
  @Input() coordinates!: Coordinates;
  @Input() restaurant!: SearchedRestaurant;

  selectedAddress: UserAddress = {} as UserAddress;
  selectedDeliveryOption: string | null = null;
  pickUpTime: number = 0;
  selectedHour: string | null = null;
  deliveryOption: string = '';
  token: string | null = null;

  constructor(private languageService: LanguageService,
              @Inject(PLATFORM_ID) private platformId: Object,
              private addressService: AddressesService,
              protected authService: AuthService,
              private dialog: MatDialog) {}

  ngOnInit() {
    this.getDeliveryOption();
    if (isPlatformBrowser(this.platformId)) {
      this.token = localStorage.getItem('jwt');
    }
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
    if(this.token && this.coordinates) {
      this.addressService.getAvailableAddresses(this.token, this.coordinates).subscribe(addresses => {
        this.addresses = addresses;
      });
    } else {
      console.error('Token or searchedAddress is not set');
    }

  }

  setAddress(address: UserAddress) {
    this.selectedAddress = address;
    this.addressService.setPhoneNumber(address.phoneNumber);
    this.deliveryChanged.emit();
  }

  isAddressSelected(address: UserAddress): boolean {
    if (this.addresses.length === 1) {
      this.setAddress(this.addresses[0]);
    }
    return this.selectedAddress !== null && address.id === this.selectedAddress.id;
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
    const maxTime = this.deliveryOption === 'delivery' ? this.restaurant.delivery?.deliveryMaxTime : this.pickUpTime;


    const dialogRef = this.dialog.open(ChooseHourDialogComponent, {
      width: '700px',
      maxWidth: '100%',
      height: '600px',
      data: {maxTime: maxTime, deliveryTime: this.restaurant.deliveryHours}
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
