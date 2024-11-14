import {Component, OnInit} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {OrderService} from "../../../services/order.service";
import {Router} from "@angular/router";
import {AuthService} from "../../../services/auth.service";
import {OrderDTO} from "../../../interfaces/order";
import {OrderItemComponent} from "../order-item/order-item.component";
import {NgForOf} from "@angular/common";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";

@Component({
  selector: 'app-order-home',
  standalone: true,
  imports: [
    OrderItemComponent,
    NgForOf
  ],
  templateUrl: './order-home.component.html',
  styleUrl: './order-home.component.css'
})
export class OrderHomeComponent implements OnInit{
  background = environment.api + '/img/ordersBackground.webp';
  isAuthenticated: boolean = false;
  orders: OrderDTO[] = [];

  constructor(private authService: AuthService,
              private languageService: LanguageService,
              private orderService: OrderService) {}

  ngOnInit() {
   this.checkAuth();
   this.getUserOrders();
  }

  checkAuth() {
    this.authService.isAuthenticated$.subscribe(isAuthenticated => {
      this.isAuthenticated = isAuthenticated;
    });
  }

  getUserOrders() {
    if (this.isAuthenticated) {

      const token = localStorage.getItem('jwt');
      if (!token) {
        console.error('No token found');
        return;
      }

      this.orderService.getUserOrders(token).subscribe({
        next: (orders) => {
          this.orders = orders;
        },
        error: (error) => {
          console.error('Error getting user orders:', error);
        }
      });

    }
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
