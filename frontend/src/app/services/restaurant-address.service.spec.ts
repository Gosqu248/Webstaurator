import { TestBed } from '@angular/core/testing';

import { RestaurantAddressService } from './restaurant-address.service';

describe('RestaurantAddressService', () => {
  let service: RestaurantAddressService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RestaurantAddressService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
