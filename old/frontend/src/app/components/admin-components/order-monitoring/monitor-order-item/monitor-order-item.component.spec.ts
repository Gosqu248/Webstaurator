import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitorOrderItemComponent } from './monitor-order-item.component';

describe('MonitorOrderItemComponent', () => {
  let component: MonitorOrderItemComponent;
  let fixture: ComponentFixture<MonitorOrderItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitorOrderItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MonitorOrderItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
