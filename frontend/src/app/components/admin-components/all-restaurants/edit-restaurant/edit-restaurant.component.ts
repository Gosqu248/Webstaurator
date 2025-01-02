import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MenuFormComponent} from "../../add-restaurant/menu-form/menu-form.component";
import {
    RestaurantAddressFormComponent
} from "../../add-restaurant/restaurant-address-form/restaurant-address-form.component";
import {RestaurantDataFormComponent} from "../../add-restaurant/restaurant-data-form/restaurant-data-form.component";
import {
    RestaurantDeliveryDataFormComponent
} from "../../add-restaurant/restaurant-delivery-data-form/restaurant-delivery-data-form.component";
import {
    RestaurantDeliveryHoursFormComponent
} from "../../add-restaurant/restaurant-delivery-hours-form/restaurant-delivery-hours-form.component";
import {
    RestaurantPaymentFormComponent
} from "../../add-restaurant/restaurant-payment-form/restaurant-payment-form.component";
import {environment} from "../../../../../environments/environment";
import {RestaurantAddress} from "../../../../interfaces/restaurant-address";
import {Delivery, DeliveryHour} from "../../../../interfaces/delivery.interface";
import {PaymentMethod} from "../../../../../../../../WebstauratorApp/src/interface/paymentMethod";
import {Menu} from "../../../../interfaces/menu";
import {LanguageService} from "../../../../services/language.service";
import {PaymentMethodsService} from "../../../../services/payment-methods.service";
import {RestaurantService} from "../../../../services/restaurant.service";
import {AddRestaurant, Restaurant} from "../../../../interfaces/restaurant";
import {LanguageTranslations} from "../../../../interfaces/language.interface";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-edit-restaurant',
  standalone: true,
  imports: [
    FormsModule,
    MenuFormComponent,
    RestaurantAddressFormComponent,
    RestaurantDataFormComponent,
    RestaurantDeliveryDataFormComponent,
    RestaurantDeliveryHoursFormComponent,
    RestaurantPaymentFormComponent,
    ReactiveFormsModule
  ],
  templateUrl: './edit-restaurant.component.html',
  styleUrl: './edit-restaurant.component.css'
})
export class EditRestaurantComponent implements OnInit {
  background = environment.api + '/img/AddRestaurant.webp';
  editForm: FormGroup;
  restaurantAddress: RestaurantAddress = {} as RestaurantAddress;
  delivery: Delivery = {} as Delivery;
  deliveryHours: DeliveryHour[] = [];
  paymentMethods: PaymentMethod[] = [];
  menuItems: Menu[] = [];
  restaurantId: number = 0;
  restaurant: AddRestaurant = {} as AddRestaurant;


  constructor(private languageService: LanguageService,
              private fb: FormBuilder,
              private paymentMethodsService: PaymentMethodsService,
              private route: ActivatedRoute,
              private router: Router,
              private restaurantService: RestaurantService) {
    this.editForm = this.fb.group({
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
      const paymentMethodsArray = this.editForm.get('paymentMethods') as FormArray;

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

    this.route.paramMap.subscribe(params => {
      const id = params.get('restaurantId')!;
      this.restaurantId = parseInt(id);
      this.getRestaurantData(this.restaurantId);
    });
  }
  getRestaurantData(id: number) {
    this.restaurantService.getRestaurantForEdit(id).subscribe(restaurant => {
      this.restaurant = restaurant;
      console.log('Restaurant data:', this.restaurant); // Debugging

      this.editForm.patchValue({
        restaurantName: restaurant.name,
        category: restaurant.category,
        imageUrl: restaurant.imageUrl,
        logoUrl: restaurant.logoUrl,
        street: restaurant.restaurantAddress.street,
        flatNumber: restaurant.restaurantAddress.flatNumber,
        city: restaurant.restaurantAddress.city,
        zipCode: restaurant.restaurantAddress.zipCode,
        deliveryMaxTime: restaurant.delivery.deliveryMaxTime,
        deliveryMinTime: restaurant.delivery.deliveryMinTime,
        deliveryPrice: restaurant.delivery.deliveryPrice,
        minimumPrice: restaurant.delivery.minimumPrice,
        pickupTime: restaurant.delivery.pickupTime,
        mondayOpen: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 1)?.openTime,
        mondayClose: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 1)?.closeTime,
        tuesdayOpen: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 2)?.openTime,
        tuesdayClose: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 2)?.closeTime,
        wednesdayOpen: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 3)?.openTime,
        wednesdayClose: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 3)?.closeTime,
        thursdayOpen: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 4)?.openTime,
        thursdayClose: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 4)?.closeTime,
        fridayOpen: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 5)?.openTime,
        fridayClose: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 5)?.closeTime,
        saturdayOpen: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 6)?.openTime,
        saturdayClose: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 6)?.closeTime,
        sundayOpen: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 0)?.openTime,
        sundayClose: restaurant.deliveryHours.find(dh => dh.dayOfWeek === 0)?.closeTime,
      });

      const paymentMethodsArray = this.editForm.get('paymentMethods') as FormArray;
      restaurant.paymentMethods.forEach(paymentMethod => {
        const index = this.paymentMethods.findIndex(pm => pm.method === paymentMethod);
        if (index !== -1) {
          paymentMethodsArray.at(index).patchValue({ selected: true });
        }
      });

      this.menuItems = restaurant.menu;
    });
  }

  emitDaysOfWeek(deliveryHours: DeliveryHour[]) {
    this.deliveryHours = deliveryHours;
  }

  updateMenuItems(menuItems: Menu[]) {
    this.menuItems = menuItems;
    console.log('Updated menu items:', this.menuItems);
  }


  updateRestaurant() {

    if (this.editForm.valid) {
      const formData = this.editForm.value;

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
      console.log(requestPayload);
      this.restaurantService.updateRestaurant(this.restaurantId,requestPayload);
      this.router.navigate(['/all-restaurants']);
    } else {
      console.log('Form is invalid');
    }
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }
}
