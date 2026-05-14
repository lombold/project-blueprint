import { DialogRef } from '@angular/cdk/dialog';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { type User, UsersService } from '@core/api';
import { NotificationService } from '@core/services/notification.service';
import { UserForm } from './user.form';

@Component({
  selector: 'app-create-user-dialog',
  imports: [UserForm],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<app-user-form (submitForm)="createUser($event)" (cancel)="dialogRef.close()" />`,
})
export class CreateUserDialog {
  protected readonly dialogRef = inject(DialogRef);
  private readonly usersService = inject(UsersService);
  private readonly notificationService = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly form = viewChild(UserForm);

  protected createUser(request: User): void {
    this.usersService
      .createUser(request)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user) => {
          this.form()?.reset();
          this.dialogRef.close(user);
          this.notificationService.info(`User "${user.username}" created successfully`);
        },
        error: (err) => console.error('Error creating user:', err),
      });
  }
}
