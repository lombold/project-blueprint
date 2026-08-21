import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { App } from './app';

describe('App', () => {
  const authService = {
    displayName: () => 'Default User',
    logout: vi.fn(),
  };

  beforeEach(async () => {
    authService.logout.mockClear();
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render title', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('ProjectName');
  });

  it('opens a user dropdown with logout', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;

    element.querySelector<HTMLElement>('[data-testid="profile-menu-button"]')?.click();
    await fixture.whenStable();
    element.querySelector<HTMLElement>('[data-testid="logout-button"]')?.click();

    expect(element.textContent).toContain('Default User');
    expect(authService.logout).toHaveBeenCalledOnce();
  });
});
