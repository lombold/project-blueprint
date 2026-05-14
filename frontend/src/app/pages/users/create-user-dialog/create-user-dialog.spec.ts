import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { type User, UsersService } from '@core/api';
import { of, throwError } from 'rxjs';
import { TestHelper } from '../../../testing/test-helper';
import { CreateUserDialog } from './create-user-dialog';
import { UserForm } from './user.form';

describe('CreateUserDialog', () => {
  it('creates a user and closes with the created user', async () => {
    const request: User = {
      username: 'ada',
      email: 'ada@example.com',
    };
    const createdUser: User = {
      id: 1,
      username: 'ada',
      email: 'ada@example.com',
    };
    const usersServiceMock = {
      createUser: vi.fn().mockReturnValue(of(createdUser)),
    };
    const dialogRefMock = TestHelper.createDialogRefMock();
    const notificationServiceMock = TestHelper.createNotificationServiceMock();

    TestBed.configureTestingModule({
      imports: [CreateUserDialog],
      providers: [
        { provide: UsersService, useValue: usersServiceMock },
        TestHelper.dialogRefProvider(dialogRefMock),
        TestHelper.notificationServiceProvider(notificationServiceMock),
      ],
    });

    const fixture = TestBed.createComponent(CreateUserDialog);
    fixture.detectChanges();

    fixture.debugElement.query(By.directive(UserForm)).triggerEventHandler('submitForm', request);
    await fixture.whenStable();

    expect(usersServiceMock.createUser).toHaveBeenCalledWith(request);
    expect(dialogRefMock.close).toHaveBeenCalledWith(createdUser);
    expect(notificationServiceMock.info).toHaveBeenCalledWith('User "ada" created successfully');
  });

  it('closes without creating when the form emits cancel', () => {
    const usersServiceMock = {
      createUser: vi.fn(),
    };
    const dialogRefMock = TestHelper.createDialogRefMock();
    const notificationServiceMock = TestHelper.createNotificationServiceMock();

    TestBed.configureTestingModule({
      imports: [CreateUserDialog],
      providers: [
        { provide: UsersService, useValue: usersServiceMock },
        TestHelper.dialogRefProvider(dialogRefMock),
        TestHelper.notificationServiceProvider(notificationServiceMock),
      ],
    });

    const fixture = TestBed.createComponent(CreateUserDialog);
    fixture.detectChanges();

    fixture.debugElement.query(By.directive(UserForm)).triggerEventHandler('cancel');

    expect(usersServiceMock.createUser).not.toHaveBeenCalled();
    expect(dialogRefMock.close).toHaveBeenCalledWith();
  });

  it('keeps the dialog open and logs when create fails', async () => {
    const request: User = {
      username: 'ada',
      email: 'ada@example.com',
    };
    const error = new Error('boom');
    const usersServiceMock = {
      createUser: vi.fn().mockReturnValue(throwError(() => error)),
    };
    const dialogRefMock = TestHelper.createDialogRefMock();
    const notificationServiceMock = TestHelper.createNotificationServiceMock();
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => undefined);

    TestBed.configureTestingModule({
      imports: [CreateUserDialog],
      providers: [
        { provide: UsersService, useValue: usersServiceMock },
        TestHelper.dialogRefProvider(dialogRefMock),
        TestHelper.notificationServiceProvider(notificationServiceMock),
      ],
    });

    const fixture = TestBed.createComponent(CreateUserDialog);
    fixture.detectChanges();

    fixture.debugElement.query(By.directive(UserForm)).triggerEventHandler('submitForm', request);
    await fixture.whenStable();

    expect(usersServiceMock.createUser).toHaveBeenCalledWith(request);
    expect(dialogRefMock.close).not.toHaveBeenCalled();
    expect(notificationServiceMock.info).not.toHaveBeenCalled();
    expect(consoleErrorSpy).toHaveBeenCalledWith('Error creating user:', error);

    consoleErrorSpy.mockRestore();
  });
});
