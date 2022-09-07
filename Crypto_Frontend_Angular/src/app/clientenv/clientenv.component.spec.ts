import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientenvComponent } from './clientenv.component';

describe('ClientenvComponent', () => {
  let component: ClientenvComponent;
  let fixture: ComponentFixture<ClientenvComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClientenvComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientenvComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
