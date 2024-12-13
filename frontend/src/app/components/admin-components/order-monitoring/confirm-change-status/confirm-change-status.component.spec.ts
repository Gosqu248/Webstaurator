import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmChangeStatusComponent } from './confirm-change-status.component';

describe('ConfirmChangeStatusComponent', () => {
  let component: ConfirmChangeStatusComponent;
  let fixture: ComponentFixture<ConfirmChangeStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmChangeStatusComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmChangeStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
