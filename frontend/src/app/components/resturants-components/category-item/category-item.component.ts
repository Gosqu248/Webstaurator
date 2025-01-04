import {Component, Input, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-category-item',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './category-item.component.html',
  styleUrl: './category-item.component.css'
})
export class CategoryItemComponent implements OnInit{
  @Input() name!: string;
  url: string = environment.api + "/img/";
  photo: string = '';

  constructor() {}

  ngOnInit() {
    this.photo = this.name === 'Amerykańska' ? this.url + 'american.jpg' :
      this.name === 'Burgery' ? this.url + 'burger.jpg' :
      this.name === 'Włoska' ? this.url + 'italian.jpg' :
      this.name === 'Kuchniaa Arabska' ? this.url + 'kebab.jpg' :
      this.name === 'Włoska' ? this.url + 'pizza.jpg' :
      this.name === 'Sushi' ? this.url + 'sushi.jpg' :
        this.url + 'sushi.jpg';
  }

}
