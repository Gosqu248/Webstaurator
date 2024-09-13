import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuAddAddressComponent } from './menu-add-address.component';

describe('MenuAddAddressComponent', () => {
  let component: MenuAddAddressComponent;
  let fixture: ComponentFixture<MenuAddAddressComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuAddAddressComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuAddAddressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
