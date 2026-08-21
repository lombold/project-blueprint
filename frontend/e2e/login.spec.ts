import { expect, test } from '@playwright/test';
import { login } from './auth';

test('logs in with the default Keycloak user and exposes logout', async ({ page }) => {
  await login(page);

  await page.getByTestId('profile-menu-button').click();
  await expect(page.getByTestId('logout-button')).toBeVisible();
});
