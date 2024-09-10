import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuChangePasswordComponent } from './menu-change-password.component';

describe('MenuChangePasswordComponent', () => {
  let component: MenuChangePasswordComponent;
  let fixture: ComponentFixture<MenuChangePasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuChangePasswordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuChangePasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
