import { Dialog } from '@angular/cdk/dialog';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject } from '@angular/core';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UsersService } from '@core/api';
import { Button } from '@shared/components/button/button';
import { Page } from '@shared/components/page/page';
import { PageAction } from '@shared/components/page/page-action';
import { CreateUserDialog } from './create-user-dialog/create-user-dialog';
import { UserList } from './user.list';

@Component({
  selector: 'app-users',
  imports: [UserList, Page, PageAction, Button],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page>
      <app-page-action>
        <button app-button (click)="openCreateDialog()">+ New User</button>
      </app-page-action>

      @if (isLoading()) {
        <div class="py-12 text-center" aria-live="polite">
          <p class="text-lg text-gray-600">Loading users...</p>
        </div>
      } @else if (loadError()) {
        <div class="py-12 text-center" aria-live="polite">
          <p class="text-lg text-red-700">Could not load users. Please try again.</p>
        </div>
      } @else if (hasUsers()) {
        <app-user-list [users]="users.value()" />
      } @else {
        <div class="py-12 text-center">
          <p class="text-lg text-gray-600">No users yet. Create one to get started!</p>
        </div>
      }
    </app-page>
  `,
})
export class UsersPage {
  private readonly usersService = inject(UsersService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly dialog = inject(Dialog);

  protected readonly users = rxResource({
    stream: () => this.usersService.listUsers(),
  });
  protected readonly isLoading = computed(() => this.users.isLoading());
  protected readonly loadError = computed(() => this.users.error());
  protected readonly hasUsers = computed(() => (this.users.value()?.length ?? 0) > 0);

  protected openCreateDialog(): void {
    this.dialog
      .open(CreateUserDialog)
      .closed.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.users.reload();
      });
  }
}
