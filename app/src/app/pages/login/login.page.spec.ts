import { ComponentFixtureAutoDetect, TestBed } from '@angular/core/testing';
import { AuthService } from '@core/services/auth.service';
import { LoginPage } from './login.page';

describe('LoginPage', () => {
  it('starts login when the user selects sign in', async () => {
    const authService = { login: vi.fn().mockResolvedValue(undefined) };
    TestBed.configureTestingModule({
      imports: [LoginPage],
      providers: [
        { provide: ComponentFixtureAutoDetect, useValue: true },
        { provide: AuthService, useValue: authService },
      ],
    });

    const fixture = TestBed.createComponent(LoginPage);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    const signInButton = element.querySelector('ion-button');

    expect(signInButton?.textContent?.trim()).toBe('Continue to sign in');
    expect(authService.login).not.toHaveBeenCalled();
    signInButton?.click();

    expect(authService.login).toHaveBeenCalledOnce();
  });
});
