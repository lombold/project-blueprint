import { expect, type Page } from '@playwright/test';

export async function login(page: Page, username = 'user', password = 'user'): Promise<void> {
  await page.goto('/login');
  await page.waitForURL(/\/realms\/project-name\/protocol\/openid-connect\/auth/);
  await page.locator('#username').fill(username);
  await page.locator('#password').fill(password);
  await page.locator('#kc-login').click();
  await page.waitForURL(/\/users/);
  await expect(page.getByTestId('profile-menu-button')).toContainText('Default User');
}
