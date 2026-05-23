import { test, expect } from '@playwright/test';
import { mockCommonBackend, loginAsAdmin } from './helpers.js';

test.describe('Competition Management CRUD E2E Tests', () => {
  let isAddCalled = false;
  let isUpdateCalled = false;
  let isDeleteCalled = false;

  test.beforeEach(async ({ page }) => {
    isAddCalled = false;
    isUpdateCalled = false;
    isDeleteCalled = false;

    // 1. Mock all common backend calls (auth, dictionary, menus)
    await mockCommonBackend(page);

    // 2. Mock list of departments
    await page.route('**/dev-api/system/dept/list', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          msg: "操作成功",
          code: 200,
          data: [
            { deptId: 101, deptName: "计算机学院" },
            { deptId: 102, deptName: "数学学院" }
          ]
        }),
      });
    });

    // 3. Mock list of competitions
    await page.route('**/dev-api/competition/competition/list*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          total: 1,
          rows: [
            {
              id: 100,
              name: "第十届挑战杯大赛",
              category: "X",
              organizations: "校团委",
              level: "A",
              tags: "T",
              scopeType: "1",
              deptNames: "计算机学院",
              status: "0",
              memo: "挑战自我"
            }
          ],
          code: 200,
          msg: "查询成功"
        }),
      });
    });

    // 4. Mock GET detail and DELETE for ID 100 (merged to avoid handler overwrites)
    await page.route('**/dev-api/competition/competition/100', async (route) => {
      const method = route.request().method();
      if (method === 'GET') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            msg: "查询成功",
            data: {
              id: 100,
              name: "第十届挑战杯大赛",
              category: "X",
              organizations: "校团委",
              level: "A",
              tags: "T",
              scopeType: "1",
              status: "0",
              memo: "挑战自我",
              competitionDeptRelList: [
                { deptId: 101, sort: 1 }
              ]
            }
          }),
        });
      } else if (method === 'DELETE') {
        isDeleteCalled = true;
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ code: 200, msg: "删除成功" }),
        });
      }
    });

    // 5. Mock POST (add) and PUT (update) competition
    await page.route('**/dev-api/competition/competition', async (route) => {
      if (route.request().method() === 'POST') {
        isAddCalled = true;
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ code: 200, msg: "新增成功" }),
        });
      } else if (route.request().method() === 'PUT') {
        isUpdateCalled = true;
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ code: 200, msg: "修改成功" }),
        });
      }
    });

    // Perform Login
    await loginAsAdmin(page);
  });

  test('should display the competition list table correctly', async ({ page }) => {
    // Navigate to the competition list page
    await page.goto('/competition/competition');

    // Wait for the table data row to render
    const dataCell = page.getByRole('cell', { name: '第十届挑战杯大赛' });
    await expect(dataCell).toBeVisible();

    // Verify other column contents
    await expect(page.getByRole('cell', { name: '校团委' })).toBeVisible();
    await expect(page.getByRole('cell', { name: '计算机学院' })).toBeVisible();
  });

  test('should support adding a new competition', async ({ page }) => {
    await page.goto('/competition/competition');

    // Click "新增" button
    await page.getByRole('button', { name: '新增' }).click();

    // Verify modal dialog is open
    await expect(page.getByText('添加总赛事')).toBeVisible();

    const dialog = page.locator('.el-dialog');

    // Fill in the form
    await dialog.getByPlaceholder('请输入赛事名称').fill('电子设计大赛');
    await dialog.getByPlaceholder('请输入盖章单位').fill('电子信息工程学院');

    // Click categories and levels (checkboxes using element labels)
    await dialog.getByText('A类赛事').click();
    await dialog.getByText('国家级').click();

    // Submit the form
    await dialog.getByRole('button', { name: '确 定' }).click();

    // Wait for modal to close and check if API was hit
    await expect(page.getByText('添加总赛事')).not.toBeVisible();
    expect(isAddCalled).toBe(true);
  });

  test('should support updating an existing competition', async ({ page }) => {
    await page.goto('/competition/competition');

    // Click "修改" link button in the table row (scoped to table body)
    await page.locator('.el-table__body').getByRole('button', { name: '修改' }).first().click();

    // Verify modal dialog for edit is open
    await expect(page.getByText('修改总赛事')).toBeVisible();

    const dialog = page.locator('.el-dialog');

    // Modify the competition name
    await dialog.getByPlaceholder('请输入赛事名称').fill('第十届挑战杯大赛 - 修改版');

    // Submit the form
    await dialog.getByRole('button', { name: '确 定' }).click();

    // Wait for modal to close and check if API was hit
    await expect(page.getByText('修改总赛事')).not.toBeVisible();
    expect(isUpdateCalled).toBe(true);
  });

  test('should support deleting a competition', async ({ page }) => {
    await page.goto('/competition/competition');

    // Click "删除" link button in the table row (scoped to table body)
    await page.locator('.el-table__body').getByRole('button', { name: '删除' }).first().click();

    // Confirm the browser pop-up message box
    const confirmButton = page.locator('.el-message-box__btns button').last();
    await confirmButton.click();

    // Verify if API was hit
    expect(isDeleteCalled).toBe(true);
  });
});
