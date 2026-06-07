import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantCategoryComponent } from './restaurant-category.component';

describe('ResturantCategoryComponent', () => {
  let component: RestaurantCategoryComponent;
  let fixture: ComponentFixture<RestaurantCategoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantCategoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantCategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
