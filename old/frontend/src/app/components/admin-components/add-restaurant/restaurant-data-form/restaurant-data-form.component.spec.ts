import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantDataFormComponent } from './restaurant-data-form.component';

describe('RestaurantDataFormComponent', () => {
  let component: RestaurantDataFormComponent;
  let fixture: ComponentFixture<RestaurantDataFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantDataFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
