import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantMenuComponent } from './restaurant-menu.component';

describe('RestaurantMenuComponent', () => {
  let component: RestaurantMenuComponent;
  let fixture: ComponentFixture<RestaurantMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantMenuComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
