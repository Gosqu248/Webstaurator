import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Additives} from "../../../interfaces/menu";
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-additive-item',
  standalone: true,
  imports: [
    NgIf,
    NgClass
  ],
  templateUrl: './additive-item.component.html',
  styleUrl: './additive-item.component.css'
})
export class AdditiveItemComponent {
  @Input() additive!: Additives;
  @Input() isSelected: boolean = false;
  @Output() select = new EventEmitter<Additives>();

  constructor() {}

  addAdditive() {
    this.select.emit(this.additive);
  }
}
