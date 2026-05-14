import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { type User } from '@core/api';
import { UserForm } from './user.form';

@Component({
  imports: [UserForm],
  template: `<app-user-form (cancel)="cancelled = true" />`,
})
class UserFormCancelHost {
  cancelled = false;
}

describe('UserForm', () => {
  it('emits username and email on submit', async () => {
    const submitted: User[] = [];

    TestBed.configureTestingModule({ imports: [UserForm] });

    const fixture = TestBed.createComponent(UserForm);
    fixture.componentInstance['submitForm'].subscribe((request) => submitted.push(request));
    fixture.detectChanges();

    const usernameInput = fixture.debugElement.query(By.css('#username'))
      .nativeElement as HTMLInputElement;
    usernameInput.value = 'ada';
    usernameInput.dispatchEvent(new Event('input'));

    const emailInput = fixture.debugElement.query(By.css('#email')).nativeElement as HTMLInputElement;
    emailInput.value = 'ada@example.com';
    emailInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    fixture.debugElement.query(By.css('form')).triggerEventHandler('submit', new SubmitEvent('submit'));
    await fixture.whenStable();

    expect(submitted).toEqual([{ username: 'ada', email: 'ada@example.com' }]);
  });

  it('does not emit when the form is invalid', () => {
    const submitted: User[] = [];

    TestBed.configureTestingModule({ imports: [UserForm] });

    const fixture = TestBed.createComponent(UserForm);
    fixture.componentInstance['submitForm'].subscribe((request) => submitted.push(request));
    fixture.detectChanges();

    fixture.debugElement.query(By.css('form')).triggerEventHandler('submit', new SubmitEvent('submit'));

    expect(submitted).toEqual([]);
  });

  it('does not emit when the email is invalid', () => {
    const submitted: User[] = [];

    TestBed.configureTestingModule({ imports: [UserForm] });

    const fixture = TestBed.createComponent(UserForm);
    fixture.componentInstance['submitForm'].subscribe((request) => submitted.push(request));
    fixture.detectChanges();

    const usernameInput = fixture.debugElement.query(By.css('#username'))
      .nativeElement as HTMLInputElement;
    usernameInput.value = 'ada';
    usernameInput.dispatchEvent(new Event('input'));

    const emailInput = fixture.debugElement.query(By.css('#email')).nativeElement as HTMLInputElement;
    emailInput.value = 'not-an-email';
    emailInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    fixture.debugElement.query(By.css('form')).triggerEventHandler('submit', new SubmitEvent('submit'));

    expect(submitted).toEqual([]);
  });

  it('emits cancel when cancel button is clicked', () => {
    TestBed.configureTestingModule({ imports: [UserFormCancelHost] });

    const fixture = TestBed.createComponent(UserFormCancelHost);
    fixture.detectChanges();

    const cancelButton = fixture.debugElement
      .queryAll(By.css('button'))
      .find((button) => button.nativeElement.textContent.trim() === 'Cancel');
    expect(cancelButton).toBeDefined();
    cancelButton?.triggerEventHandler('click', new MouseEvent('click'));

    expect(fixture.componentInstance.cancelled).toBe(true);
  });
});
