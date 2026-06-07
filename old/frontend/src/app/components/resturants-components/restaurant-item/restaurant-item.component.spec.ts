import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantItemComponent } from './restaurant-item.component';

describe('RestaurantItemComponent', () => {
  let component: RestaurantItemComponent;
  let fixture: ComponentFixture<RestaurantItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
