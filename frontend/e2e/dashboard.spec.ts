import { test, expect } from '@playwright/test';

test.describe('Dashboard Page E2E Tests', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to dashboard
    await page.goto('/ui/');
    await page.waitForLoadState('networkidle');
  });

  test('should load dashboard page', async ({ page }) => {
    // Check that the page title contains 'Gym Buddy'
    await expect(page.locator('h1')).toContainText('Gym Buddy');
  });

  test('should display dashboard statistics', async ({ page }) => {
    // Wait for stats to load
    await page.waitForSelector('text=Total Users');
    
    // Verify all stat cards are visible
    const userStats = page.locator('text=Total Users');
    const workoutStats = page.locator('text=Workouts Logged');
    const exerciseStats = page.locator('text=Exercises Completed');

    await expect(userStats).toBeVisible();
    await expect(workoutStats).toBeVisible();
    await expect(exerciseStats).toBeVisible();
  });

  test('should display getting started section', async ({ page }) => {
    const gettingStarted = page.locator('text=Getting Started');
    await expect(gettingStarted).toBeVisible();

    // Verify getting started items
    await expect(page.locator('text=Create a user profile')).toBeVisible();
    await expect(page.locator('text=Log your workouts')).toBeVisible();
    await expect(page.locator('text=Track your progress')).toBeVisible();
  });

  test('should navigate to Users page from navigation', async ({ page }) => {
    // Click Users link
    await page.click('a:has-text("Users")');
    
    // Wait for Users page to load
    await page.waitForLoadState('networkidle');
    
    // Verify we're on Users page
    await expect(page.locator('h1')).toContainText('Users');
  });

  test('should navigate to Workouts page from navigation', async ({ page }) => {
    // Click Workouts link
    await page.click('a:has-text("Workouts")');
    
    // Wait for Workouts page to load
    await page.waitForLoadState('networkidle');
    
    // Verify we're on Workouts page
    await expect(page.locator('h1')).toContainText('Workouts');
  });
});
