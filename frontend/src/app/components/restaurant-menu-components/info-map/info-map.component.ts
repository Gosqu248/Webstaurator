import {Component, Input, OnInit} from '@angular/core';
import {RestaurantAddress} from "../../../interfaces/restaurant-address";
import * as L from 'leaflet';
import {RestaurantService} from "../../../services/api/restaurant.service";
import {RestaurantAddressService} from "../../../services/api/restaurant-address.service";

@Component({
    selector: 'app-info-map',
    imports: [],
    templateUrl: './info-map.component.html',
    styleUrl: './info-map.component.css'
})
export class InfoMapComponent implements OnInit{
  @Input() restaurantId!: number;

  address: RestaurantAddress = {} as RestaurantAddress;
  private map!: L.Map;

  constructor(private restaurantService: RestaurantService,
              private restaurantAddressService: RestaurantAddressService) {}

  ngOnInit() {
    this.getRestaurantAddress();
  }

  getRestaurantAddress(): void {
    this.restaurantAddressService.getRestaurantAddress(this.restaurantId).subscribe((data) => {
      if (typeof window !== 'undefined') {
        this.initializeMap(data);
      }
      this.address = data
    });
  }

  private initializeMap(address: RestaurantAddress): void {

    this.map = L.map('map', {
      dragging: false,
      touchZoom: false,
      scrollWheelZoom: false,
      doubleClickZoom: false,
      boxZoom: false,
      keyboard: false
    })
      .setView([address.latitude!, address.longitude!], 16.5);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);


    this.restaurantService.getLogo(this.restaurantId).subscribe(logo => {
      L.marker([address.latitude!, address.longitude!],
        {icon: this.createLogoIcon(logo)})
        .addTo(this.map)
    });




  }


  private createLogoIcon(logo:string) {
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
      iconAnchor: [25, 40],
    })
  }


}
