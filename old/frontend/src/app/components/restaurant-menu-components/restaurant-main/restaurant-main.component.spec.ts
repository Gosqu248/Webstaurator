import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantMainComponent } from './restaurant-main.component';

describe('RestaurantMainComponent', () => {
  let component: RestaurantMainComponent;
  let fixture: ComponentFixture<RestaurantMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantMainComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
