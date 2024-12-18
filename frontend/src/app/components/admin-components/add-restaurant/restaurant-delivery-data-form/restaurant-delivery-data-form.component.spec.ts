import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantDeliveryDataFormComponent } from './restaurant-delivery-data-form.component';

describe('RestaurantDeliveryDataFormComponent', () => {
  let component: RestaurantDeliveryDataFormComponent;
  let fixture: ComponentFixture<RestaurantDeliveryDataFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantDeliveryDataFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantDeliveryDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
