import { ComponentFixture, TestBed } from '@angular/core/testing';

import { type AppNotification } from '@shared/models/notification';

import { NotificationCenterComponent } from './notification-center';

describe('NotificationCenterComponent', () => {
  let fixture: ComponentFixture<NotificationCenterComponent>;
  let component: NotificationCenterComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationCenterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationCenterComponent);
    component = fixture.componentInstance;
  });

  it('keeps the polite notification region mounted when empty', () => {
    fixture.componentRef.setInput('notifications', []);
    fixture.detectChanges();

    const region = fixture.nativeElement.querySelector('section[aria-live="polite"]');
    const articles = fixture.nativeElement.querySelectorAll('article');

    expect(region).not.toBeNull();
    expect(region.getAttribute('aria-label')).toBe('Application notifications');
    expect(articles).toHaveLength(0);
  });

  it('renders notifications with severity styles', () => {
    fixture.componentRef.setInput('notifications', [
      notification('1', 'info', 'Info message'),
      notification('2', 'warn', 'Warn message'),
      notification('3', 'error', 'Error message'),
    ]);
    fixture.detectChanges();

    const articles = fixture.nativeElement.querySelectorAll('article');

    expect(articles).toHaveLength(3);
    expect(articles[0].textContent).toContain('Info message');
    expect(articles[0].classList).toContain('bg-emerald-50');
    expect(articles[1].classList).toContain('bg-amber-50');
    expect(articles[2].classList).toContain('bg-rose-50');
  });

  it('uses reduced-motion-safe slide-in animation for notification articles', () => {
    fixture.componentRef.setInput('notifications', [notification('1', 'warn', 'Warn message')]);
    fixture.detectChanges();

    const article = fixture.nativeElement.querySelector('article');

    expect(article.classList).toContain('motion-safe:animate-slide-in-top');
    expect(article.classList).not.toContain('animate-slide-in-top');
  });

  it('shows dismiss buttons for warn and error notifications only with unique labels', () => {
    fixture.componentRef.setInput('notifications', [
      notification('1', 'info', 'Info message'),
      notification('2', 'warn', 'First warning'),
      notification('3', 'warn', 'Second warning'),
      notification('4', 'error', 'Error message'),
    ]);
    fixture.detectChanges();

    const buttons = fixture.nativeElement.querySelectorAll('button');

    expect(buttons).toHaveLength(3);
    expect(buttons[0].getAttribute('aria-label')).toBe(
      'Dismiss warn notification: First warning',
    );
    expect(buttons[1].getAttribute('aria-label')).toBe(
      'Dismiss warn notification: Second warning',
    );
    expect(buttons[2].getAttribute('aria-label')).toBe(
      'Dismiss error notification: Error message',
    );
  });

  it('emits the dismissed notification id', () => {
    const dismissed = vi.fn();
    component.dismissed.subscribe(dismissed);
    fixture.componentRef.setInput('notifications', [notification('2', 'warn', 'Warn message')]);
    fixture.detectChanges();

    fixture.nativeElement.querySelector('button').click();

    expect(dismissed).toHaveBeenCalledWith('2');
  });
});

function notification(
  id: string,
  type: AppNotification['type'],
  message: string,
): AppNotification {
  return { id, type, message };
}
