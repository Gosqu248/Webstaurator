import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantDeliveryHoursFormComponent } from './restaurant-delivery-hours-form.component';

describe('RestaurantDeliveryHoursFormComponent', () => {
  let component: RestaurantDeliveryHoursFormComponent;
  let fixture: ComponentFixture<RestaurantDeliveryHoursFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantDeliveryHoursFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantDeliveryHoursFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
