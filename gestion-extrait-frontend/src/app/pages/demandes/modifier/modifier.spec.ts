import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Modifier } from './modifier';

describe('Modifier', () => {
  let component: Modifier;
  let fixture: ComponentFixture<Modifier>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Modifier],
    }).compileComponents();

    fixture = TestBed.createComponent(Modifier);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
