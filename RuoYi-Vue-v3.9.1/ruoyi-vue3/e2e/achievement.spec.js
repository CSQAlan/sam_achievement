import { test, expect } from '@playwright/test';
import { mockCommonBackend, loginAsAdmin } from './helpers.js';

test.describe('Achievement Management CRUD E2E Tests', () => {
  let isAddCalled = false;
  let isUpdateCalled = false;
  let isDeleteCalled = false;

  test.beforeEach(async ({ page }) => {
    isAddCalled = false;
    isUpdateCalled = false;
    isDeleteCalled = false;

    // 1. Mock standard backend endpoints
    await mockCommonBackend(page);

    // 2. Mock competition search list
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

    // 3. Mock sessions search list
    await page.route('**/dev-api/session/session/list*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          rows: [
            { id: 1, session: "第一届", level: "1", year: "2026" }
          ]
        })
      });
    });

    // 4. Mock achievements lists (list-responsible)
    await page.route('**/dev-api/achievement/manage/list-responsible*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          total: 1,
          rows: [
            {
              achievementId: 100,
              competitionName: "全国电子设计竞赛",
              sessionName: "第一届",
              certificateNo: "CERT-100200",
              grade: "1",
              contestant: "张三, 李四",
              instructor: "王老师",
              level: "1",
              reviewStatus: "0",
              awardTime: "2026-05-23",
              reviewResult: 0
            }
          ],
          code: 200,
          msg: "查询成功"
        })
      });
    });

    // 5. Mock GET achievement detail (for edit form loading)
    await page.route('**/dev-api/achievement/manage/100*', async (route) => {
      const method = route.request().method();
      if (method === 'GET') {
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
              awardTime: "2026-05-23",
              track: "单片机赛道",
              groupId: "1",
              ownerDepId: 100
            }
          })
        });
      } else if (method === 'DELETE') {
        isDeleteCalled = true;
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ code: 200, msg: "删除成功" })
        });
      }
    });

    // 6. Mock POST and PUT achievement
    await page.route('**/dev-api/achievement/manage', async (route) => {
      if (route.request().method() === 'POST') {
        isAddCalled = true;
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ code: 200, msg: "新增成功" })
        });
      } else if (route.request().method() === 'PUT') {
        isUpdateCalled = true;
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ code: 200, msg: "修改成功" })
        });
      }
    });

    // Perform login
    await loginAsAdmin(page);
  });

  test('should display the achievement entries table correctly', async ({ page }) => {
    // Navigate to achievement record page
    await page.goto('/achievement/manage');

    // Assert table cell populated with mock data
    await expect(page.getByRole('cell', { name: 'CERT-100200' })).toBeVisible();
    await expect(page.getByRole('cell', { name: '张三, 李四' })).toBeVisible();
  });

  test('should support adding a new achievement entry', async ({ page }) => {
    await page.goto('/achievement/manage');

    // Click "新增" button
    await page.getByRole('button', { name: '新增' }).click();

    // Verify slide-out form or modal is open (checks for title in form view)
    const formContainer = page.locator('.outcome-page');
    await expect(formContainer).toBeVisible();

    // Fill out form
    // Select competition
    await page.getByRole('combobox', { name: '* 参加比赛' }).click();
    await page.getByRole('option', { name: '全国电子设计竞赛' }).click();

    // Select award time
    await page.getByRole('combobox', { name: '* 获奖时间' }).fill('2026-05-23');
    await page.getByRole('combobox', { name: '* 获奖时间' }).press('Enter');

    // Select award level
    await page.getByRole('combobox', { name: '* 获奖级别' }).click({ force: true });
    await page.getByRole('option', { name: '国家级' }).click();

    // Select award rank
    await page.getByRole('combobox', { name: '* 奖项等级' }).click({ force: true });
    await page.getByRole('option', { name: '特等奖' }).click();

    // Fill track
    await page.getByRole('textbox', { name: '* 参赛赛道' }).fill('单片机赛道');

    // Fill certificateNo
    await page.getByRole('textbox', { name: '* 证书编号' }).fill('NEW-CERT-500');

    // Select group
    await page.getByRole('combobox', { name: '* 参赛组别' }).click({ force: true });
    await page.getByRole('option', { name: '学生组' }).click();

    // Click submit button (often named "确 定" or "保存")
    // Wait, let's click the main submit action button
    const submitBtn = page.getByRole('button', { name: /确\s*定/ }).or(page.getByRole('button', { name: /保\s*存/ })).first();
    await submitBtn.click();

    expect(isAddCalled).toBe(true);
  });

  test('should support updating an existing achievement', async ({ page }) => {
    await page.goto('/achievement/manage');

    // Click row-level "修改" link button
    await page.locator('.el-table__body').getByRole('button', { name: '修改' }).first().click();

    // Modify certificateNo
    const certField = page.getByRole('textbox', { name: '* 证书编号' });
    await expect(certField).toBeVisible();
    await certField.fill('CERT-100200-MOD');

    // Click submit
    const submitBtn = page.getByRole('button', { name: /保\s*存/ }).or(page.getByRole('button', { name: /确\s*定/ })).first();
    await submitBtn.click();

    expect(isUpdateCalled).toBe(true);
  });

  test('should support deleting an achievement entry', async ({ page }) => {
    await page.goto('/achievement/manage');

    // Click row-level "删除" link button
    await page.locator('.el-table__body').getByRole('button', { name: '删除' }).first().click();

    // Confirm deletion alert/dialog
    const confirmBtn = page.locator('.el-message-box__btns button').last();
    await confirmBtn.click();

    expect(isDeleteCalled).toBe(true);
  });
});
