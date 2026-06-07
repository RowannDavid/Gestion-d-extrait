import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Nouvelle } from './nouvelle';

describe('Nouvelle', () => {
  let component: Nouvelle;
  let fixture: ComponentFixture<Nouvelle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Nouvelle],
    }).compileComponents();

    fixture = TestBed.createComponent(Nouvelle);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
