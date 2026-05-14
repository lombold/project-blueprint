import { Injectable, signal } from '@angular/core';

import { AppNotification, NotificationType } from '@shared/models/notification';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  readonly #notifications = signal<readonly AppNotification[]>([]);
  readonly notifications = this.#notifications.asReadonly();

  info(message: string, ...list: string[]): string {
    const id = this.add(NotificationType.Info, message, ...list);

    window.setTimeout(() => this.dismiss(id), 5000);

    return id;
  }

  warn(message: string, ...list: string[]): string {
    return this.add(NotificationType.Warn, message, ...list);
  }

  error(message: string, ...list: string[]): string {
    return this.add(NotificationType.Error, message, ...list);
  }

  dismiss(id: string): void {
    this.#notifications.update((notifications) =>
      notifications.filter((notification) => notification.id !== id),
    );
  }

  private add(type: NotificationType, message: string, ...list: string[]): string {
    const id = crypto.randomUUID();

    this.#notifications.update((notifications) => [...notifications, { id, type, message, list }]);

    return id;
  }
}
