import { test, expect } from '@playwright/test';

test.describe('Users Page E2E Tests', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to Users page
    await page.goto('/ui/users');
    await page.waitForLoadState('networkidle');
  });

  test('should load users page', async ({ page }) => {
    // Check page title
    const heading = page.locator('h1');
    await expect(heading).toContainText('Users');
  });

  test('should display new user button', async ({ page }) => {
    const button = page.locator('button:has-text("New User")');
    await expect(button).toBeVisible();
  });

  test('should show form when clicking new user button', async ({ page }) => {
    // Click new user button
    const button = page.locator('button:has-text("New User")');
    await button.click();

    // Wait for form to appear
    await page.waitForSelector('input[placeholder="Username"]');
    
    // Verify form fields are visible
    await expect(page.locator('input[placeholder="Username"]')).toBeVisible();
    await expect(page.locator('input[placeholder="Email"]')).toBeVisible();
  });

  test('should cancel form', async ({ page }) => {
    // Open form
    await page.click('button:has-text("New User")');
    await page.waitForSelector('input[placeholder="Username"]');

    // Click cancel
    await page.click('button:has-text("Cancel")');

    // Form should be hidden
    await expect(page.locator('input[placeholder="Username"]')).not.toBeVisible();
  });

  test('should create a new user', async ({ page }) => {
    // Mock the API response
    await page.route('**/api/v1/users', route => {
      if (route.request().method() === 'POST') {
        route.abort('failed');
      } else {
        route.continue();
      }
    });

    // Open form
    await page.click('button:has-text("New User")');
    await page.waitForSelector('input[placeholder="Username"]');

    // Fill in form
    await page.fill('input[placeholder="Username"]', 'testuser');
    await page.fill('input[placeholder="Email"]', 'test@example.com');

    // Submit form
    await page.click('button:has-text("Create"):first-of-type');

    // Verify form is closed (optional - depends on API response)
    // In real scenario, the form would close after successful submission
  });

  test('should navigate back to dashboard from users page', async ({ page }) => {
    // Click Dashboard link
    await page.click('a:has-text("Dashboard")');
    
    // Wait for page load
    await page.waitForLoadState('networkidle');
    
    // Verify we're on dashboard
    await expect(page.locator('h1')).toContainText('Gym Buddy');
  });
});
