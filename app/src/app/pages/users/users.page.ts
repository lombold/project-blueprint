import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { UsersService } from '@core/api';
import { AuthService } from '@core/services/auth.service';
import {
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonItem,
  IonLabel,
  IonList,
  IonSpinner,
  IonTitle,
  IonToolbar,
} from '@ionic/angular/standalone';

@Component({
  selector: 'app-users',
  templateUrl: './users.page.html',
  styleUrls: ['./users.page.scss'],
  imports: [
    IonButton,
    IonButtons,
    IonContent,
    IonHeader,
    IonItem,
    IonLabel,
    IonList,
    IonSpinner,
    IonTitle,
    IonToolbar,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UsersPage {
  private readonly usersService = inject(UsersService);
  private readonly authService = inject(AuthService);

  protected readonly users = rxResource({
    stream: () => this.usersService.listUsers(),
  });
  protected readonly hasUsers = computed(() => (this.users.value()?.length ?? 0) > 0);
  protected readonly accountMenuOpen = signal(false);
  protected readonly displayName = this.authService.displayName;

  protected retry(): void {
    this.users.reload();
  }

  protected toggleAccountMenu(): void {
    this.accountMenuOpen.update((open) => !open);
  }

  protected logout(): void {
    this.accountMenuOpen.set(false);
    void this.authService.logout();
  }
}
