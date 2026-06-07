import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeFirstSectionComponent } from './home-first-section.component';

describe('HomeFirstSectionComponent', () => {
  let component: HomeFirstSectionComponent;
  let fixture: ComponentFixture<HomeFirstSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeFirstSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeFirstSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
