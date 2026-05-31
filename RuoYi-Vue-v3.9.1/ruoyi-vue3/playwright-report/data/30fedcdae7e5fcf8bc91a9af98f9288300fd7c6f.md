# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: apply.spec.js >> Competition Application & Review E2E Tests >> should allow reviewing a pending application
- Location: e2e\apply.spec.js:129:3

# Error details

```
Test timeout of 30000ms exceeded.
```

```
Error: locator.click: Test timeout of 30000ms exceeded.
Call log:
  - waiting for locator('.el-message-box__btns button').last()

```

# Page snapshot

```yaml
- generic [ref=e1]:
  - generic [ref=e3]:
    - generic [ref=e4]:
      - link "成果管理系统" [ref=e6] [cursor=pointer]:
        - /url: /
        - img [ref=e7]
        - heading "成果管理系统" [level=1] [ref=e8]
      - menubar [ref=e12]:
        - link "首页" [ref=e14] [cursor=pointer]:
          - /url: /index
          - menuitem "首页" [ref=e15]:
            - img [ref=e16]
            - generic [ref=e18]: 首页
        - menuitem "赛事管理" [ref=e20]:
          - generic [ref=e21] [cursor=pointer]:
            - img [ref=e22]
            - generic [ref=e24]: 赛事管理
            - img [ref=e26]
        - menuitem "成果管理" [ref=e29]:
          - generic [ref=e30] [cursor=pointer]:
            - img [ref=e31]
            - generic [ref=e33]: 成果管理
            - img [ref=e35]
        - menuitem "比赛申请" [ref=e38]:
          - generic [ref=e39] [cursor=pointer]:
            - img [ref=e40]
            - generic [ref=e42]: 比赛申请
            - img [ref=e44]
        - menuitem "申请审核" [expanded] [ref=e47]:
          - generic [ref=e48] [cursor=pointer]:
            - img [ref=e49]
            - generic [ref=e51]: 申请审核
            - img [ref=e53]
          - menu [ref=e55]:
            - link "审核列表" [ref=e57] [cursor=pointer]:
              - /url: /competitionapply-review/index
              - menuitem "审核列表" [ref=e58]:
                - img [ref=e59]
                - generic [ref=e61]: 审核列表
        - menuitem "报销管理" [ref=e63]:
          - generic [ref=e64] [cursor=pointer]:
            - img [ref=e65]
            - generic [ref=e67]: 报销管理
            - img [ref=e69]
    - generic [ref=e71]:
      - generic [ref=e72]:
        - generic [ref=e73]:
          - img [ref=e75] [cursor=pointer]
          - navigation "面包屑" [ref=e77]:
            - generic [ref=e78]:
              - link "首页" [ref=e79]
              - text: /
            - generic [ref=e80]:
              - link "申请审核" [ref=e81]
              - text: /
            - link "审核列表" [ref=e83]
          - generic [ref=e84]:
            - img [ref=e86] [cursor=pointer]
            - img [ref=e89] [cursor=pointer]
            - img [ref=e92] [cursor=pointer]
            - button [ref=e96] [cursor=pointer]:
              - img [ref=e97]
            - button "管理员" [ref=e100] [cursor=pointer]:
              - img [ref=e101]
              - text: 管理员
        - generic [ref=e105]:
          - link "首页" [ref=e106] [cursor=pointer]:
            - /url: /index
          - link "审核列表" [ref=e107] [cursor=pointer]:
            - /url: /competitionapply-review/index
            - text: 审核列表
            - img [ref=e109]
      - generic [ref=e112]:
        - generic [ref=e113]:
          - generic [ref=e114]:
            - generic [ref=e115]: 申请人学号
            - textbox "申请人学号" [ref=e119]:
              - /placeholder: 请输入申请人学号
          - generic [ref=e120]:
            - generic [ref=e121]: 申请人学院
            - textbox "申请人学院" [ref=e125]:
              - /placeholder: 请输入申请人学院
          - generic [ref=e126]:
            - generic [ref=e127]: 赛事名称
            - textbox "赛事名称" [ref=e131]:
              - /placeholder: 请输入赛事名称
          - generic [ref=e132]:
            - generic [ref=e133]: 赛事类别
            - generic [ref=e136] [cursor=pointer]:
              - generic:
                - combobox "赛事类别" [ref=e138]
                - generic [ref=e139]: 请选择赛事类别
              - img [ref=e142]
          - generic [ref=e144]:
            - generic [ref=e145]: 赛事级别
            - generic [ref=e148] [cursor=pointer]:
              - generic:
                - combobox "赛事级别" [ref=e150]
                - generic [ref=e151]: 请选择赛事级别
              - img [ref=e154]
          - generic [ref=e156]:
            - generic [ref=e157]: 适用范围
            - generic [ref=e160] [cursor=pointer]:
              - generic:
                - combobox "适用范围" [ref=e162]
                - generic [ref=e163]: 请选择适用范围
              - img [ref=e166]
          - generic [ref=e168]:
            - generic [ref=e169]: 审核范围
            - generic [ref=e172] [cursor=pointer]:
              - generic:
                - combobox "审核范围" [ref=e174]
                - generic [ref=e175]: 未审核
              - img [ref=e178]
          - generic [ref=e180]:
            - generic [ref=e181]: 审核状态
            - generic [ref=e184]:
              - generic:
                - combobox "审核状态" [disabled] [ref=e186]
                - generic [ref=e187]: 待审核
              - generic:
                - generic:
                  - img
          - generic [ref=e189]:
            - button "搜索" [ref=e190] [cursor=pointer]:
              - img [ref=e192]
              - generic [ref=e194]: 搜索
            - button "重置" [ref=e195] [cursor=pointer]:
              - img [ref=e197]
              - generic [ref=e199]: 重置
        - generic [ref=e202]:
          - button [ref=e203] [cursor=pointer]:
            - img [ref=e205]
          - button [ref=e207] [cursor=pointer]:
            - img [ref=e209]
        - generic [ref=e212]:
          - table [ref=e214]:
            - rowgroup [ref=e230]:
              - row "选择所有行 申请人学号 申请人学院 赛事名称 年份 届次 赛事类别 盖章单位 赛事级别 适用范围 审核状态 标签 赛事说明 操作" [ref=e231]:
                - columnheader "选择所有行" [ref=e232]:
                  - generic "选择所有行" [ref=e234] [cursor=pointer]:
                    - generic [ref=e235]:
                      - checkbox "选择所有行"
                - columnheader "申请人学号" [ref=e237]:
                  - generic [ref=e238]: 申请人学号
                - columnheader "申请人学院" [ref=e239]:
                  - generic [ref=e240]: 申请人学院
                - columnheader "赛事名称" [ref=e241]:
                  - generic [ref=e242]: 赛事名称
                - columnheader "年份" [ref=e243]:
                  - generic [ref=e244]: 年份
                - columnheader "届次" [ref=e245]:
                  - generic [ref=e246]: 届次
                - columnheader "赛事类别" [ref=e247]:
                  - generic [ref=e248]: 赛事类别
                - columnheader "盖章单位" [ref=e249]:
                  - generic [ref=e250]: 盖章单位
                - columnheader "赛事级别" [ref=e251]:
                  - generic [ref=e252]: 赛事级别
                - columnheader "适用范围" [ref=e253]:
                  - generic [ref=e254]: 适用范围
                - columnheader "审核状态" [ref=e255]:
                  - generic [ref=e256]: 审核状态
                - columnheader "标签" [ref=e257]:
                  - generic [ref=e258]: 标签
                - columnheader "赛事说明" [ref=e259]:
                  - generic [ref=e260]: 赛事说明
                - columnheader "操作" [ref=e261]:
                  - generic [ref=e262]: 操作
          - table [ref=e267]:
            - rowgroup [ref=e283]:
              - row "选择当前行 待审核 审核" [ref=e284]:
                - cell "选择当前行" [ref=e285]:
                  - generic "选择当前行" [ref=e287] [cursor=pointer]:
                    - generic [ref=e288]:
                      - checkbox "选择当前行"
                - cell [ref=e290]
                - cell [ref=e291]
                - cell [ref=e292]
                - cell [ref=e293]
                - cell [ref=e294]
                - cell [ref=e295]
                - cell [ref=e296]
                - cell [ref=e297]
                - cell [ref=e298]
                - cell "待审核" [ref=e299]:
                  - generic [ref=e303]: 待审核
                - cell [ref=e304]
                - cell [ref=e305]
                - cell "审核" [ref=e306]:
                  - button "审核" [ref=e308] [cursor=pointer]:
                    - img [ref=e310]
                    - generic [ref=e313]: 审核
        - generic [ref=e315]:
          - generic [ref=e316]: 共 1 条
          - generic [ref=e319] [cursor=pointer]:
            - generic:
              - combobox [ref=e321]
              - generic [ref=e322]: 10条/页
            - img [ref=e325]
          - button "上一页" [disabled] [ref=e327]:
            - generic:
              - img
          - list [ref=e328]:
            - listitem "第 1 页" [ref=e329]: "1"
          - button "下一页" [disabled] [ref=e330]:
            - generic:
              - img
          - generic [ref=e331]:
            - generic [ref=e332]: 前往
            - spinbutton "页" [ref=e335]: "1"
            - generic [ref=e336]: 页
  - img
  - dialog "赛事申请审核" [ref=e338]:
    - generic [active] [ref=e339]:
      - banner [ref=e340]:
        - heading "赛事申请审核" [level=2] [ref=e341]
        - button "关闭此对话框" [ref=e342] [cursor=pointer]:
          - img [ref=e344]
      - generic [ref=e347]:
        - generic [ref=e348]:
          - generic [ref=e349]:
            - generic [ref=e350]:
              - generic [ref=e351]: "* 赛事名称"
              - textbox "* 赛事名称" [ref=e355]:
                - /placeholder: 请输入赛事名称
                - text: 全国电子设计竞赛
            - generic [ref=e356]:
              - generic [ref=e357]: 年份
              - generic [ref=e359]:
                - button "减少数值" [ref=e360] [cursor=pointer]:
                  - img [ref=e362]
                - button "增加数值" [ref=e364] [cursor=pointer]:
                  - img [ref=e366]
                - spinbutton "年份" [ref=e370]: "2026"
            - generic [ref=e371]:
              - generic [ref=e372]: 届次
              - textbox "届次" [ref=e376]:
                - /placeholder: 例如：2025、十二届
                - text: 第一届
            - generic [ref=e377]:
              - generic [ref=e378]: "* 赛事类别"
              - radiogroup "* 赛事类别" [ref=e380]:
                - generic [ref=e381] [cursor=pointer]:
                  - radio "A类赛事" [checked] [ref=e383]
                  - generic [ref=e385]: A类赛事
                - generic [ref=e386] [cursor=pointer]:
                  - radio "B类赛事" [ref=e388]
                  - generic [ref=e390]: B类赛事
            - generic [ref=e391]:
              - generic [ref=e392]: "* 盖章单位"
              - textbox "* 盖章单位" [ref=e396]:
                - /placeholder: 请输入盖章单位
                - text: 教育部
            - generic [ref=e397]:
              - generic [ref=e398]: "* 赛事级别"
              - radiogroup "* 赛事级别" [ref=e400]:
                - generic [ref=e401] [cursor=pointer]:
                  - radio "国家级" [checked] [ref=e403]
                  - generic [ref=e405]: 国家级
                - generic [ref=e406] [cursor=pointer]:
                  - radio "省部级" [ref=e408]
                  - generic [ref=e410]: 省部级
            - generic [ref=e411]:
              - generic [ref=e412]: 标签
              - group "标签" [ref=e414]:
                - generic [ref=e415] [cursor=pointer]:
                  - generic [ref=e416]:
                    - checkbox "创新创业"
                  - generic [ref=e418]: 创新创业
                - generic [ref=e419] [cursor=pointer]:
                  - generic [ref=e420]:
                    - checkbox "学科竞赛"
                  - generic [ref=e422]: 学科竞赛
            - generic [ref=e423]:
              - generic [ref=e424]: 赛事说明
              - textbox "赛事说明" [ref=e427]:
                - /placeholder: 请输入赛事说明
          - generic [ref=e429]:
            - generic [ref=e430]: 审核意见
            - textbox "审核意见" [ref=e433]:
              - /placeholder: 驳回必填；通过可选
        - generic [ref=e435]:
          - separator [ref=e436]:
            - generic [ref=e437]: 附件管理
          - generic [ref=e438]:
            - tablist [ref=e442]:
              - tab "附件类型A" [selected] [ref=e444]
              - tab "附件类型B" [ref=e445]
            - tabpanel "附件类型A" [ref=e447]:
              - alert [ref=e449]:
                - generic [ref=e451]: 请上传奖状 PDF 文件
      - contentinfo [ref=e452]:
        - generic [ref=e453]:
          - button "通过" [ref=e454] [cursor=pointer]:
            - generic [ref=e455]: 通过
          - button "驳回" [ref=e456] [cursor=pointer]:
            - generic [ref=e457]: 驳回
          - button "取消" [ref=e458] [cursor=pointer]:
            - generic [ref=e459]: 取消
```

# Test source

```ts
  47  |           code: 200,
  48  |           msg: "查询成功"
  49  |         })
  50  |       });
  51  |     });
  52  | 
  53  |     // 4. Mock GET single application details (for edit/review loading)
  54  |     await page.route('**/dev-api/competition-apply/competitionapply/50*', async (route) => {
  55  |       await route.fulfill({
  56  |         status: 200,
  57  |         contentType: 'application/json',
  58  |         body: JSON.stringify({
  59  |           code: 200,
  60  |           msg: "查询成功",
  61  |           data: {
  62  |             id: 50,
  63  |             competitionId: 1,
  64  |             name: "全国电子设计竞赛",
  65  |             year: 2026,
  66  |             session: "第一届",
  67  |             category: "X",
  68  |             organizations: "教育部",
  69  |             level: "A",
  70  |             teamName: "逐梦团队",
  71  |             studentName: "张三",
  72  |             studentNo: "S1001",
  73  |             status: "0"
  74  |           }
  75  |         })
  76  |       });
  77  |     });
  78  | 
  79  |     // 5. Mock POST (add) application
  80  |     await page.route('**/dev-api/competition-apply/competitionapply', async (route) => {
  81  |       if (route.request().method() === 'POST') {
  82  |         isAddCalled = true;
  83  |         await route.fulfill({
  84  |           status: 200,
  85  |           contentType: 'application/json',
  86  |           body: JSON.stringify({ code: 200, msg: "申请成功" })
  87  |         });
  88  |       }
  89  |     });
  90  | 
  91  |     // 6. Mock PUT review application
  92  |     await page.route('**/dev-api/competition-apply/competitionapply/review/50', async (route) => {
  93  |       if (route.request().method() === 'PUT') {
  94  |         isReviewCalled = true;
  95  |         await route.fulfill({
  96  |           status: 200,
  97  |           contentType: 'application/json',
  98  |           body: JSON.stringify({ code: 200, msg: "审核操作成功" })
  99  |         });
  100 |       }
  101 |     });
  102 | 
  103 |     // Perform login
  104 |     await loginAsAdmin(page);
  105 |   });
  106 | 
  107 |   test('should submit a competition application successfully', async ({ page }) => {
  108 |     // Navigate to Competition Apply page
  109 |     await page.goto('/competition-apply/competitionapply');
  110 | 
  111 |     // Click "新增" or "申请" button
  112 |     await page.getByRole('button', { name: '新增' }).click();
  113 | 
  114 |     // Verify application modal opens
  115 |     const dialog = page.locator('.el-dialog');
  116 |     await expect(dialog).toBeVisible();
  117 | 
  118 |     // Fill application form details (name select input, year, session)
  119 |     await dialog.getByPlaceholder('请选择已有赛事或直接输入新的赛事名称').fill('全国电子设计竞赛');
  120 |     await dialog.getByPlaceholder('例如：2025、十二届').fill('第一届');
  121 | 
  122 |     // Submit
  123 |     const submitBtn = dialog.getByRole('button', { name: '确 定' }).or(dialog.getByRole('button', { name: '确定' })).first();
  124 |     await submitBtn.click();
  125 | 
  126 |     expect(isAddCalled).toBe(true);
  127 |   });
  128 | 
  129 |   test('should allow reviewing a pending application', async ({ page }) => {
  130 |     // Navigate to Review page
  131 |     await page.goto('/competitionapply-review/index');
  132 | 
  133 |     // Wait for application list to render and click "审核" button in the table row
  134 |     const reviewBtn = page.locator('.el-table__body').getByRole('button', { name: '审核' }).first();
  135 |     await reviewBtn.click();
  136 | 
  137 |     // Assert review dialog modal is visible
  138 |     const dialog = page.locator('.el-dialog');
  139 |     await expect(dialog).toBeVisible();
  140 | 
  141 |     // Click "通过" or "同意" or "审核通过" button
  142 |     const approveBtn = dialog.getByRole('button', { name: '通过' }).or(dialog.getByRole('button', { name: '同意' })).first();
  143 |     await approveBtn.click({ force: true });
  144 | 
  145 |     // Confirm the action in the popup dialog box
  146 |     const confirmBtn = page.locator('.el-message-box__btns button').last();
> 147 |     await confirmBtn.click();
      |                      ^ Error: locator.click: Test timeout of 30000ms exceeded.
  148 | 
  149 |     expect(isReviewCalled).toBe(true);
  150 |   });
  151 | });
  152 | 
```