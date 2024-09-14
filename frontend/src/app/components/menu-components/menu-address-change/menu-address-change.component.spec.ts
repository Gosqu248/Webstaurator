import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuAddressChangeComponent } from './menu-address-change.component';

describe('MenuAddressChangeComponent', () => {
  let component: MenuAddressChangeComponent;
  let fixture: ComponentFixture<MenuAddressChangeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuAddressChangeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuAddressChangeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
