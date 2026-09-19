import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OpinionItemComponent } from './opinion-item.component';

describe('OpinionItemComponent', () => {
  let component: OpinionItemComponent;
  let fixture: ComponentFixture<OpinionItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OpinionItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
