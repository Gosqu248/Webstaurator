import {Component, Input} from '@angular/core';
import {NgClass} from "@angular/common";

@Component({
    selector: 'app-sort-item',
    imports: [
        NgClass
    ],
    templateUrl: './sort-item.component.html',
    styleUrl: './sort-item.component.css'
})
export class SortItemComponent {
  @Input() description!: string | undefined;
  @Input() icon!: string | undefined;
  @Input() selected!: boolean;

}
