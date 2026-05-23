import { test, expect } from '@playwright/test';
import { mockCommonBackend } from './helpers.js';

test.describe('Login E2E Tests (Mocked Backend)', () => {
  test.beforeEach(async ({ page }) => {
    // 1. Mock all common backend calls (auth, dictionary, menus)
    await mockCommonBackend(page);

    // Go to the login page
    await page.goto('/login');
  });

  test('should display validation error when submitting empty fields', async ({ page }) => {
    // Clear the username which is pre-filled with 'admin' by default
    await page.getByPlaceholder('账号').fill('');
    await page.getByPlaceholder('密码').fill('');

    // Click on the login button
    await page.getByRole('button', { name: '登 录' }).click();

    // Verify element-plus validation messages are displayed
    await expect(page.locator('.el-form-item__error').first()).toBeVisible();
    await expect(page.getByText('请输入您的账号')).toBeVisible();
  });

  test('should successfully log in with default credentials and redirect to dashboard', async ({ page }) => {
    // Fill in the default admin credentials
    await page.getByPlaceholder('账号').fill('admin');
    await page.getByPlaceholder('密码').fill('admin123');

    // Click on the login button
    await page.getByRole('button', { name: '登 录' }).click();

    // Wait for the main container or sidebar to load after successful login
    const sidebar = page.locator('.sidebar-container');
    await expect(sidebar).toBeVisible({ timeout: 10000 });

    // Assert URL has redirected to root/index
    await expect(page).toHaveURL(/.*index/);
  });
});
