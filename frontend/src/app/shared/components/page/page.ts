import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouteData } from '../../../app.routes';

@Component({
  selector: 'app-page',
  imports: [],
  template: `
    <div class="min-h-screen bg-gray-50 p-8">
      <div class="mx-auto max-w-6xl space-y-8">
        <header class="space-y-3 flex row space-between items-start">
          <div class="flex-1">
            @if (title()) {
              <p class="text-sm font-semibold uppercase tracking-[0.3em] text-indigo-600">
                {{ title() }}
              </p>
            }
            @if (subtitle()) {
              <h1 class="text-4xl font-bold text-gray-900">{{ subtitle() }}</h1>
            }
          </div>

          <ng-content select="app-page-action" />
        </header>
        <main>
          <ng-content />
        </main>
      </div>
    </div>
  `,
  styles: `
    :host {
      display: block;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Page {
  private readonly activeRoute = inject(ActivatedRoute);
  private readonly routeData = toSignal<RouteData>(this.activeRoute.data);
  protected readonly title = computed(() => this.routeData()?.title);
  protected readonly subtitle = computed(() => this.routeData()?.subtitle);
}
