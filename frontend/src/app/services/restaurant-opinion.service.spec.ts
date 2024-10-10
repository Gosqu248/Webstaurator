import { TestBed } from '@angular/core/testing';

import { RestaurantOpinionService } from './restaurant-opinion.service';

describe('RestaurantOpinionService', () => {
  let service: RestaurantOpinionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RestaurantOpinionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
