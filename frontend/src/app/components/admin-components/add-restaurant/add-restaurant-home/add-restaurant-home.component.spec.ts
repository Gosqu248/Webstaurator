import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRestaurantHomeComponent } from './add-restaurant-home.component';

describe('AddRestaurantHomeComponent', () => {
  let component: AddRestaurantHomeComponent;
  let fixture: ComponentFixture<AddRestaurantHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddRestaurantHomeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddRestaurantHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
