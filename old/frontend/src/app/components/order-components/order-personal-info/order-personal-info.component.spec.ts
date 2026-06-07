import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderPersonalInfoComponent } from './order-personal-info.component';

describe('OrderPersonalInfoComponent', () => {
  let component: OrderPersonalInfoComponent;
  let fixture: ComponentFixture<OrderPersonalInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderPersonalInfoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderPersonalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
