import { ComponentFixtureAutoDetect, TestBed } from '@angular/core/testing';
import { type User, UsersService } from '@core/api';
import { of, throwError } from 'rxjs';
import { UsersPage } from './users.page';

describe('UsersPage', () => {
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
      ],
    });

    const fixture = TestBed.createComponent(UsersPage);
    await fixture.whenStable();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Could not load users.');

    const retry = (fixture.nativeElement as HTMLElement).querySelector('ion-button');
    retry?.dispatchEvent(new Event('click'));
    await fixture.whenStable();

    expect(usersService.listUsers).toHaveBeenCalledTimes(2);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('No users found.');
  });
});
