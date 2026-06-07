import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderDeliveryItemComponent } from './order-delivery-item.component';

describe('OrderDeliveryItemComponent', () => {
  let component: OrderDeliveryItemComponent;
  let fixture: ComponentFixture<OrderDeliveryItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderDeliveryItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderDeliveryItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
