import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavMenuProfileComponent } from './nav-menu-profile.component';

describe('NavMenuProfileComponent', () => {
  let component: NavMenuProfileComponent;
  let fixture: ComponentFixture<NavMenuProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavMenuProfileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavMenuProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
