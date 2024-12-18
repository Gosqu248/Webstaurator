import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantPaymentFormComponent } from './restaurant-payment-form.component';

describe('RestaurantPaymentFormComponent', () => {
  let component: RestaurantPaymentFormComponent;
  let fixture: ComponentFixture<RestaurantPaymentFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantPaymentFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantPaymentFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
