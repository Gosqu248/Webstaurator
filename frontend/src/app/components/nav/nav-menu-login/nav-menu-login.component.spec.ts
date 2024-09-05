import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavMenuLoginComponent } from './nav-menu-login.component';

describe('NavMenuLoginComponent', () => {
  let component: NavMenuLoginComponent;
  let fixture: ComponentFixture<NavMenuLoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavMenuLoginComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavMenuLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
