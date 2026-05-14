import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { type User } from '@core/api';

@Component({
  selector: 'app-user-list',
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
      @for (user of users(); track user.id ?? user.username) {
        <article class="rounded-lg bg-white p-6 shadow-lg transition-shadow hover:shadow-xl">
          <h3 class="mb-2 text-xl font-bold text-gray-900">{{ user.username }}</h3>
          <p class="text-gray-600">{{ user.email }}</p>
        </article>
      }
    </div>
  `,
})
export class UserList {
  readonly users = input.required<User[] | undefined>();
}
