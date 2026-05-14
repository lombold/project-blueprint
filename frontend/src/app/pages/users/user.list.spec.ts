import { TestBed } from '@angular/core/testing';
import { type User } from '@core/api';
import { UserList } from './user.list';

describe('UserList', () => {
  it('renders user names and emails', () => {
    const users: User[] = [
      {
        id: 1,
        username: 'ada',
        email: 'ada@example.com',
      },
    ];

    TestBed.configureTestingModule({ imports: [UserList] });

    const fixture = TestBed.createComponent(UserList);
    fixture.componentRef.setInput('users', users);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('ada');
    expect(compiled.textContent).toContain('ada@example.com');
  });

  it('renders no user cards when users are not loaded', () => {
    TestBed.configureTestingModule({ imports: [UserList] });

    const fixture = TestBed.createComponent(UserList);
    fixture.componentRef.setInput('users', undefined);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).querySelector('article')).toBeNull();
  });
});
