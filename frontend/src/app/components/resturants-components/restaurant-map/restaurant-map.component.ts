import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {SearchedRestaurant} from "../../../interfaces/searched-restaurant";
import * as L from 'leaflet';
import { Marker} from 'leaflet';
import {AddressSuggestionsService} from "../../../services/address-suggestions.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {Subscription} from 'rxjs';
import {RestaurantService} from "../../../services/restaurant.service";

@Component({
  selector: 'app-restaurant-map',
  standalone: true,
  imports: [],
  templateUrl: './restaurant-map.component.html',
  styleUrl: './restaurant-map.component.css'
})
export class RestaurantMapComponent implements OnInit, OnDestroy {
  @Input() restaurants!: SearchedRestaurant[];
  @Input() address!: string;
  private map!: L.Map;
  private markers: L.Marker[] = [];
  private languageSubscription!: Subscription;

  constructor(private addressSuggestionsService: AddressSuggestionsService,
              private restaurantService: RestaurantService,
              private languageService: LanguageService) {
  }

  ngOnInit() {
    this.getCoordinates();
    this.languageSubscription = this.languageService.languageChangeSubject.subscribe(() => {
      this.updatePopups();
    });
  }

  ngOnDestroy() {
    if (this.map) {
      this.map.remove();
    }
    if (this.languageSubscription) {
      this.languageSubscription.unsubscribe();
    }
  }

  private getCoordinates(): void {
    this.addressSuggestionsService.getCoordinates(this.address).subscribe(coordinates => {
      this.initializeMap(coordinates.lat, coordinates.lon);
    });
  }

  private initializeMap(lat: number, lon: number): void {
    this.map = L.map('map').setView([lat, lon], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    const locationIcon = L.divIcon({
      html: `
        <div style="
          background-color: #2196F3;
          width: 40px;
          height: 40px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-weight: bold;
          border: 2px solid white;
          box-shadow: 0 2px 5px rgba(0,0,0,0.9);
        ">
          <i class="fa-solid fa-location-dot"></i>
        </div>
      `,
      className: 'location-marker',
      iconAnchor: [15, 15],
      popupAnchor: [0, -20]
    });

    const marker = L.marker([lat, lon], { icon: locationIcon }).addTo(this.map);
    marker.bindPopup(this.getTranslation('yourLocation'));

    this.addMarkers(marker);
    this.centerMapOnMarkers();
  }

  private addMarkers(loc: Marker): void {
    this.markers.forEach(marker => marker.remove());
    this.markers = [];

    this.restaurants.forEach(restaurant => {
      this.restaurantService.getLogo(restaurant.restaurantId).subscribe(logo => {
      const marker = L.marker([restaurant.lat, restaurant.lon], {
        icon: this.createCustomIcon(logo)
      });


      const popupContent = this.createPopupContent(restaurant);
      marker.bindPopup(popupContent);

      marker.addTo(this.map);
      this.markers.push(loc);
      this.markers.push(marker);
    });
    });
  }

  private createCustomIcon(logo: string) {
    return L.divIcon({
      className: 'custom-marker',
      html: `

        <img src="${logo}" alt="logo" style="
                  width: 60px;
                  height: 60px;
                  border-radius: 50%;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  color: grey;
                  font-weight: bold;
                  background: linear-gradient(365deg, whitesmoke,  #ffc98f, orange);
                  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.9);
              "/>
      `,
      iconAnchor: [5, 5]
    });
  }

  private createPopupContent(restaurant: SearchedRestaurant): string {
    const rating = restaurant.rating.toFixed(1);

    return `
      <div class="restaurant-popup">
        <h3>${restaurant.name}</h3>
        <div class="popup-details">
          <p><strong>Kategoria:</strong> ${restaurant.category}</p>
          <p><strong>Ocena:</strong> ${rating} ⭐</p>
          <p><strong>Odległość:</strong> ${restaurant.distance} km</p>
          <p><strong>Koszt dostawy:</strong> ${restaurant.deliveryPrice} zł</p>
          <p><strong>Odbiór własny:</strong> ${restaurant.pickup ? 'Tak' : 'Nie'}</p>
        </div>
      </div>
    `;
  }

  private centerMapOnMarkers(): void {
    if (this.markers.length > 0) {
      const group = L.featureGroup(this.markers);
      this.map.fitBounds(group.getBounds().pad(0.1));
    }
  }

  private updatePopups(): void {
    this.markers.forEach(marker => {
      const popup = marker.getPopup();
      if (popup) {
        popup.setContent(this.getTranslation('yourLocation'));
      }
    });
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k) {
    return this.languageService.getTranslation(key);
  }
}
