import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { AuthService } from '@core/services/auth.service';
import { IonButton, IonContent, IonHeader, IonTitle, IonToolbar } from '@ionic/angular/standalone';

@Component({
  selector: 'app-login',
  template: `
    <ion-header [translucent]="true">
      <ion-toolbar>
        <ion-title>ProjectName</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content [fullscreen]="true" class="ion-padding">
      <section class="login-card">
        <p class="eyebrow">ProjectName</p>
        <h1>Sign in</h1>
        <p>Use your ProjectName account to continue.</p>
        <ion-button expand="block" size="large" (click)="login()">Continue to sign in</ion-button>
      </section>
    </ion-content>
  `,
  styles: `
    .login-card {
      display: grid;
      gap: 1rem;
      margin: 18vh auto 0;
      max-width: 28rem;
    }

    .login-card h1,
    .login-card p {
      margin: 0;
    }

    .eyebrow {
      color: var(--ion-color-primary);
      font-weight: 600;
    }
  `,
  imports: [IonButton, IonContent, IonHeader, IonTitle, IonToolbar],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginPage {
  private readonly authService = inject(AuthService);

  protected login(): void {
    void this.authService.login();
  }
}
