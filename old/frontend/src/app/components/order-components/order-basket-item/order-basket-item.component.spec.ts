import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderBasketItemComponent } from './order-basket-item.component';

describe('OrderBasketItemComponent', () => {
  let component: OrderBasketItemComponent;
  let fixture: ComponentFixture<OrderBasketItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderBasketItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderBasketItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
