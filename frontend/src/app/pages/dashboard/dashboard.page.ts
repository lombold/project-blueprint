import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

/**
 * Dashboard page component - Main landing page
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-8">
      <div class="max-w-6xl mx-auto">
        <div class="text-center mb-12">
          <h1 class="text-5xl font-bold text-gray-900 mb-4">Gym Buddy</h1>
          <p class="text-xl text-gray-600">Track your fitness journey and achieve your goals</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <!-- Stats Card 1 -->
          <div class="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow">
            <div class="text-indigo-600 text-4xl font-bold mb-2">{{ stats.totalUsers }}</div>
            <p class="text-gray-600 text-lg">Total Users</p>
          </div>

          <!-- Stats Card 2 -->
          <div class="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow">
            <div class="text-green-600 text-4xl font-bold mb-2">{{ stats.totalWorkouts }}</div>
            <p class="text-gray-600 text-lg">Workouts Logged</p>
          </div>

          <!-- Stats Card 3 -->
          <div class="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow">
            <div class="text-purple-600 text-4xl font-bold mb-2">{{ stats.totalExercises }}</div>
            <p class="text-gray-600 text-lg">Exercises Completed</p>
          </div>
        </div>

        <div class="mt-12 bg-white rounded-lg shadow-lg p-8">
          <h2 class="text-2xl font-bold text-gray-900 mb-4">Getting Started</h2>
          <ul class="space-y-3 text-gray-700">
            <li class="flex items-start">
              <span class="text-indigo-600 font-bold mr-3">→</span>
              <span>Create a user profile in the Users section</span>
            </li>
            <li class="flex items-start">
              <span class="text-indigo-600 font-bold mr-3">→</span>
              <span>Log your workouts with detailed exercises</span>
            </li>
            <li class="flex items-start">
              <span class="text-indigo-600 font-bold mr-3">→</span>
              <span>Track your progress and celebrate wins</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class DashboardPage implements OnInit {
  stats = {
    totalUsers: 0,
    totalWorkouts: 0,
    totalExercises: 0
  };

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadStats();
  }

  private loadStats(): void {
    // Load statistics from backend
    this.apiService.getUsers().subscribe({
      next: (users) => {
        this.stats.totalUsers = users.length;
      }
    });

    this.apiService.getWorkouts().subscribe({
      next: (workouts) => {
        this.stats.totalWorkouts = workouts.length;
        this.stats.totalExercises = workouts.reduce((acc, w) => acc + (w.exerciseCount || 0), 0);
      }
    });
  }
}
