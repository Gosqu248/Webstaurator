import { TestBed } from '@angular/core/testing';

import { AddressSuggestionsService } from './address-suggestions.service';

describe('AddressSuggestionsService', () => {
  let service: AddressSuggestionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AddressSuggestionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
