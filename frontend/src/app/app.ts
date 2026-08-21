import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { LucideChevronDown, LucideLogOut, LucideUserRound } from '@lucide/angular';
import { ClickOutside } from '@shared/directives/click-outside';

@Component({
  selector: 'app-root',
  imports: [
    ClickOutside,
    LucideChevronDown,
    LucideLogOut,
    LucideUserRound,
    RouterLink,
    RouterOutlet,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  private readonly authService = inject(AuthService);

  protected readonly title = signal('frontend');
  protected readonly profileMenuOpen = signal(false);
  protected readonly displayName = this.authService.displayName;

  protected toggleProfileMenu(): void {
    this.profileMenuOpen.update((open) => !open);
  }

  protected closeProfileMenu(): void {
    this.profileMenuOpen.set(false);
  }

  protected logout(): void {
    this.authService.logout();
  }
}
