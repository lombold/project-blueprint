import { ComponentFixtureAutoDetect, TestBed } from '@angular/core/testing';
import { type User, UsersService } from '@core/api';
import { AuthService } from '@core/services/auth.service';
import { of, throwError } from 'rxjs';
import { UsersPage } from './users.page';

describe('UsersPage', () => {
  const authService = {
    displayName: () => 'Default User',
    logout: vi.fn(),
  };

  it('loads and renders users', async () => {
    const users: User[] = [
      {
        id: 1,
        username: 'ada',
        email: 'ada@example.com',
      },
    ];
    const usersService = {
      listUsers: vi.fn().mockReturnValue(of(users)),
    };

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        { provide: ComponentFixtureAutoDetect, useValue: true },
        { provide: UsersService, useValue: usersService },
        { provide: AuthService, useValue: authService },
      ],
    });

    const fixture = TestBed.createComponent(UsersPage);
    await fixture.whenStable();

    expect(usersService.listUsers).toHaveBeenCalledOnce();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('ada');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('ada@example.com');
  });

  it('renders an empty state when no users exist', async () => {
    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        { provide: ComponentFixtureAutoDetect, useValue: true },
        {
          provide: UsersService,
          useValue: { listUsers: vi.fn().mockReturnValue(of([])) },
        },
        { provide: AuthService, useValue: authService },
      ],
    });

    const fixture = TestBed.createComponent(UsersPage);
    await fixture.whenStable();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('No users found.');
  });

  it('renders an error and retries loading', async () => {
    const usersService = {
      listUsers: vi
        .fn()
        .mockReturnValueOnce(throwError(() => new Error('boom')))
        .mockReturnValueOnce(of([])),
    };
    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        { provide: ComponentFixtureAutoDetect, useValue: true },
        { provide: UsersService, useValue: usersService },
        { provide: AuthService, useValue: authService },
      ],
    });

    const fixture = TestBed.createComponent(UsersPage);
    await fixture.whenStable();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Could not load users.');

    const retry = (fixture.nativeElement as HTMLElement).querySelector(
      '[data-testid="retry-button"]',
    );
    retry?.dispatchEvent(new Event('click'));
    await fixture.whenStable();

    expect(usersService.listUsers).toHaveBeenCalledTimes(2);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('No users found.');
  });

  it('shows the signed-in user and logs out from the account menu', async () => {
    authService.logout.mockClear();
    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        { provide: ComponentFixtureAutoDetect, useValue: true },
        { provide: UsersService, useValue: { listUsers: vi.fn().mockReturnValue(of([])) } },
        { provide: AuthService, useValue: authService },
      ],
    });

    const fixture = TestBed.createComponent(UsersPage);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain('Default User');
    const accountButton = element.querySelector<HTMLElement>(
      '[data-testid="account-menu-button"]',
    );
    const accountDropdown = element.querySelector<HTMLIonPopoverElement>(
      '[data-testid="account-menu-dropdown"]',
    );

    expect(accountButton).not.toBeNull();
    expect(accountDropdown).not.toBeNull();
    expect(accountDropdown?.isOpen).toBe(false);
    accountButton?.click();
    await fixture.whenStable();
    expect(accountDropdown?.isOpen).toBe(true);
    const logoutAction = fixture.componentInstance as unknown as { logout(): void };
    logoutAction.logout();
    await fixture.whenStable();

    expect(authService.logout).toHaveBeenCalledOnce();
    expect(accountDropdown?.isOpen).toBe(false);
  });
});
