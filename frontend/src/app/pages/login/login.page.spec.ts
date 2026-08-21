import { TestBed } from '@angular/core/testing';
import { AuthService } from '@core/services/auth.service';
import { LoginPage } from './login.page';

describe('LoginPage', () => {
  it('starts login when the page loads', async () => {
    const authService = { login: vi.fn().mockResolvedValue(undefined) };
    TestBed.configureTestingModule({
      imports: [LoginPage],
      providers: [{ provide: AuthService, useValue: authService }],
    });

    const fixture = TestBed.createComponent(LoginPage);
    await fixture.whenStable();

    expect(authService.login).toHaveBeenCalledOnce();
  });
});
