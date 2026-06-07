import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdditivesDialogComponent } from './additives-dialog.component';

describe('AdditivesDialogComponent', () => {
  let component: AdditivesDialogComponent;
  let fixture: ComponentFixture<AdditivesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdditivesDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdditivesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
