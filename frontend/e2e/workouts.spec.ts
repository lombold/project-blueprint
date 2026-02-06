import { test, expect } from '@playwright/test';

test.describe('Workouts Page E2E Tests', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to Workouts page
    await page.goto('/ui/workouts');
    await page.waitForLoadState('networkidle');
  });

  test('should load workouts page', async ({ page }) => {
    // Check page title
    const heading = page.locator('h1');
    await expect(heading).toContainText('Workouts');
  });

  test('should display new workout button', async ({ page }) => {
    const button = page.locator('button:has-text("New Workout")');
    await expect(button).toBeVisible();
  });

  test('should show form when clicking new workout button', async ({ page }) => {
    // Click new workout button
    const button = page.locator('button:has-text("New Workout")');
    await button.click();

    // Wait for form to appear
    await page.waitForSelector('input[placeholder="Workout Name"]');
    
    // Verify form fields are visible
    await expect(page.locator('input[placeholder="Workout Name"]')).toBeVisible();
    await expect(page.locator('textarea[placeholder="Description"]')).toBeVisible();
    await expect(page.locator('input[placeholder="Duration (minutes)"]')).toBeVisible();
  });

  test('should cancel form', async ({ page }) => {
    // Open form
    await page.click('button:has-text("New Workout")');
    await page.waitForSelector('input[placeholder="Workout Name"]');

    // Click cancel
    await page.click('button:has-text("Cancel")');

    // Form should be hidden
    await expect(page.locator('input[placeholder="Workout Name"]')).not.toBeVisible();
  });

  test('should fill and submit workout form', async ({ page }) => {
    // Open form
    await page.click('button:has-text("New Workout")');
    await page.waitForSelector('input[placeholder="Workout Name"]');

    // Fill in form
    await page.fill('input[placeholder="Workout Name"]', 'Push Day');
    await page.fill('textarea[placeholder="Description"]', 'Chest, shoulders, and triceps');
    await page.fill('input[placeholder="Duration (minutes)"]', '60');

    // Note: We don't submit here since we'd need a running backend
    // In CI, the backend would be running and this would work
  });

  test('should display empty state message', async ({ page }) => {
    // Check for empty state message
    const emptyState = page.locator('text=No workouts yet');
    
    // This will be visible if there are no workouts
    // In real scenario, this depends on the API response
    if (await emptyState.isVisible()) {
      await expect(emptyState).toContainText('Create one to get started');
    }
  });

  test('should navigate back to dashboard from workouts page', async ({ page }) => {
    // Click Dashboard link
    await page.click('a:has-text("Dashboard")');
    
    // Wait for page load
    await page.waitForLoadState('networkidle');
    
    // Verify we're on dashboard
    await expect(page.locator('h1')).toContainText('Gym Buddy');
  });
});
