import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseHourDialogComponent } from './choose-hour-dialog.component';

describe('ChooseHourDialogComponent', () => {
  let component: ChooseHourDialogComponent;
  let fixture: ComponentFixture<ChooseHourDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChooseHourDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChooseHourDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
