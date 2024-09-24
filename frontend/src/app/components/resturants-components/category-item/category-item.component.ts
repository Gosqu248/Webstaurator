import {Component, Input, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";

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
  url: string = 'http://localhost:8080/img/';
  photo: string = '';

  constructor() {}

  ngOnInit() {
    this.photo = this.name === 'American' ? this.url + 'american.jpg' :
      this.name === 'Burger' ? this.url + 'burger.jpg' :
      this.name === 'Italian' ? this.url + 'italian.jpg' :
      this.name === 'Kebab' ? this.url + 'kebab.jpg' :
      this.name === 'Pizza' ? this.url + 'pizza.jpg' :
      this.name === 'Sushi' ? this.url + 'sushi.jpg' :
        this.url + 'sushi.jpg';
  }

}
