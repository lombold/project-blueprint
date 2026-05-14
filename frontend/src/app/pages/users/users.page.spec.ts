import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { type User, UsersService } from '@core/api';
import { of, Subject, throwError } from 'rxjs';
import { TestHelper } from '../../testing/test-helper';
import { CreateUserDialog } from './create-user-dialog/create-user-dialog';
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
    const usersServiceMock = {
      listUsers: vi.fn().mockReturnValue(of(users)),
    };
    const dialogMock = TestHelper.createDialogMock();

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        { provide: UsersService, useValue: usersServiceMock },
        TestHelper.dialogProvider(dialogMock),
        TestHelper.activatedRouteProvider(),
      ],
    });

    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(usersServiceMock.listUsers).toHaveBeenCalledTimes(1);
    expect(compiled.textContent).toContain('ada');
    expect(compiled.textContent).toContain('ada@example.com');
  });

  it('opens create dialog and reloads users after it closes', async () => {
    const firstLoad: User[] = [];
    const secondLoad: User[] = [
      {
        id: 2,
        username: 'grace',
        email: 'grace@example.com',
      },
    ];
    const closed = new Subject<void>();
    const usersServiceMock = {
      listUsers: vi.fn().mockReturnValueOnce(of(firstLoad)).mockReturnValueOnce(of(secondLoad)),
    };
    const dialogMock = TestHelper.createDialogMock(closed.asObservable());

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        { provide: UsersService, useValue: usersServiceMock },
        TestHelper.dialogProvider(dialogMock),
        TestHelper.activatedRouteProvider(),
      ],
    });

    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();
    await fixture.whenStable();

    fixture.debugElement.query(By.css('button')).nativeElement.click();
    closed.next();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(dialogMock.open).toHaveBeenCalledWith(CreateUserDialog);
    expect(usersServiceMock.listUsers).toHaveBeenCalledTimes(2);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('grace');
  });

  it('renders loading state while users request is pending', () => {
    const usersSubject = new Subject<User[]>();
    const usersServiceMock = {
      listUsers: vi.fn().mockReturnValue(usersSubject.asObservable()),
    };
    const dialogMock = TestHelper.createDialogMock();

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        { provide: UsersService, useValue: usersServiceMock },
        TestHelper.dialogProvider(dialogMock),
        TestHelper.activatedRouteProvider(),
      ],
    });

    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Loading users...');
    usersSubject.complete();
  });

  it('renders load error when loading fails', async () => {
    const usersServiceMock = {
      listUsers: vi.fn().mockReturnValue(throwError(() => new Error('boom'))),
    };
    const dialogMock = TestHelper.createDialogMock();

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        { provide: UsersService, useValue: usersServiceMock },
        TestHelper.dialogProvider(dialogMock),
        TestHelper.activatedRouteProvider(),
      ],
    });

    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'Could not load users. Please try again.',
    );
  });

  it('renders an empty state when no users exist', async () => {
    const usersServiceMock = {
      listUsers: vi.fn().mockReturnValue(of([])),
    };
    const dialogMock = TestHelper.createDialogMock();

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        { provide: UsersService, useValue: usersServiceMock },
        TestHelper.dialogProvider(dialogMock),
        TestHelper.activatedRouteProvider(),
      ],
    });

    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'No users yet. Create one to get started!',
    );
  });
});
