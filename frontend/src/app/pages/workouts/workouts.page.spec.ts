import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { WorkoutsPage } from './workouts.page';
import { ApiService } from '../../services/api.service';
import { User } from '../../models/user.model';
import { Workout } from '../../models/workout.model';

describe('WorkoutsPage', () => {
  it('renders users in the workout form select', async () => {
    const users: User[] = [
      { id: 1, username: 'alex', email: 'alex@example.com' },
      { id: 2, username: 'casey', email: 'casey@example.com' }
    ];

    const apiServiceMock: Partial<ApiService> = {
      getUsers: () => of(users),
      getWorkouts: () => of([]),
      createWorkout: () => of({ userId: 1, name: 'Test Workout' } as Workout),
      deleteWorkout: () => of(void 0)
    };

    await TestBed.configureTestingModule({
      imports: [WorkoutsPage],
      providers: [{ provide: ApiService, useValue: apiServiceMock }]
    }).compileComponents();

    const fixture = TestBed.createComponent(WorkoutsPage);
    fixture.componentInstance.showForm();
    fixture.detectChanges();
    await fixture.whenStable();

    const optionTexts = Array.from(
      fixture.nativeElement.querySelectorAll('select option')
    ).map((option) => (<HTMLOptionElement>option).textContent?.trim());

    expect(optionTexts).toContain('alex');
    expect(optionTexts).toContain('casey');
  });
});

