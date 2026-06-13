import { expect } from '@playwright/test';

export async function mockCommonBackend(page) {
  // Set E2E test flag in browser context
  await page.addInitScript(() => {
    window.__E2E_TEST__ = true;
  });

  // Log all network requests and response details
  page.on('request', req => console.log('>>', req.method(), req.url()));
  page.on('response', async res => {
    const url = res.url();
    if (res.status() >= 400) {
      console.log('<< HTTP ERROR', res.status(), url);
    }
    if (url.includes('/dev-api/')) {
      try {
        const text = await res.text();
        const json = JSON.parse(text);
        if (json && json.code !== 200) {
          console.log('<< API ERROR', url, '->', text);
        }
      } catch (e) {
        // Not a JSON response or already read
      }
    }
  });

  // 1. Mock Captcha Image
  await page.route('**/dev-api/captchaImage', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        captchaEnabled: false,
        img: "",
        uuid: ""
      }),
    });
  });

  // 2. Mock Login
  await page.route('**/dev-api/login', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        token: "mocked-jwt-token-xyz"
      }),
    });
  });

  // 3. Mock getInfo
  await page.route('**/dev-api/getInfo', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        permissions: ["*:*:*"],
        roles: ["admin"],
        user: {
          userId: 1,
          userName: "admin",
          nickName: "管理员",
          avatar: "",
          phonenumber: "13800000000",
          email: "admin@ruoyi.vip",
          profileInitialized: 1
        }
      }),
    });
  });

  // 4. Mock getRouters with default constant system menus
  await page.route('**/dev-api/getRouters', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        data: [
          {
            name: "Competition",
            path: "/competition",
            hidden: false,
            redirect: "noRedirect",
            component: "Layout",
            alwaysShow: true,
            meta: { title: "赛事管理", icon: "tab", noCache: false, link: null },
            children: [
              {
                name: "CompetitionIndex",
                path: "competition",
                hidden: false,
                component: "competition/competition/index",
                meta: { title: "总赛事", icon: "list", noCache: false, link: null }
              }
            ]
          },
          {
            name: "Achievement",
            path: "/achievement",
            hidden: false,
            redirect: "noRedirect",
            component: "Layout",
            alwaysShow: true,
            meta: { title: "成果管理", icon: "tab", noCache: false, link: null },
            children: [
              {
                name: "AchievementManage",
                path: "manage",
                hidden: false,
                component: "achievement/manage/index",
                meta: { title: "成果录入", icon: "list", noCache: false, link: null }
              }
            ]
          },
          {
            name: "CompetitionApply",
            path: "/competition-apply",
            hidden: false,
            redirect: "noRedirect",
            component: "Layout",
            alwaysShow: true,
            meta: { title: "比赛申请", icon: "tab", noCache: false, link: null },
            children: [
              {
                name: "CompetitionApplyIndex",
                path: "competitionapply",
                hidden: false,
                component: "competition-apply/competitionapply/index",
                meta: { title: "申请列表", icon: "list", noCache: false, link: null }
              }
            ]
          },
          {
            name: "CompetitionApplyReview",
            path: "/competitionapply-review",
            hidden: false,
            redirect: "noRedirect",
            component: "Layout",
            alwaysShow: true,
            meta: { title: "申请审核", icon: "tab", noCache: false, link: null },
            children: [
              {
                name: "CompetitionApplyReviewIndex",
                path: "index",
                hidden: false,
                component: "competitionapply-review/index",
                meta: { title: "审核列表", icon: "list", noCache: false, link: null }
              }
            ]
          },
          {
            name: "Reimbursement",
            path: "/reimbursement",
            hidden: false,
            redirect: "noRedirect",
            component: "Layout",
            alwaysShow: true,
            meta: { title: "报销管理", icon: "tab", noCache: false, link: null },
            children: [
              {
                name: "ReimbursementIndex",
                path: "Reimbursement",
                hidden: false,
                component: "system/Reimbursement/index",
                meta: { title: "报销项目详情", icon: "list", noCache: false, link: null }
              }
            ]
          }
        ]
      }),
    });
  });

  // 5. Mock dictionary data APIs
  await page.route('**/dev-api/system/dict/data/type/*', async (route) => {
    const url = route.request().url();
    const lastSlashIndex = url.lastIndexOf('/');
    let type = url.substring(lastSlashIndex + 1);
    const questionMarkIndex = type.indexOf('?');
    if (questionMarkIndex !== -1) {
      type = type.substring(0, questionMarkIndex);
    }
    
    let dictData = [];
    if (type === 'sys_competition_category') {
      dictData = [{ dictLabel: 'A类赛事', dictValue: 'X' }, { dictLabel: 'B类赛事', dictValue: 'Y' }];
    } else if (type === 'sys_competition_level') {
      dictData = [{ dictLabel: '国家级', dictValue: 'A' }, { dictLabel: '省部级', dictValue: 'B' }];
    } else if (type === 'sys_competition_tag') {
      dictData = [{ dictLabel: '创新创业', dictValue: 'T' }, { dictLabel: '学科竞赛', dictValue: 'D' }];
    } else if (type === 'sys_competition_scope_type') {
      dictData = [{ dictLabel: '全校', dictValue: '0' }, { dictLabel: '限院系', dictValue: '1' }];
    } else if (type === 'sys_competition_status') {
      dictData = [{ dictLabel: '启用', dictValue: '0' }, { dictLabel: '禁用', dictValue: '1' }];
    } else if (type === 'sys_competition_del_flag') {
      dictData = [{ dictLabel: '正常', dictValue: '0' }, { dictLabel: '删除', dictValue: '1' }];
    } else if (type === 'achievement_category') {
      dictData = [{ dictLabel: '学术论文', dictValue: '1' }, { dictLabel: '专利/软件著作权', dictValue: '2' }];
    } else if (type === 'group_type') {
      dictData = [{ dictLabel: '学生组', dictValue: '1' }, { dictLabel: '教师组', dictValue: '2' }];
    } else if (type === 'award_rank') {
      dictData = [{ dictLabel: '特等奖', dictValue: '1' }, { dictLabel: '一等奖', dictValue: '2' }, { dictLabel: '二等奖', dictValue: '3' }];
    } else if (type === 'award_level_type') {
      dictData = [{ dictLabel: '国家级', dictValue: '1' }, { dictLabel: '省部级', dictValue: '2' }];
    } else if (type === 'college_audit_status') {
      dictData = [{ dictLabel: '待审核', dictValue: '0' }, { dictLabel: '审核通过', dictValue: '2' }];
    } else if (type === 'school_audit_status') {
      dictData = [{ dictLabel: '待审核', dictValue: '2' }, { dictLabel: '审核通过', dictValue: '1' }];
    } else if (type === 'college_reason' || type === 'school_reason') {
      dictData = [{ dictLabel: '附件模糊', dictValue: '1' }, { dictLabel: '信息不全', dictValue: '2' }];
    } else if (type === 'sys_shenhe_status') {
      dictData = [{ dictLabel: '待审核', dictValue: '0' }, { dictLabel: '通过', dictValue: '1' }, { dictLabel: '拒绝', dictValue: '2' }];
    } else if (type === 'attach_type') {
      dictData = [{ dictLabel: '附件类型A', dictValue: '1' }, { dictLabel: '附件类型B', dictValue: '2' }];
    } else if (type === 'reimbursement_status') {
      dictData = [{ dictLabel: '待审核', dictValue: '0' }, { dictLabel: '已划拨', dictValue: '1' }, { dictLabel: '已拒绝', dictValue: '2' }];
    }

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        data: dictData
      }),
    });
  });

  // 6. Mock Department List
  await page.route('**/dev-api/system/dept/list', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        data: [
          { deptId: 100, deptName: "计算机学院" }
        ]
      }),
    });
  });

  // 7. Mock Competition Option List
  await page.route('**/dev-api/competition/competition/optionList', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        data: [
          { id: 1, name: "全国电子设计竞赛" }
        ]
      }),
    });
  });

  // 8. Mock Homepage Dashboard Stats
  await page.route('**/dev-api/achievement/manage/stats/dashboard', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        data: {
          totalCount: 10,
          pendingCount: 2,
          approvedCount: 8
        }
      }),
    });
  });

  await page.route('**/dev-api/achievement/manage/stats/year*', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        data: [
          { year: 2024, count: 2 },
          { year: 2025, count: 3 },
          { year: 2026, count: 5 }
        ]
      }),
    });
  });

  // 9. Mock Homepage Notice List
  await page.route('**/dev-api/system/notice/list*', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        total: 0,
        rows: []
      }),
    });
  });

  // 10. Mock Competition List
  await page.route('**/dev-api/competition/competition/list*', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        total: 1,
        rows: [
          { id: 1, name: "全国电子设计竞赛" }
        ]
      }),
    });
  });

  // 11. Mock Session List
  await page.route('**/dev-api/session/session/list*', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        total: 1,
        rows: [
          { id: 1, session: "第一届", level: "1", year: "2026" }
        ]
      }),
    });
  });

  // 12. Mock listTracks
  await page.route('**/dev-api/achievement/manage/listTracks*', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        data: ["单片机赛道", "嵌入式赛道"]
      }),
    });
  });

  // 13. Mock student list
  await page.route('**/dev-api/student/student/list*', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        msg: "操作成功",
        code: 200,
        rows: [
          {
            studentId: 1,
            no: "admin",
            name: "管理员",
            school: 100,
            department: 101,
            major: 102,
            className: "计算机2201",
            classYear: "2022"
          }
        ],
        total: 1
      }),
    });
  });

  // 14. Mock certificate uniqueness check
  await page.route('**/dev-api/achievement/manage/checkCertificateNoUnique*', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        code: 200,
        msg: "操作成功",
        data: true
      }),
    });
  });
}

export async function loginAsAdmin(page) {
  await page.goto('/login');
  await page.getByPlaceholder('账号').fill('admin');
  await page.getByPlaceholder('密码').fill('admin123');
  await page.getByRole('button', { name: '登 录' }).click();
  // Wait for sidebar
  await expect(page.locator('.sidebar-container')).toBeVisible({ timeout: 10000 });
}
