import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuAddressesItemComponent } from './menu-addresses-item.component';

describe('MenuAddressesItemComponent', () => {
  let component: MenuAddressesItemComponent;
  let fixture: ComponentFixture<MenuAddressesItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuAddressesItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuAddressesItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
