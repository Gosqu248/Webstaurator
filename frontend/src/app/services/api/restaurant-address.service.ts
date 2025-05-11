import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { forkJoin, map, Observable, of, switchMap } from "rxjs";
import { RestaurantAddress } from "../../interfaces/restaurant-address";
import { SearchedRestaurant } from "../../interfaces/searched-restaurant";
import { Coordinates } from "../../interfaces/coordinates";
import {SearchedRestaurantsService} from "../state/searched-restaurant.service";
import {DeliveryService} from "./delivery.service";
import {PaymentMethodsService} from "./payment-methods.service";
import {RestaurantService} from "./restaurant.service";

@Injectable({
  providedIn: 'root'
})
export class RestaurantAddressService {
  private apiUrl = environment.api + '/api/restaurantAddress';

  constructor(
    private http: HttpClient,
    private searchedRestaurantsService: SearchedRestaurantsService,
    private deliveryService: DeliveryService,
    private restaurantService: RestaurantService,
    private paymentMethodsService: PaymentMethodsService
  ) {}

  getRestaurantAddress(id: number): Observable<RestaurantAddress> {
    return this.http.get<RestaurantAddress>(`${this.apiUrl}/get?restaurantId=${id}`);
  }

  searchRestaurant(address: string): Observable<SearchedRestaurant[]> {
    return this.http.get<SearchedRestaurant[]>(`${this.apiUrl}/search?address=${address}`)
      .pipe(
        switchMap(restaurants => {
          if (restaurants.length === 0) {
            return of([])
          }

          const restaurantWithData = restaurants.map(restaurant => {
            const restaurantObs = this.restaurantService.getRestaurantById(restaurant.restaurantId);
            const addressObs = this.getRestaurantAddress(restaurant.restaurantId);
            const deliveryObs = this.deliveryService.getDelivery(restaurant.restaurantId);
            const deliveryTimeObs = this.deliveryService.getDeliveryTIme(restaurant.restaurantId);
            const paymentObs = this.paymentMethodsService.getRestaurantPaymentMethods(restaurant.restaurantId).pipe(
              map(paymentMethods => paymentMethods.map(payment => ({
                  ...payment,
                  image: environment.api + payment.image
              })))
            );

            return forkJoin({
              restaurant: restaurantObs,
              address: addressObs,
              delivery: deliveryObs,
              deliveryTime: deliveryTimeObs,
              paymentMethods: paymentObs
            }).pipe(
              map(data => ({
                ...restaurant,
                restaurant: data.restaurant,
                restaurantAddress: data.address,
                delivery: data.delivery,
                deliveryHours: data.deliveryTime,
                paymentMethods: data.paymentMethods
              }))
            );
          });

          return forkJoin(restaurantWithData);
        }),
        switchMap(restaurantsWithAddresses => {
          this.searchedRestaurantsService.setSearchedRestaurants(restaurantsWithAddresses);
          return of(restaurantsWithAddresses);
        })
      );
  }

  getCoordinates(id: number): Observable<Coordinates> {
    return this.http.get<Coordinates>(`${this.apiUrl}/getCoordinates?restaurantId=${id}`);
  }
}
