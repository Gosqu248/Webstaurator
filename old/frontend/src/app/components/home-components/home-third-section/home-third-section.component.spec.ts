import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeThirdSectionComponent } from './home-third-section.component';

describe('HomeThirdSectionComponent', () => {
  let component: HomeThirdSectionComponent;
  let fixture: ComponentFixture<HomeThirdSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeThirdSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeThirdSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
