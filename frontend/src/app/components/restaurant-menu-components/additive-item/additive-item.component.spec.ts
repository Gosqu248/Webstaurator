import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdditiveItemComponent } from './additive-item.component';

describe('AdditiveItemComponent', () => {
  let component: AdditiveItemComponent;
  let fixture: ComponentFixture<AdditiveItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdditiveItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdditiveItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
