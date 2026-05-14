import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

import { AppNotification, NotificationType } from '@shared/models/notification';

@Component({
  selector: 'app-notification-center',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section
      class="fixed inset-0 z-[49] flex items-start justify-center px-6 py-6 pt-20 pointer-events-none"
      aria-label="Application notifications"
      aria-live="polite"
    >
      <div class="space-y-2">
        @for (notification of notifications(); track notification.id) {
          <article
            class="motion-safe:animate-slide-in-top flex items-center justify-between gap-3 rounded-md border px-4 py-3 text-sm shadow-sm pointer-events-auto"
            [class.bg-emerald-50]="notification.type === notificationType.Info"
            [class.border-emerald-200]="notification.type === notificationType.Info"
            [class.text-emerald-800]="notification.type === notificationType.Info"
            [class.bg-amber-50]="notification.type === notificationType.Warn"
            [class.border-amber-200]="notification.type === notificationType.Warn"
            [class.text-amber-900]="notification.type === notificationType.Warn"
            [class.bg-rose-50]="notification.type === notificationType.Error"
            [class.border-rose-200]="notification.type === notificationType.Error"
            [class.text-rose-900]="notification.type === notificationType.Error"
          >
            <div class="flex items-start gap-1 flex-col">
              <span class="font-bold">{{ notification.message }}</span>
              @if (notification.list) {
                <ul class="ml-2 list-disc">
                  @for (item of notification.list; track item) {
                    <li>{{ item }}</li>
                  }
                </ul>
              }
            </div>
            @if (notification.type !== notificationType.Info) {
              <button
                type="button"
                class="ml-2 inline-flex h-6 w-6 items-center justify-center rounded border border-current/30 text-base leading-none hover:bg-black/5"
                [attr.aria-label]="
                  'Dismiss ' + notification.type + ' notification: ' + notification.message
                "
                (click)="dismissed.emit(notification.id)"
              >
                &times;
              </button>
            }
          </article>
        }
      </div>
    </section>
  `,
})
export class NotificationCenterComponent {
  protected readonly notificationType = NotificationType;

  readonly notifications = input.required<readonly AppNotification[]>();
  readonly dismissed = output<string>();
}
