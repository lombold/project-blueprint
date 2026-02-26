import { TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';
import { UsersService, type User } from '@core/api';
import { UsersPage } from './users.page';

describe('UsersPage', () => {
  it('loads users on init', async () => {
    const expectedUsers: User[] = [
      { id: 1, username: 'alice', email: 'alice@example.com' }
    ];
    const userServiceMock = {
      listUsers: vi.fn().mockReturnValue(of(expectedUsers))
    };

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [{ provide: UsersService, useValue: userServiceMock }]
    });

    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();
    await fixture.whenStable();

    expect(userServiceMock.listUsers).toHaveBeenCalledTimes(1);
    expect(fixture.componentInstance.users()).toEqual(expectedUsers);
  });

  it('exposes load error when loading fails', async () => {
    const error = new Error('boom');
    const userServiceMock = {
      listUsers: vi.fn().mockReturnValue(throwError(() => error))
    };

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [{ provide: UsersService, useValue: userServiceMock }]
    });

    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();
    await fixture.whenStable();

    expect(userServiceMock.listUsers).toHaveBeenCalledTimes(1);
    expect(fixture.componentInstance.users()).toEqual([]);
    expect(fixture.componentInstance.loadError()).toBe(error);
  });

  it('renders loading state while users request is pending', () => {
    const usersSubject = new Subject<User[]>();
    const userServiceMock = {
      listUsers: vi.fn().mockReturnValue(usersSubject.asObservable())
    };

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [{ provide: UsersService, useValue: userServiceMock }]
    });

    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Loading users...');
    usersSubject.complete();
  });

  it('reloads users after creating a user', async () => {
    const userServiceMock = {
      listUsers: vi.fn().mockReturnValue(of([])),
      createUser: vi.fn().mockReturnValue(of({ id: 1, username: 'alice', email: 'alice@example.com' }))
    };

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [{ provide: UsersService, useValue: userServiceMock }]
    });

    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();
    await fixture.whenStable();

    fixture.componentInstance.formModel.set({ username: 'alice', email: 'alice@example.com' });
    fixture.componentInstance.createUser(new Event('submit') as SubmitEvent);
    await fixture.whenStable();

    expect(userServiceMock.createUser).toHaveBeenCalledTimes(1);
    expect(userServiceMock.listUsers).toHaveBeenCalledTimes(2);
  });
});
