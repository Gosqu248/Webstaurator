import {Component, OnInit} from '@angular/core';
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MonitorOrderItemComponent} from "../../order-monitoring/monitor-order-item/monitor-order-item.component";
import {NgForOf, NgIf, TitleCasePipe} from "@angular/common";
import {environment} from "../../../../../environments/environment";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {LanguageService} from "../../../../services/language.service";
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {RestaurantService} from "../../../../services/restaurant.service";
import {MatTimepickerModule} from "@angular/material/timepicker";
import {MatInput, MatInputModule} from "@angular/material/input";
import {Delivery, DeliveryHour} from "../../../../interfaces/delivery.interface";
import {RestaurantAddress} from "../../../../interfaces/restaurant-address";
import {PaymentMethodsService} from "../../../../services/payment-methods.service";
import {RestaurantDataFormComponent} from "../restaurant-data-form/restaurant-data-form.component";
import {RestaurantAddressFormComponent} from "../restaurant-address-form/restaurant-address-form.component";
import {
  RestaurantDeliveryDataFormComponent
} from "../restaurant-delivery-data-form/restaurant-delivery-data-form.component";
import {RestaurantPaymentFormComponent} from "../restaurant-payment-form/restaurant-payment-form.component";
import {
  RestaurantDeliveryHoursFormComponent
} from "../restaurant-delivery-hours-form/restaurant-delivery-hours-form.component";
import {MenuFormComponent} from "../menu-form/menu-form.component";
import {AddRestaurant} from "../../../../interfaces/restaurant";
import {Menu} from "../../../../interfaces/menu";
import {Router} from "@angular/router";
import {PaymentMethod} from "../../../../interfaces/paymentMethod";

@Component({
  selector: 'app-add-restaurant-home',
  standalone: true,
  imports: [
    MatProgressSpinner,
    MonitorOrderItemComponent,
    NgForOf,
    NgIf,
    FormsModule,
    ReactiveFormsModule,
    MatTimepickerModule,
    TitleCasePipe,
    MatInput,
    MatInputModule,
    RestaurantDataFormComponent,
    RestaurantAddressFormComponent,
    RestaurantDeliveryDataFormComponent,
    RestaurantPaymentFormComponent,
    RestaurantDeliveryHoursFormComponent,
    MenuFormComponent
  ],
  templateUrl: './add-restaurant-home.component.html',
  styleUrl: './add-restaurant-home.component.css',
})
export class AddRestaurantHomeComponent implements OnInit{
  background = environment.api + '/img/AddRestaurant.webp';
  addForm: FormGroup;
  restaurantAddress: RestaurantAddress = {} as RestaurantAddress;
  delivery: Delivery = {} as  Delivery;
  deliveryHours: DeliveryHour[] = [];
  paymentMethods: PaymentMethod[] = [];
  menuItems: Menu[] = [];


  constructor(private languageService: LanguageService,
              private fb: FormBuilder,
              private router: Router,
              private paymentMethodsService: PaymentMethodsService,
              private restaurantService: RestaurantService) {
    this.addForm = this.fb.group({
      restaurantName: ['', Validators.required],
      category: ['', Validators.required],
      imageUrl: ['', Validators.required],
      logoUrl: ['', Validators.required],
      street: ['', Validators.required],
      flatNumber: ['', Validators.required],
      city: ['', Validators.required],
      zipCode: ['', Validators.required],
      deliveryMaxTime: ['', Validators.required],
      deliveryMinTime: ['', Validators.required],
      deliveryPrice: ['', Validators.required],
      minimumPrice: ['', Validators.required],
      pickupTime: ['', Validators.required],
      mondayOpen: [''],
      mondayClose: [''],
      tuesdayOpen: [''],
      tuesdayClose: [''],
      wednesdayOpen: [''],
      wednesdayClose: [''],
      thursdayOpen: [''],
      thursdayClose: [''],
      fridayOpen: [''],
      fridayClose: [''],
      saturdayOpen: [''],
      saturdayClose: [''],
      sundayOpen: [''],
      sundayClose: [''],
      paymentMethods: this.fb.array([], Validators.required)
    });
  }

  ngOnInit() {
    this.paymentMethodsService.getPaymentMethods().subscribe(paymentMethods => {
      this.paymentMethods = paymentMethods;
      const paymentMethodsArray = this.addForm.get('paymentMethods') as FormArray;

      while (paymentMethodsArray.length !== 0) {
        paymentMethodsArray.removeAt(0);
      }

      paymentMethods.forEach(paymentMethod => {
        paymentMethodsArray.push(
          this.fb.group({
            method: paymentMethod.method,
            selected: false
          })
        );
      });
    });
  }
  emitDaysOfWeek(deliveryHours: DeliveryHour[]) {
    this.deliveryHours = deliveryHours;
  }

  updateMenuItems(menuItems: Menu[]) {
    this.menuItems = menuItems;
    console.log('Updated menu items:', this.menuItems); // Debugowanie
  }



  addRestaurant() {

    if (this.addForm.valid) {
      const formData = this.addForm.value;

      this.delivery = {
        deliveryMinTime: formData.deliveryMinTime,
        deliveryMaxTime: formData.deliveryMaxTime,
        deliveryPrice: formData.deliveryPrice,
        minimumPrice: formData.minimumPrice,
        pickupTime: formData.pickupTime
      }

      this.restaurantAddress = {
        street: formData.street,
        flatNumber: formData.flatNumber,
        city: formData.city,
        zipCode: formData.zipCode,

      }

      const selectedPaymentMethodName = formData.paymentMethods
        .filter((pm: any) => pm.selected)
        .map((pm: any) => pm.method);


      const requestPayload: AddRestaurant = {
        name: formData.restaurantName,
        category: formData.category,
        imageUrl: formData.imageUrl,
        logoUrl: formData.logoUrl,
        restaurantAddress: this.restaurantAddress,
        delivery: this.delivery,
        deliveryHours: this.deliveryHours,
        paymentMethods: selectedPaymentMethodName,
        menu: this.menuItems,

      };
      this.restaurantService.addRestaurant(requestPayload);
      this.router.navigate(['/']);
    } else {
      console.log('Form is invalid');
    }
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }


}
