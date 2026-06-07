import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderBasketComponent } from './order-basket.component';

describe('OrderBasketComponent', () => {
  let component: OrderBasketComponent;
  let fixture: ComponentFixture<OrderBasketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderBasketComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderBasketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
