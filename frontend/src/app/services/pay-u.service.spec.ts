import { TestBed } from '@angular/core/testing';

import { PayUService } from './pay-u.service';

describe('PayUService', () => {
  let service: PayUService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PayUService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
