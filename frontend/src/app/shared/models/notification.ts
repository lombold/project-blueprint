export const NotificationType = {
  Info: 'info',
  Warn: 'warn',
  Error: 'error',
} as const;

export type NotificationType = (typeof NotificationType)[keyof typeof NotificationType];

export interface AppNotification {
  readonly id: string;
  readonly type: NotificationType;
  readonly message: string;
  readonly list?: string[];
}
