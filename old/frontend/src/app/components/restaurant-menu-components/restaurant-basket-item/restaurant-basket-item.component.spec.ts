import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantBasketItemComponent } from './restaurant-basket-item.component';

describe('RestaurantBasketItemComponent', () => {
  let component: RestaurantBasketItemComponent;
  let fixture: ComponentFixture<RestaurantBasketItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantBasketItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantBasketItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
