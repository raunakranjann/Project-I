import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandlordDashboard } from './landlord-dashboard';

describe('LandlordDashboard', () => {
  let component: LandlordDashboard;
  let fixture: ComponentFixture<LandlordDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandlordDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandlordDashboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
