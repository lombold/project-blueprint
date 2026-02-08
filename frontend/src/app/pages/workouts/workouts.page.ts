import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Workout } from '../../models/workout.model';
import { User } from '../../models/user.model';

/**
 * Workouts page component - Track workouts and exercises
 */
@Component({
  selector: 'app-workouts',
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="min-h-screen bg-gray-50 p-8">
      <div class="max-w-6xl mx-auto">
        <div class="flex justify-between items-center mb-8">
          <h1 class="text-4xl font-bold text-gray-900">Workouts</h1>
          <button
            (click)="showForm()"
            class="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-semibold"
          >
            + New Workout
          </button>
        </div>

        @if (isFormOpen()) {
          <div class="bg-white rounded-lg shadow-lg p-6 mb-8">
            <h2 class="text-2xl font-bold mb-6 text-gray-900">Create New Workout</h2>
            <form [formGroup]="form" (ngSubmit)="createWorkout()">
              <div class="space-y-4">
                <select
                  formControlName="userId"
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  required
                >
                  <option [ngValue]="null">Select User</option>
                  @for (user of users(); track user.id) {
                    <option [ngValue]="user.id">
                      {{ user.username }}
                    </option>
                  }
                </select>
                <input
                  formControlName="name"
                  placeholder="Workout Name"
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  required
                />
                <textarea
                  formControlName="description"
                  placeholder="Description"
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                ></textarea>
                <input
                  formControlName="durationMinutes"
                  type="number"
                  placeholder="Duration (minutes)"
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                />
                <div class="flex gap-4">
                  <button
                    type="submit"
                    class="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-semibold"
                  >
                    Create
                  </button>
                  <button
                    type="button"
                    (click)="cancelForm()"
                    class="px-6 py-3 bg-gray-300 text-gray-800 rounded-lg hover:bg-gray-400 transition-colors font-semibold"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </form>
          </div>
        }

        @if (workouts().length > 0) {
          <div class="space-y-6">
            @for (workout of workouts(); track workout.id) {
              <div class="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow">
                <div class="flex justify-between items-start mb-4">
                  <div>
                    <h3 class="text-2xl font-bold text-gray-900">{{ workout.name }}</h3>
                    <p class="text-gray-600 mt-1">{{ workout.description }}</p>
                  </div>
                  <button
                    (click)="deleteWorkout(workout.id)"
                    class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                  >
                    Delete
                  </button>
                </div>
                <div class="flex gap-6 text-gray-700">
                  <div>
                    <span class="font-semibold">User:</span> {{ getUserName(workout.userId) }}
                  </div>
                  <div>
                    <span class="font-semibold">Duration:</span> {{ workout.durationMinutes || 0 }} min
                  </div>
                  <div>
                    <span class="font-semibold">Exercises:</span> {{ workout.exerciseCount || 0 }}
                  </div>
                </div>
              </div>
            }
          </div>
        } @else {
          <div class="text-center py-12">
            <p class="text-gray-600 text-lg">No workouts yet. Create one to get started!</p>
          </div>
        }
      </div>
    </div>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkoutsPage implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly formBuilder = inject(FormBuilder);

  workouts = signal<Workout[]>([]);
  users = signal<User[]>([]);
  isFormOpen = signal(false);

  form = this.formBuilder.group({
    userId: this.formBuilder.control<number | null>(null, { validators: [Validators.required] }),
    name: this.formBuilder.nonNullable.control('', { validators: [Validators.required] }),
    description: this.formBuilder.nonNullable.control(''),
    durationMinutes: this.formBuilder.nonNullable.control(0)
  });

  ngOnInit(): void {
    this.loadUsers();
    this.loadWorkouts();
  }

  showForm(): void {
    this.isFormOpen.set(true);
  }

  cancelForm(): void {
    this.isFormOpen.set(false);
    this.form.reset({
      userId: null,
      name: '',
      description: '',
      durationMinutes: 0
    });
  }

  createWorkout(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.getRawValue();
    const userId = formValue.userId;

    if (userId === null) {
      return;
    }

    const newWorkout: Workout = {
      userId,
      name: formValue.name,
      description: formValue.description,
      durationMinutes: formValue.durationMinutes,
    };

    this.apiService.createWorkout(newWorkout).subscribe({
      next: () => {
        this.loadWorkouts();
        this.cancelForm();
      },
      error: (err) => console.error('Error creating workout:', err)
    });
  }

  deleteWorkout(id: any): void {
    if (id && confirm('Are you sure?')) {
      this.apiService.deleteWorkout(id).subscribe({
        next: () => this.loadWorkouts(),
        error: (err) => console.error('Error deleting workout:', err)
      });
    }
  }

  private loadWorkouts(): void {
    this.apiService.getWorkouts().subscribe({
      next: (workouts) => this.workouts.set(workouts),
      error: (err) => console.error('Error loading workouts:', err)
    });
  }

  private loadUsers(): void {
    this.apiService.getUsers().subscribe({
      next: (users) => this.users.set(users),
      error: (err) => console.error('Error loading users:', err)
    });
  }

  getUserName(userId: number): string {
    const user = this.users().find(u => u.id === userId);
    return user ? user.username : 'Unknown User';
  }
}
