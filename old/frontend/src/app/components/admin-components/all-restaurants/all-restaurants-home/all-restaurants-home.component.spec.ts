import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllRestaurantsHomeComponent } from './all-restaurants-home.component';

describe('AllRestaurantsHomeComponent', () => {
  let component: AllRestaurantsHomeComponent;
  let fixture: ComponentFixture<AllRestaurantsHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllRestaurantsHomeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllRestaurantsHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
