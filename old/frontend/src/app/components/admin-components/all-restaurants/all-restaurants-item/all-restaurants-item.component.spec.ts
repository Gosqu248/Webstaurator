import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllRestaurantsItemComponent } from './all-restaurants-item.component';

describe('AllRestaurantsItemComponent', () => {
  let component: AllRestaurantsItemComponent;
  let fixture: ComponentFixture<AllRestaurantsItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllRestaurantsItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllRestaurantsItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
