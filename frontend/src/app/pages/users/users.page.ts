import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import {User, UsersService} from '../../core/api';
import {email, form, FormField, required} from '@angular/forms/signals';

/**
 * Users page component - Manage user profiles
 */
@Component({
  selector: 'app-users',
  imports: [FormField],
  changeDetection: ChangeDetectionStrategy.OnPush,
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

        @if (isFormOpen()) {
          <div class="bg-white rounded-lg shadow-lg p-6 mb-8">
            <h2 class="text-2xl font-bold mb-6 text-gray-900">Create New User</h2>
            <form (submit)="createUser($event)">
              <div class="space-y-4">
                <div>
                  <label class="sr-only" for="username">Username</label>
                  <input
                    id="username"
                    type="text"
                    [formField]="userForm.username"
                    placeholder="Username"
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  />
                  @if (userForm.username().touched() && userForm.username().invalid()) {
                    <p class="text-sm text-red-600 mt-2">{{ userForm.username().errors()[0]?.message }}</p>
                  }
                </div>
                <div>
                  <label class="sr-only" for="email">Email</label>
                  <input
                    id="email"
                    type="email"
                    [formField]="userForm.email"
                    placeholder="Email"
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  />
                  @if (userForm.email().touched() && userForm.email().invalid()) {
                    <p class="text-sm text-red-600 mt-2">{{ userForm.email().errors()[0]?.message }}</p>
                  }
                </div>
                <div class="flex gap-4">
                  <button
                    type="submit"
                    [disabled]="!canSubmit()"
                    class="px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors font-semibold disabled:opacity-60 disabled:cursor-not-allowed"
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

        @if (isLoading()) {
          <div class="text-center py-12" aria-live="polite">
            <p class="text-gray-600 text-lg">Loading users...</p>
          </div>
        } @else if (loadError()) {
          <div class="text-center py-12" aria-live="polite">
            <p class="text-red-700 text-lg">Could not load users. Please try again.</p>
          </div>
        } @else if (hasUsers()) {
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            @for (user of users(); track user.id ?? user.username) {
              <div class="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow">
                <h3 class="text-xl font-bold text-gray-900 mb-2">{{ user.username }}</h3>
                <p class="text-gray-600 mb-4">{{ user.email }}</p>
                <button
                  (click)="deleteUser(user.id)"
                  class="w-full px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                >
                  Delete
                </button>
              </div>
            }
          </div>
        } @else {
          <div class="text-center py-12">
            <p class="text-gray-600 text-lg">No users yet. Create one to get started!</p>
          </div>
        }
      </div>
    </div>
  `,
  styles: []
})
export class UsersPage {
  private readonly userService = inject(UsersService);
  readonly usersResource = rxResource({
    defaultValue: [] as User[],
    stream: () => this.userService.listUsers()
  });
  readonly users = computed(() => this.usersResource.hasValue() ? this.usersResource.value() : []);
  readonly isFormOpen = signal(false);
  readonly formModel = signal({
    username: '',
    email: ''
  });
  readonly userForm = form(this.formModel, (schema) => {
    required(schema.username, { message: 'Username is required.' });
    required(schema.email, { message: 'Email is required.' });
    email(schema.email, { message: 'Enter a valid email address.' });
  });
  readonly canSubmit = computed(() => this.userForm.username().valid() && this.userForm.email().valid());
  readonly isLoading = computed(() => this.usersResource.isLoading());
  readonly loadError = computed(() => this.usersResource.error());
  readonly hasUsers = computed(() => this.users().length > 0);

  showForm(): void {
    this.isFormOpen.set(true);
  }

  cancelForm(): void {
    this.isFormOpen.set(false);
    this.resetForm();
  }

  createUser(event: SubmitEvent): void {
    event.preventDefault();
    if (!this.canSubmit()) {
      return;
    }

    const newUser: User = {
      username: this.formModel().username,
      email: this.formModel().email
    };

    this.userService.createUser(newUser).subscribe({
      next: () => {
        this.usersResource.reload();
        this.cancelForm();
      },
      error: (err) => console.error('Error creating user:', err)
    });
  }

  deleteUser(id: number | undefined): void {
    if (id === undefined || !confirm('Are you sure?')) {
      return;
    }

    this.userService.deleteUser(id).subscribe({
      next: () => this.usersResource.reload(),
      error: (err) => console.error('Error deleting user:', err)
    });
  }

  private resetForm(): void {
    this.formModel.set({ username: '', email: '' });
  }
}
