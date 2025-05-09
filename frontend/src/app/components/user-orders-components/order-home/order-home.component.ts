import {Component, OnInit, OnDestroy} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {OrderService} from "../../../services/api/order.service";
import {AuthService} from "../../../services/api/auth.service";
import {OrderDTO} from "../../../interfaces/order";
import {OrderItemComponent} from "../order-item/order-item.component";
import {NgForOf, NgIf} from "@angular/common";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/state/language.service";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-order-home',
  standalone: true,
  imports: [
    OrderItemComponent,
    NgForOf,
    MatProgressSpinner,
    NgIf
  ],
  templateUrl: './order-home.component.html',
  styleUrl: './order-home.component.css'
})
export class OrderHomeComponent implements OnInit, OnDestroy {
  background = environment.api + '/img/ordersBackground.webp';
  isAuthenticated: boolean = false;
  orders: OrderDTO[] = [];
  isLoading: boolean = true;
  private opinionAddedSubscription!: Subscription;

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
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error getting user orders:', error);
        }
      });
    }
  }

  ngOnDestroy() {
    if (this.opinionAddedSubscription) {
      this.opinionAddedSubscription.unsubscribe();
    }
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
