import { ChangeDetectionStrategy, Component, computed, output, signal } from '@angular/core';
import { email, form, FormField, required } from '@angular/forms/signals';
import { type User } from '@core/api';
import { Button } from '@shared/components/button/button';

@Component({
  selector: 'app-user-form',
  imports: [FormField, Button],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="w-lg rounded-lg bg-white p-6 shadow-lg">
      <h2 class="text-2xl font-bold text-gray-900">Create user</h2>
      <form class="mt-5 space-y-4" (submit)="submit($event)">
        <div>
          <label class="sr-only" for="username">Username</label>
          <input
            id="username"
            type="text"
            [formField]="form.username"
            placeholder="Username"
            class="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-transparent focus:ring-2 focus:ring-indigo-500"
          />
          @if (form.username().touched() && form.username().invalid()) {
            <p class="mt-2 text-sm text-red-600">
              {{ form.username().errors()[0]?.message }}
            </p>
          }
        </div>

        <div>
          <label class="sr-only" for="email">Email</label>
          <input
            id="email"
            type="email"
            [formField]="form.email"
            placeholder="Email"
            class="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-transparent focus:ring-2 focus:ring-indigo-500"
          />
          @if (form.email().touched() && form.email().invalid()) {
            <p class="mt-2 text-sm text-red-600">
              {{ form.email().errors()[0]?.message }}
            </p>
          }
        </div>

        <div class="flex items-center gap-4">
          <button app-button type="submit" [disabled]="!canSubmit()">Create</button>
          <button app-button type="button" variant="secondary" (click)="cancel.emit()">
            Cancel
          </button>
        </div>
      </form>
    </section>
  `,
})
export class UserForm {
  protected readonly submitForm = output<User>();
  protected readonly cancel = output<void>();

  protected readonly model = signal<User>({
    username: '',
    email: '',
  });
  protected readonly form = form(this.model, (schema) => {
    required(schema.username, { message: 'Username is required.' });
    required(schema.email, { message: 'Email is required.' });
    email(schema.email, { message: 'Enter a valid email address.' });
  });
  protected readonly canSubmit = computed(() => this.form().valid());

  reset(): void {
    this.form().reset();
  }

  protected submit(event: SubmitEvent): void {
    event.preventDefault();
    if (!this.canSubmit()) {
      return;
    }

    this.submitForm.emit(this.model());
  }
}
