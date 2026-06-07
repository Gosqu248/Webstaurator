import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitorHomeComponent } from './monitor-home.component';

describe('MonitorHomeComponent', () => {
  let component: MonitorHomeComponent;
  let fixture: ComponentFixture<MonitorHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitorHomeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MonitorHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
