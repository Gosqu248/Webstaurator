import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantAddressFormComponent } from './restaurant-address-form.component';

describe('RestaurantAddressFormComponent', () => {
  let component: RestaurantAddressFormComponent;
  let fixture: ComponentFixture<RestaurantAddressFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantAddressFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantAddressFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
