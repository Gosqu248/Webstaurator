import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {UserAddress} from "../../../interfaces/user.address.interface";

@Component({
    selector: 'app-order-delivery-item',
    imports: [
        NgIf,
        NgClass
    ],
    templateUrl: './order-delivery-item.component.html',
    styleUrl: './order-delivery-item.component.css'
})
export class OrderDeliveryItemComponent {
  @Input() address!: UserAddress;
  @Input() isSelected!: boolean;
  @Output() setAddress = new EventEmitter<unknown>();

  select() {
    this.setAddress.emit(this.address);
  }



}
