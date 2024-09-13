import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuAddressesComponent } from './menu-addresses.component';

describe('MenuAddressesComponent', () => {
  let component: MenuAddressesComponent;
  let fixture: ComponentFixture<MenuAddressesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuAddressesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuAddressesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
