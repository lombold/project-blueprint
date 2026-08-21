import { DOCUMENT } from '@angular/common';
import { Directive, ElementRef, inject, output } from '@angular/core';

@Directive({
  selector: '[appClickOutside]',
  host: {
    '(document:click)': 'onDocumentClick($event)',
    '(document:keydown.escape)': 'escapePressed.emit()',
  },
})
export class ClickOutside {
  private readonly document = inject(DOCUMENT);
  private readonly element = inject<ElementRef<HTMLElement>>(ElementRef);

  readonly clickedOutside = output<void>();
  readonly escapePressed = output<void>();

  protected onDocumentClick(event: Event): void {
    const target = event.target;
    const node = this.document.defaultView?.Node;
    if (node && target instanceof node && !this.element.nativeElement.contains(target)) {
      this.clickedOutside.emit();
    }
  }
}
