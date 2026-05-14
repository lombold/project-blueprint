import { Dialog, DialogRef } from '@angular/cdk/dialog';
import { type Provider } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NotificationService } from '@core/services/notification.service';
import { type Observable, of } from 'rxjs';

export class TestHelper {
  static createDialogMock<T = unknown>(closed?: Observable<T>) {
    return {
      open: vi.fn().mockReturnValue({ closed: closed ?? of(undefined) }),
    };
  }

  static createDialogRefMock() {
    return {
      close: vi.fn(),
    };
  }

  static createNotificationServiceMock() {
    return {
      info: vi.fn(),
    };
  }

  static dialogProvider(dialogMock = TestHelper.createDialogMock()): Provider {
    return { provide: Dialog, useValue: dialogMock };
  }

  static dialogRefProvider(dialogRefMock = TestHelper.createDialogRefMock()): Provider {
    return { provide: DialogRef, useValue: dialogRefMock };
  }

  static notificationServiceProvider(
    notificationServiceMock = TestHelper.createNotificationServiceMock(),
  ): Provider {
    return { provide: NotificationService, useValue: notificationServiceMock };
  }

  static activatedRouteProvider(data: Record<string, unknown> = {}): Provider {
    return { provide: ActivatedRoute, useValue: { data: of(data) } };
  }
}
