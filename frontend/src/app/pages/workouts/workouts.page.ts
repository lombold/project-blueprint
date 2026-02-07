import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Workout } from '../../models/workout.model';
import { User } from '../../models/user.model';

/**
 * Workouts page component - Track workouts and exercises
 */
@Component({
  selector: 'app-workouts',
  standalone: true,
  imports: [CommonModule, FormsModule],
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

        <!-- Workout Form -->
        <div *ngIf="isFormOpen()" class="bg-white rounded-lg shadow-lg p-6 mb-8">
          <h2 class="text-2xl font-bold mb-6 text-gray-900">Create New Workout</h2>
          <form (ngSubmit)="createWorkout()">
            <div class="space-y-4">
              <select
                [(ngModel)]="formData.userId"
                name="userId"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                required
              >
                <option value="">Select User</option>
                <option *ngFor="let user of users()" [value]="user.id">
                  {{ user.username }}
                </option>
              </select>
              <input
                [(ngModel)]="formData.name"
                name="name"
                placeholder="Workout Name"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                required
              />
              <textarea
                [(ngModel)]="formData.description"
                name="description"
                placeholder="Description"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
              ></textarea>
              <input
                [(ngModel)]="formData.durationMinutes"
                name="durationMinutes"
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

        <!-- Workouts List -->
        <div class="space-y-6">
          <div *ngFor="let workout of workouts()" class="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow">
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
        </div>

        <div *ngIf="workouts().length === 0" class="text-center py-12">
          <p class="text-gray-600 text-lg">No workouts yet. Create one to get started!</p>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class WorkoutsPage implements OnInit {
  workouts = signal<Workout[]>([]);
  users = signal<User[]>([]);
  isFormOpen = signal(false);
  formData = {
    userId: null as any,
    name: '',
    description: '',
    durationMinutes: 0,
    difficulty: ''
  };

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadWorkouts();
  }

  showForm(): void {
    this.isFormOpen.set(true);
  }

  cancelForm(): void {
    this.isFormOpen.set(false);
    this.formData = {
      userId: null,
      name: '',
      description: '',
      durationMinutes: 0,
      difficulty: ''
    };
  }

  createWorkout(): void {
    if (!this.formData.userId) {
      alert('Please select a user');
      return;
    }

    const newWorkout: Workout = {
      userId: this.formData.userId,
      name: this.formData.name,
      description: this.formData.description,
      durationMinutes: this.formData.durationMinutes,
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
