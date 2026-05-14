import { ErrorHandler, inject, Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ValidationErrorResponse } from '@core/api';
import { NotificationService } from '@core/services/notification.service';

@Injectable({
  providedIn: 'root',
})
export class GlobalErrorHandler implements ErrorHandler {
  private readonly notificatonService = inject(NotificationService);

  handleError(error: unknown): void {
    if (error instanceof HttpErrorResponse) {
      this.handleHttpError(error);
      return;
    }

    if (error instanceof Error) {
      this.handleRuntimeError(error);
      return;
    }

    console.error('Unknown error', error);
  }

  private handleHttpError(error: HttpErrorResponse): void {
    console.error('HTTP error', {
      status: error.status,
      message: error.message,
      url: error.url,
      error: error.error,
    });

    switch (error.status) {
      case 401:
        break;
      case 400:
        this.handleBadRequest(error);
        break;
      case 403:
        break;

      case 500:
        break;
    }
  }

  private handleRuntimeError(error: Error): void {
    console.error('Runtime error', {
      message: error.message,
      stack: error.stack,
    });
  }

  private handleBadRequest(error: HttpErrorResponse): void {
    const errorResponse: ValidationErrorResponse = error.error;

    this.notificatonService.warn(
      'Validation error',
      ...errorResponse.errors.map((e) => `${e.field} ${e.message}`),
    );
  }
}
