import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuFavouriteComponent } from './menu-favourite.component';

describe('MenuFavouriteComponent', () => {
  let component: MenuFavouriteComponent;
  let fixture: ComponentFixture<MenuFavouriteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuFavouriteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuFavouriteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
