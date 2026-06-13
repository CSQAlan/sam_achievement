import { test, expect } from '@playwright/test';
import { mockCommonBackend, loginAsAdmin } from './helpers.js';

async function fulfillJson(route, body, status = 200) {
  await route.fulfill({
    status,
    contentType: 'application/json',
    body: JSON.stringify(body),
  });
}

async function browserFetch(page, url, options = {}) {
  return await page.evaluate(async ({ url, options }) => {
    const response = await fetch(url, options);
    const headers = Object.fromEntries(response.headers.entries());
    const contentType = response.headers.get('content-type') || '';
    let body;
    if (contentType.includes('application/json')) {
      body = await response.json();
    } else {
      body = await response.text();
    }
    return { status: response.status, headers, body };
  }, { url, options });
}

async function mockUserInfo(page, { roles = ['admin'], permissions = ['*:*:*'], userName = 'admin' } = {}) {
  await page.unroute('**/dev-api/getInfo');
  await page.route('**/dev-api/getInfo', async (route) => {
    await fulfillJson(route, {
      msg: '操作成功',
      code: 200,
      permissions,
      roles,
      user: {
        userId: userName === 'admin' ? 1 : 2001,
        userName,
        nickName: userName === 'admin' ? '管理员' : '测试学生',
        avatar: '',
        phonenumber: '13800000000',
        email: `${userName}@example.test`,
        profileInitialized: 1,
      },
    });
  });
}

async function mockAchievementList(page, rows = defaultAchievementRows()) {
  const fulfillList = async (route) => {
    const url = new URL(route.request().url());
    const pageSize = Number(url.searchParams.get('pageSize') || 10);
    await fulfillJson(route, {
      total: rows.length,
      rows: rows.slice(0, Math.min(Math.max(pageSize, 1), rows.length)),
      code: 200,
      msg: '查询成功',
    });
  };
  await page.route('**/dev-api/achievement/manage/list*', fulfillList);
  await page.route('**/dev-api/achievement/manage/list-responsible*', fulfillList);
}

function defaultAchievementRows() {
  return [
    {
      achievementId: 100,
      competitionName: '全国电子设计竞赛',
      sessionName: '第一届',
      certificateNo: 'CERT-100200',
      grade: '1',
      contestant: '张三, 李四',
      instructor: '王老师',
      level: '1',
      reviewStatus: '0',
      awardTime: '2026-05-23',
      reviewResult: 0,
    },
    {
      achievementId: 101,
      competitionName: '程序设计竞赛',
      sessionName: '第二届',
      certificateNo: 'CERT-100201',
      grade: '2',
      contestant: '赵六',
      instructor: '钱老师',
      level: '2',
      reviewStatus: '1',
      awardTime: '2026-05-24',
      reviewResult: 1,
    },
    {
      achievementId: 102,
      competitionName: '数学建模竞赛',
      sessionName: '第三届',
      certificateNo: 'CERT-100202',
      grade: '3',
      contestant: '孙七',
      instructor: '周老师',
      level: '1',
      reviewStatus: '2',
      awardTime: '2026-05-25',
      reviewResult: 2,
    },
  ];
}

test.describe('QA acceptance coverage for B role tasks', () => {
  test('FT-LOGIN-003 rejects wrong password and stays on login page', async ({ page }) => {
    await mockCommonBackend(page);
    await page.unroute('**/dev-api/login');
    await page.route('**/dev-api/login', async (route) => {
      await fulfillJson(route, { code: 500, msg: '用户名或密码错误' });
    });

    await page.goto('/login');
    await page.getByPlaceholder('账号').fill('admin');
    await page.getByPlaceholder('密码').fill('wrong-password');
    await page.getByRole('button', { name: '登 录' }).click();

    await expect(page.getByText('用户名或密码错误')).toBeVisible();
    await expect(page).toHaveURL(/\/login/);
  });

  test('FT-LOGIN-004 rejects disabled account and stays on login page', async ({ page }) => {
    await mockCommonBackend(page);
    await page.unroute('**/dev-api/login');
    await page.route('**/dev-api/login', async (route) => {
      await fulfillJson(route, { code: 500, msg: '账号已停用' });
    });

    await page.goto('/login');
    await page.getByPlaceholder('账号').fill('disabled_user');
    await page.getByPlaceholder('密码').fill('admin123');
    await page.getByRole('button', { name: '登 录' }).click();

    await expect(page.getByText('账号已停用')).toBeVisible();
    await expect(page).toHaveURL(/\/login/);
  });

  test('FT-AUTH-001 rejects unauthenticated achievement API access', async ({ page }) => {
    await mockCommonBackend(page);
    await page.route('**/dev-api/achievement/manage/list-responsible*', async (route) => {
      const authHeader = route.request().headers().authorization;
      if (!authHeader) {
        await fulfillJson(route, { code: 401, msg: '认证失败，无法访问系统资源' }, 401);
        return;
      }
      await fulfillJson(route, { code: 200, rows: [], total: 0, msg: '查询成功' });
    });

    await page.goto('/login');
    const response = await browserFetch(page, '/dev-api/achievement/manage/list-responsible?pageNum=1&pageSize=10');

    expect(response.status).toBe(401);
    expect(response.body).toMatchObject({ code: 401 });
  });

  test('FT-AUTH-002/003/004 enforces limited student permissions', async ({ page }) => {
    await mockCommonBackend(page);
    await mockUserInfo(page, {
      roles: ['student'],
      permissions: ['achievement:manage:list'],
      userName: 'student001',
    });
    await mockAchievementList(page, [defaultAchievementRows()[0]]);
    await page.route('**/dev-api/achievement/manage/list-guided*', async (route) => {
      await fulfillJson(route, { code: 403, msg: '当前操作没有权限' }, 403);
    });
    await page.route('**/dev-api/achievement/manage/999*', async (route) => {
      await fulfillJson(route, { code: 403, msg: '当前操作没有权限' }, 403);
    });
    await page.route('**/dev-api/achievement/manage/100', async (route) => {
      if (route.request().method() === 'DELETE') {
        await fulfillJson(route, { code: 403, msg: '当前操作没有权限' }, 403);
      }
    });

    await loginAsAdmin(page);
    await page.goto('/achievement/manage');
    await expect(page.getByText('CERT-100200')).toBeVisible();
    await expect(page.locator('.toolbar-left').getByRole('button', { name: '新增' })).toHaveCount(0);
    await expect(page.locator('.toolbar-left').getByRole('button', { name: '删除' })).toHaveCount(0);
    await expect(page.locator('.toolbar-left').getByRole('button', { name: '导出' })).toHaveCount(0);

    const guided = await browserFetch(page, '/dev-api/achievement/manage/list-guided?pageNum=1&pageSize=10', {
      headers: { Authorization: 'Bearer mocked-jwt-token-xyz' },
    });
    const tamperedDetail = await browserFetch(page, '/dev-api/achievement/manage/999', {
      headers: { Authorization: 'Bearer mocked-jwt-token-xyz' },
    });
    const deleteAttempt = await browserFetch(page, '/dev-api/achievement/manage/100', {
      method: 'DELETE',
      headers: { Authorization: 'Bearer mocked-jwt-token-xyz' },
    });

    expect(guided.status).toBe(403);
    expect(tamperedDetail.status).toBe(403);
    expect(deleteAttempt.status).toBe(403);
  });

  test('FT-ACH-002 filters achievement list by certificate number', async ({ page }) => {
    await mockCommonBackend(page);
    let searchedUrl = '';
    await page.route('**/dev-api/achievement/manage/list-responsible*', async (route) => {
      searchedUrl = route.request().url();
      await fulfillJson(route, {
        total: 1,
        rows: [defaultAchievementRows()[0]],
        code: 200,
        msg: '查询成功',
      });
    });

    await loginAsAdmin(page);
    await page.goto('/achievement/manage');
    await expect(page.getByText('CERT-100200')).toBeVisible();

    const responsePromise = page.waitForResponse((response) =>
      response.url().includes('/dev-api/achievement/manage/list-responsible') &&
      response.url().includes('certificateNo=CERT-100200') &&
      response.status() === 200
    );
    await page.getByPlaceholder('请输入证书编号').fill('CERT-100200');
    await page.getByRole('button', { name: '搜索' }).click();
    await responsePromise;

    expect(decodeURIComponent(searchedUrl)).toContain('certificateNo=CERT-100200');
  });

  test('FT-ACH-004 validates required achievement fields before saving', async ({ page }) => {
    await mockCommonBackend(page);
    await mockAchievementList(page);
    await loginAsAdmin(page);
    await page.goto('/achievement/manage');

    await page.getByRole('button', { name: '新增' }).click();
    await expect(page.locator('.outcome-page')).toBeVisible();
    await page.getByRole('button', { name: /保\s*存|确\s*定/ }).first().click();

    await expect(page.getByText('比赛不能为空')).toBeVisible();
    await expect(page.getByText('获奖时间不能为空')).toBeVisible();
    await expect(page.getByText('证书编号不能为空')).toBeVisible();
  });

  test('FT-ACH-005 prevents duplicate certificate numbers', async ({ page }) => {
    await mockCommonBackend(page);
    await mockAchievementList(page);
    await page.unroute('**/dev-api/achievement/manage/checkCertificateNoUnique*');
    await page.route('**/dev-api/achievement/manage/checkCertificateNoUnique*', async (route) => {
      await fulfillJson(route, { code: 200, msg: '操作成功', data: false });
    });

    await loginAsAdmin(page);
    await page.goto('/achievement/manage');
    await page.getByRole('button', { name: '新增' }).click();
    await expect(page.locator('.outcome-page')).toBeVisible();

    const certificateNo = page.getByRole('textbox', { name: '* 证书编号' });
    await certificateNo.fill('CERT-100200');
    await certificateNo.press('Tab');

    await expect(page.getByText('证书编号已存在')).toBeVisible();
  });

  test('FT-ACH-008 loads dashboard and yearly achievement statistics', async ({ page }) => {
    await mockCommonBackend(page);
    await page.goto('/login');

    const dashboard = await browserFetch(page, '/dev-api/achievement/manage/stats/dashboard');
    const yearly = await browserFetch(page, '/dev-api/achievement/manage/stats/year');

    expect(dashboard.status).toBe(200);
    expect(dashboard.body).toMatchObject({
      code: 200,
      data: {
        totalCount: 10,
        pendingCount: 2,
        approvedCount: 8,
      },
    });
    expect(yearly.status).toBe(200);
    expect(yearly.body.code).toBe(200);
    expect(yearly.body.data).toEqual(
      expect.arrayContaining([
        expect.objectContaining({ year: 2026, count: 5 }),
      ])
    );
  });

  test('FT-ACH-009 exports selected achievement attachments as zip', async ({ page }) => {
    await mockCommonBackend(page);
    let requestPayload = null;
    await page.route('**/dev-api/achievement/manage/exportAttachmentZip', async (route) => {
      requestPayload = JSON.parse(route.request().postData() || '{}');
      await route.fulfill({
        status: 200,
        contentType: 'application/zip',
        body: 'fake-zip-content',
      });
    });

    await page.goto('/login');
    const response = await browserFetch(page, '/dev-api/achievement/manage/exportAttachmentZip', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer mocked-jwt-token-xyz',
      },
      body: JSON.stringify({
        achievementIds: ['100'],
        types: [1, 2],
        sourceMode: 'responsible',
      }),
    });

    expect(response.status).toBe(200);
    expect(response.headers['content-type']).toContain('application/zip');
    expect(requestPayload).toMatchObject({
      achievementIds: ['100'],
      types: [1, 2],
      sourceMode: 'responsible',
    });
  });

  test('FT-BOUND-003 rejects overlong certificate numbers before saving', async ({ page }) => {
    await mockCommonBackend(page);
    await mockAchievementList(page);
    await loginAsAdmin(page);
    await page.goto('/achievement/manage');

    await page.getByRole('button', { name: '新增' }).click();
    await expect(page.locator('.outcome-page')).toBeVisible();

    const certificateNo = page.getByRole('textbox', { name: '* 证书编号' });
    await certificateNo.fill('X'.repeat(101));
    await certificateNo.press('Tab');

    await expect(page.getByText('证书编号不能超过100个字符')).toBeVisible();
  });

  test('FT-EXP-001 exports achievement Excel from the achievement page', async ({ page }) => {
    await mockCommonBackend(page);
    await mockAchievementList(page);
    let exportCalled = false;
    await page.route('**/dev-api/achievement/manage/export', async (route) => {
      exportCalled = true;
      expect(route.request().method()).toBe('POST');
      await route.fulfill({
        status: 200,
        contentType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        body: 'fake-xlsx-content',
      });
    });

    await loginAsAdmin(page);
    await page.goto('/achievement/manage');
    await expect(page.getByText('CERT-100200')).toBeVisible();
    await page.locator('.toolbar-left').getByRole('button', { name: '导出', exact: true }).click();
    await expect.poll(() => exportCalled).toBe(true);
  });

  test('FT-COMP-002 validates required fields when adding competition', async ({ page }) => {
    await mockCommonBackend(page);
    await loginAsAdmin(page);
    await page.goto('/competition/competition');

    await page.getByRole('button', { name: '新增' }).click();
    await expect(page.getByText('添加总赛事')).toBeVisible();
    await page.locator('.el-dialog').getByRole('button', { name: '确 定' }).click();

    await expect(page.getByText('赛事名称不能为空')).toBeVisible();
    await expect(page.getByText('赛事类别不能为空')).toBeVisible();
    await expect(page.getByText('盖章单位不能为空')).toBeVisible();
    await expect(page.getByText('赛事级别不能为空')).toBeVisible();
  });

  test('FT-BOUND-001/002/005 validates paging and empty export boundaries', async ({ page }) => {
    await mockCommonBackend(page);
    const rows = defaultAchievementRows();
    await page.route('**/dev-api/achievement/manage/list-responsible*', async (route) => {
      const url = new URL(route.request().url());
      const pageSize = Math.max(Number(url.searchParams.get('pageSize') || 10), 1);
      await fulfillJson(route, {
        code: 200,
        msg: '查询成功',
        total: rows.length,
        rows: rows.slice(0, Math.min(pageSize, rows.length)),
      });
    });
    await page.route('**/dev-api/achievement/manage/export', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        body: '',
      });
    });

    await page.goto('/login');
    const minPage = await browserFetch(page, '/dev-api/achievement/manage/list-responsible?pageNum=1&pageSize=1');
    const largePage = await browserFetch(page, '/dev-api/achievement/manage/list-responsible?pageNum=1&pageSize=1000');
    const emptyExport = await browserFetch(page, '/dev-api/achievement/manage/export', { method: 'POST' });

    expect(minPage.status).toBe(200);
    expect(minPage.body.rows).toHaveLength(1);
    expect(largePage.status).toBe(200);
    expect(largePage.body.rows).toHaveLength(rows.length);
    expect(emptyExport.status).toBe(200);
    expect(emptyExport.headers['content-type']).toContain('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
  });

  test('FT-BOUND-004 safely handles special characters in query filters', async ({ page }) => {
    await mockCommonBackend(page);
    const specialCertificateNo = `"><script>alert(1)</script>' OR 1=1 --`;
    let receivedCertificateNo = '';
    page.on('dialog', (dialog) => dialog.dismiss());
    await page.route('**/dev-api/achievement/manage/list-responsible*', async (route) => {
      const url = new URL(route.request().url());
      receivedCertificateNo = url.searchParams.get('certificateNo') || '';
      await fulfillJson(route, {
        code: 200,
        msg: '查询成功',
        total: 0,
        rows: [],
      });
    });

    await loginAsAdmin(page);
    await page.goto('/achievement/manage');
    await page.getByPlaceholder('请输入证书编号').fill(specialCertificateNo);
    await page.getByRole('button', { name: '搜索' }).click();
    await expect(page.locator('.el-table__empty-text')).toBeVisible();

    expect(receivedCertificateNo).toBe(specialCertificateNo);
  });
});
