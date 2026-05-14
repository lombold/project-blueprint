import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-page-action',
  imports: [],
  template: ` <ng-content /> `,
  styles: ``,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PageAction {}
