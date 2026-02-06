import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { User } from '../../models/user.model';

/**
 * Users page component - Manage user profiles
 */
@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, FormsModule],
  template: `
    <div class="min-h-screen bg-gray-50 p-8">
      <div class="max-w-6xl mx-auto">
        <div class="flex justify-between items-center mb-8">
          <h1 class="text-4xl font-bold text-gray-900">Users</h1>
          <button
            (click)="showForm()"
            class="px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors font-semibold"
          >
            + New User
          </button>
        </div>

        <!-- User Form -->
        <div *ngIf="isFormOpen()" class="bg-white rounded-lg shadow-lg p-6 mb-8">
          <h2 class="text-2xl font-bold mb-6 text-gray-900">Create New User</h2>
          <form (ngSubmit)="createUser()">
            <div class="space-y-4">
              <input
                [(ngModel)]="formData.username"
                name="username"
                placeholder="Username"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                required
              />
              <input
                [(ngModel)]="formData.email"
                name="email"
                type="email"
                placeholder="Email"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                required
              />
              <div class="flex gap-4">
                <button
                  type="submit"
                  class="px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors font-semibold"
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

        <!-- Users List -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div *ngFor="let user of users()" class="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow">
            <h3 class="text-xl font-bold text-gray-900 mb-2">{{ user.username }}</h3>
            <p class="text-gray-600 mb-4">{{ user.email }}</p>
            <button
              (click)="deleteUser(user.id)"
              class="w-full px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
            >
              Delete
            </button>
          </div>
        </div>

        <div *ngIf="users().length === 0" class="text-center py-12">
          <p class="text-gray-600 text-lg">No users yet. Create one to get started!</p>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class UsersPage implements OnInit {
  users = signal<User[]>([]);
  isFormOpen = signal(false);
  formData = {
    username: '',
    email: ''
  };

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  showForm(): void {
    this.isFormOpen.set(true);
  }

  cancelForm(): void {
    this.isFormOpen.set(false);
    this.formData = { username: '', email: '' };
  }

  createUser(): void {
    const newUser: User = {
      username: this.formData.username,
      email: this.formData.email
    };

    this.apiService.createUser(newUser).subscribe({
      next: () => {
        this.loadUsers();
        this.cancelForm();
      },
      error: (err) => console.error('Error creating user:', err)
    });
  }

  deleteUser(id: any): void {
    if (id && confirm('Are you sure?')) {
      this.apiService.deleteUser(id).subscribe({
        next: () => this.loadUsers(),
        error: (err) => console.error('Error deleting user:', err)
      });
    }
  }

  private loadUsers(): void {
    this.apiService.getUsers().subscribe({
      next: (users) => this.users.set(users),
      error: (err) => console.error('Error loading users:', err)
    });
  }
}
