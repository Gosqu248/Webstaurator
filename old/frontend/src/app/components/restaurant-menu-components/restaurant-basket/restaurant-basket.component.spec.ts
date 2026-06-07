import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantBasketComponent } from './restaurant-basket.component';

describe('RestaurantBasketComponent', () => {
  let component: RestaurantBasketComponent;
  let fixture: ComponentFixture<RestaurantBasketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantBasketComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantBasketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
