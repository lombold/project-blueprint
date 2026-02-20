import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { UsersService, type User } from '../../core/api';
import { UsersPage } from './users.page';

describe('UsersPage', () => {
  it('loads users on init', () => {
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

    expect(userServiceMock.listUsers).toHaveBeenCalledTimes(1);
    expect(fixture.componentInstance.users()).toEqual(expectedUsers);
  });

  it('keeps users empty when loading fails', () => {
    const userServiceMock = {
      listUsers: vi.fn().mockReturnValue(throwError(() => new Error('boom')))
    };
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => undefined);

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [{ provide: UsersService, useValue: userServiceMock }]
    });

    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();

    expect(userServiceMock.listUsers).toHaveBeenCalledTimes(1);
    expect(fixture.componentInstance.users()).toEqual([]);

    consoleErrorSpy.mockRestore();
  });
});
