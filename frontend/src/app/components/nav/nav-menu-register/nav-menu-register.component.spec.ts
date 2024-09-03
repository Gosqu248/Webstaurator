import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavMenuRegisterComponent } from './nav-menu-register.component';

describe('NavMenuRegisterComponent', () => {
  let component: NavMenuRegisterComponent;
  let fixture: ComponentFixture<NavMenuRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavMenuRegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavMenuRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
