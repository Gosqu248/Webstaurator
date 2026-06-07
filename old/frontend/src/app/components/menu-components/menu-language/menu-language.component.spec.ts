import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuLanguageComponent } from './menu-language.component';

describe('MenuLanguageComponent', () => {
  let component: MenuLanguageComponent;
  let fixture: ComponentFixture<MenuLanguageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuLanguageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuLanguageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
