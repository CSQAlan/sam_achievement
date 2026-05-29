import { test, expect } from '@playwright/test';
import { mockCommonBackend, loginAsAdmin } from './helpers.js';

test.describe('Competition Application & Review E2E Tests', () => {
  let isAddCalled = false;
  let isReviewCalled = false;

  test.beforeEach(async ({ page }) => {
    isAddCalled = false;
    isReviewCalled = false;

    // 1. Mock common APIs
    await mockCommonBackend(page);

    // 2. Mock competition selection list (for application form dropdown)
    await page.route('**/dev-api/competition/competition/list*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          rows: [
            { id: 1, name: "全国电子设计竞赛" }
          ]
        })
      });
    });

    // 3. Mock list of competition applications
    await page.route('**/dev-api/competition-apply/competitionapply/list*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          total: 1,
          rows: [
            {
              id: 50,
              competitionId: 1,
              competitionName: "全国电子设计竞赛",
              teamName: "逐梦团队",
              studentName: "张三",
              studentNo: "S1001",
              status: "0", // 待审核
              createTime: "2026-05-23"
            }
          ],
          code: 200,
          msg: "查询成功"
        })
      });
    });

    // 4. Mock GET single application details (for edit/review loading)
    await page.route('**/dev-api/competition-apply/competitionapply/50*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          msg: "查询成功",
          data: {
            id: 50,
            competitionId: 1,
            name: "全国电子设计竞赛",
            year: 2026,
            session: "第一届",
            category: "X",
            organizations: "教育部",
            level: "A",
            teamName: "逐梦团队",
            studentName: "张三",
            studentNo: "S1001",
            status: "0"
          }
        })
      });
    });

    // 5. Mock POST (add) application
    await page.route('**/dev-api/competition-apply/competitionapply', async (route) => {
      if (route.request().method() === 'POST') {
        isAddCalled = true;
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ code: 200, msg: "申请成功" })
        });
      }
    });

    // 6. Mock PUT review application
    await page.route('**/dev-api/competition-apply/competitionapply/review/50', async (route) => {
      if (route.request().method() === 'PUT') {
        isReviewCalled = true;
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ code: 200, msg: "审核操作成功" })
        });
      }
    });

    // Perform login
    await loginAsAdmin(page);
  });

  test('should submit a competition application successfully', async ({ page }) => {
    // Navigate to Competition Apply page
    await page.goto('/competition-apply/competitionapply');

    // Click "新增" or "申请" button
    await page.getByRole('button', { name: '新增' }).click();

    // Verify application modal opens
    const dialog = page.locator('.el-dialog');
    await expect(dialog).toBeVisible();

    // Fill application form details (name select input, year, session)
    await dialog.getByPlaceholder('请选择已有赛事或直接输入新的赛事名称').fill('全国电子设计竞赛');
    await dialog.getByPlaceholder('例如：2025、十二届').fill('第一届');

    // Submit
    const submitBtn = dialog.getByRole('button', { name: '确 定' }).or(dialog.getByRole('button', { name: '确定' })).first();
    await submitBtn.click();

    expect(isAddCalled).toBe(true);
  });

  test('should allow reviewing a pending application', async ({ page }) => {
    // Navigate to Review page
    await page.goto('/competitionapply-review/index');

    // Wait for application list to render and click "审核" button in the table row
    const reviewBtn = page.locator('.el-table__body').getByRole('button', { name: '审核' }).first();
    await reviewBtn.click();

    // Assert review dialog modal is visible
    const dialog = page.locator('.el-dialog');
    await expect(dialog).toBeVisible();

    // Click "通过" or "同意" or "审核通过" button
    const approveBtn = dialog.getByRole('button', { name: '通过' }).or(dialog.getByRole('button', { name: '同意' })).first();
    await approveBtn.click({ force: true });

    // Confirm the action in the popup dialog box
    const confirmBtn = page.locator('.el-message-box__btns button').last();
    await confirmBtn.click();

    expect(isReviewCalled).toBe(true);
  });
});
