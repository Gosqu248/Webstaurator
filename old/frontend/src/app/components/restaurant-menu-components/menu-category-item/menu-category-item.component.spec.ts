import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuCategoryItemComponent } from './menu-category-item.component';

describe('MenuCategoryItemComponent', () => {
  let component: MenuCategoryItemComponent;
  let fixture: ComponentFixture<MenuCategoryItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuCategoryItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuCategoryItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
