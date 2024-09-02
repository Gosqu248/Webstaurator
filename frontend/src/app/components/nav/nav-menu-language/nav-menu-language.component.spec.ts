import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavMenuLanguageComponent } from './nav-menu-language.component';

describe('NavMenuLanguageComponent', () => {
  let component: NavMenuLanguageComponent;
  let fixture: ComponentFixture<NavMenuLanguageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavMenuLanguageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavMenuLanguageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
