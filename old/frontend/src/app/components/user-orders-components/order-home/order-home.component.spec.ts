import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderHomeComponent } from './order-home.component';

describe('OrderHomeComponent', () => {
  let component: OrderHomeComponent;
  let fixture: ComponentFixture<OrderHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderHomeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
