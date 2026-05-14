import { TestBed } from '@angular/core/testing';

import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    vi.useFakeTimers();
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationService);
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('adds an info notification that auto-dismisses after 5 seconds', () => {
    service.info('Saved successfully');

    expect(service.notifications()).toEqual([
      expect.objectContaining({ type: 'info', message: 'Saved successfully' }),
    ]);

    vi.advanceTimersByTime(4999);
    expect(service.notifications()).toHaveLength(1);

    vi.advanceTimersByTime(1);
    expect(service.notifications()).toEqual([]);
  });

  it('adds warn and error notifications until they are dismissed', () => {
    service.warn('Check the input');
    service.error('Save failed');

    const notifications = service.notifications();

    expect(notifications).toEqual([
      expect.objectContaining({ type: 'warn', message: 'Check the input' }),
      expect.objectContaining({ type: 'error', message: 'Save failed' }),
    ]);

    vi.advanceTimersByTime(5000);
    expect(service.notifications()).toHaveLength(2);

    service.dismiss(notifications[0].id);
    expect(service.notifications()).toEqual([
      expect.objectContaining({ type: 'error', message: 'Save failed' }),
    ]);
  });
});
