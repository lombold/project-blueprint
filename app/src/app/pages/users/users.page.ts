import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { UsersService } from '@core/api';
import {
  IonButton,
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

  protected readonly users = rxResource({
    stream: () => this.usersService.listUsers(),
  });
  protected readonly hasUsers = computed(() => (this.users.value()?.length ?? 0) > 0);

  protected retry(): void {
    this.users.reload();
  }
}
