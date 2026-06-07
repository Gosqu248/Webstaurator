import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderMenuItemComponent } from './order-menu-item.component';

describe('OrderMenuItemComponent', () => {
  let component: OrderMenuItemComponent;
  let fixture: ComponentFixture<OrderMenuItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderMenuItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderMenuItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
