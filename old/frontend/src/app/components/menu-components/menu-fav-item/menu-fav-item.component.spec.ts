import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuFavItemComponent } from './menu-fav-item.component';

describe('MenuFavItemComponent', () => {
  let component: MenuFavItemComponent;
  let fixture: ComponentFixture<MenuFavItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuFavItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuFavItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
