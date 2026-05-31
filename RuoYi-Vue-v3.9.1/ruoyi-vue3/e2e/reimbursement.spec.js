import { test, expect } from '@playwright/test';
import { mockCommonBackend, loginAsAdmin } from './helpers.js';

test.describe('Reimbursement Projects CRUD E2E Tests', () => {
  let isCancelCalled = false;

  test.beforeEach(async ({ page }) => {
    isCancelCalled = false;

    // 1. Mock common backend routes
    await mockCommonBackend(page);

    // 2. Mock project info
    await page.route('**/dev-api/system/SamReimbursementItems/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          msg: "操作成功",
          data: {
            id: 1,
            name: "2026年学科竞赛专项经费",
            ownerDepId: 100,
            status: "0"
          }
        })
      });
    });

    // 3. Mock reimbursement rules
    await page.route('**/dev-api/system/SamReimbursementItems/reimbursementRules*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          msg: "操作成功",
          data: [
            {
              id: 1,
              ruleName: "一等奖报销比例",
              ratio: "1.0"
            }
          ]
        })
      });
    });

    // 4. Mock list of associated achievements
    await page.route('**/dev-api/system/Reimbursement/list*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          total: 1,
          rows: [
            {
              achievementId: 100,
              sessionId: 1,
              category: "1",
              competitionName: "全国电子设计竞赛",
              name: "电子钟设计",
              contestants: "张三, 李四",
              instructors: "王老师",
              level: "1",
              grade: "1",
              awardTime: "2026-05-23",
              fee: 5000,
              reimbursementDate: "",
              isReimburse: 1
            }
          ],
          code: 200,
          msg: "查询成功"
        })
      });
    });

    // 5. Mock achievement detail for the View Detail dialog
    await page.route('**/dev-api/achievement/manage/100*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          msg: "操作成功",
          data: {
            achievementId: 100,
            competitionId: 1,
            sessionId: 1,
            certificateNo: "CERT-100200",
            grade: "1",
            contestant: "张三, 李四",
            instructor: "王老师",
            level: "1",
            reviewStatus: "0",
            awardTime: "2026-05-23"
          }
        })
      });
    });

    // 6. Mock cancel association
    await page.route('**/dev-api/system/Reimbursement/cancelAssociation', async (route) => {
      isCancelCalled = true;
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ code: 200, msg: "取消关联成功" })
      });
    });

    // Perform login
    await loginAsAdmin(page);
  });

  async function navigateToReimbursementDetails(page) {
    // Navigate to /index first (a static route) to let the app initialize and load routes
    await page.goto('/');
    // Use client-side router to navigate to bypass 404 router reload issue in RuoYi
    await page.evaluate(() => {
      const app = document.getElementById('app').__vue_app__;
      app.config.globalProperties.$router.push('/reimbursement/Reimbursement?reimbursementItemId=1');
    });
    // Wait for the URL and page elements to render
    await page.waitForURL('**/reimbursement/Reimbursement*');
    await expect(page.locator('.app-container')).toBeVisible();
  }

  test('should display the reimbursement projects associated achievements correctly', async ({ page }) => {
    await navigateToReimbursementDetails(page);

    // Assert that the list table displays mock items
    await expect(page.getByRole('cell', { name: '全国电子设计竞赛' })).toBeVisible();
    await expect(page.getByRole('cell', { name: '电子钟设计' })).toBeVisible();
  });

  test('should support viewing achievement details', async ({ page }) => {
    await navigateToReimbursementDetails(page);

    // Click "详情" button
    await page.locator('.el-table__body').getByRole('button', { name: '详情' }).first().click();

    // Verify achievement detail dialog is open
    const dialog = page.locator('.el-dialog');
    await expect(dialog).toBeVisible();

    // Close dialog
    await dialog.getByRole('button', { name: /关\s*闭/ }).or(dialog.getByRole('button', { name: '关闭' })).first().click();
  });

  test('should support canceling association', async ({ page }) => {
    await navigateToReimbursementDetails(page);

    // Click "取消关联" button
    await page.locator('.el-table__body').getByRole('button', { name: '取消关联' }).first().click();

    // Confirm deletion/action alert/dialog
    const confirmBtn = page.locator('.el-message-box__btns button').last();
    await confirmBtn.click();

    expect(isCancelCalled).toBe(true);
  });
});
