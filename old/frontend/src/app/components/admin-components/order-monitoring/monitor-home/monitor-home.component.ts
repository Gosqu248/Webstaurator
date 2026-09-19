import {Component, OnInit} from '@angular/core';
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {NgForOf, NgIf} from "@angular/common";
import {environment} from "../../../../../environments/environment";
import {AdminOrderDTO} from "../../../../interfaces/order";
import {AuthService} from "../../../../services/api/auth.service";
import {LanguageService} from "../../../../services/state/language.service";
import {OrderService} from "../../../../services/api/order.service";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {MonitorOrderItemComponent} from "../monitor-order-item/monitor-order-item.component";

@Component({
    selector: 'app-monitor-home',
    imports: [
        MatProgressSpinner,
        NgForOf,
        NgIf,
        MonitorOrderItemComponent
    ],
    templateUrl: './monitor-home.component.html',
    styleUrl: './monitor-home.component.css'
})
export class MonitorHomeComponent implements OnInit{
  background = environment.api + '/img/ordersBackground.webp';
  isAuthenticated: boolean = false;
  orders: AdminOrderDTO[] = [];
  isLoading: boolean = true;

  constructor(private authService: AuthService,
              private languageService: LanguageService,
              private orderService: OrderService) {}

  ngOnInit() {
    this.checkAuth();
    this.getOrders();
  }

  checkAuth() {
    this.authService.isAuthenticated$.subscribe(isAuthenticated => {
      this.isAuthenticated = isAuthenticated;
    });
  }

  getOrders() {
    if (this.isAuthenticated) {
      this.orderService.getAllOrders().subscribe({
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


  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
