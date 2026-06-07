import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeSecondSectionComponent } from './home-second-section.component';

describe('HomeSecondSectionComponent', () => {
  let component: HomeSecondSectionComponent;
  let fixture: ComponentFixture<HomeSecondSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeSecondSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeSecondSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
