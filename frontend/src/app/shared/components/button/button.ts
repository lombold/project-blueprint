import { Component, HostBinding, input } from '@angular/core';

type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger';
type ButtonSize = 'sm' | 'md' | 'lg';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'button[app-button]',
  standalone: true,
  template: `
    @if (loading()) {
      <span
        class="h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
      ></span>
    }

    <ng-content />
  `,
  host: {
    '[attr.type]': 'type()',
    '[disabled]': 'disabled() || loading()',
  },
})
export class Button {
  variant = input<ButtonVariant>('primary');
  size = input<ButtonSize>('md');
  type = input<'button' | 'submit' | 'reset'>('button');
  disabled = input(false);
  loading = input(false);
  fullWidth = input(false);

  @HostBinding('class')
  get classes(): string {
    return [
      'inline-flex items-center justify-center gap-2 rounded-lg font-medium',
      'transition-colors duration-150',
      'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2',
      'disabled:pointer-events-none disabled:opacity-50',
      this.fullWidth() ? 'w-full' : '',
      this.sizeClasses(),
      this.variantClasses(),
    ].join(' ');
  }

  private sizeClasses(): string {
    return {
      sm: 'h-8 px-3 text-sm',
      md: 'h-10 px-4 text-sm',
      lg: 'h-12 px-6 text-base',
    }[this.size()];
  }

  private variantClasses(): string {
    return {
      primary: 'bg-blue-600 text-white hover:bg-blue-700 focus-visible:ring-blue-600',
      secondary: 'bg-zinc-100 text-zinc-900 hover:bg-zinc-200 focus-visible:ring-zinc-400',
      ghost: 'bg-transparent text-zinc-700 hover:bg-zinc-100 focus-visible:ring-zinc-400',
      danger: 'bg-red-600 text-white hover:bg-red-700 focus-visible:ring-red-600',
    }[this.variant()];
  }
}
