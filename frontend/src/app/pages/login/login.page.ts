import { ChangeDetectionStrategy, Component, inject, type OnInit } from '@angular/core';
import { AuthService } from '@core/services/auth.service';
import { LucideLogIn } from '@lucide/angular';
import { Button } from '@shared/components/button/button';

@Component({
  selector: 'app-login',
  imports: [Button, LucideLogIn],
  template: `
    <main class="min-h-[calc(100vh-4.5rem)] bg-zinc-50 px-6 py-16">
      <section class="mx-auto flex max-w-md flex-col gap-8">
        <div>
          <p class="text-sm font-medium text-indigo-700">ProjectName</p>
          <h1 class="mt-3 text-3xl font-semibold text-zinc-950">Sign in</h1>
          <p class="mt-3 text-base text-zinc-600">Use your ProjectName account to continue.</p>
        </div>
        <button app-button type="button" size="lg" (click)="login()">
          <svg lucideLogIn size="18" aria-hidden="true"></svg>
          Sign in with Keycloak
        </button>
      </section>
    </main>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginPage implements OnInit {
  private readonly authService = inject(AuthService);

  ngOnInit(): void {
    void this.authService.login();
  }

  protected login(): void {
    void this.authService.login();
  }
}
