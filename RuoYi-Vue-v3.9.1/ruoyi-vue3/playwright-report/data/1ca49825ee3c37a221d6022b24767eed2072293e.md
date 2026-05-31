# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: apply.spec.js >> Competition Application & Review E2E Tests >> should submit a competition application successfully
- Location: e2e\apply.spec.js:107:3

# Error details

```
Test timeout of 30000ms exceeded.
```

```
Error: locator.fill: Test timeout of 30000ms exceeded.
Call log:
  - waiting for locator('.el-dialog').getByPlaceholder('请选择已有赛事或直接输入新的赛事名称')

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
        - menuitem "比赛申请" [expanded] [ref=e38]:
          - generic [ref=e39] [cursor=pointer]:
            - img [ref=e40]
            - generic [ref=e42]: 比赛申请
            - img [ref=e44]
          - menu [ref=e46]:
            - link "申请列表" [ref=e48] [cursor=pointer]:
              - /url: /competition-apply/competitionapply
              - menuitem "申请列表" [ref=e49]:
                - img [ref=e50]
                - generic [ref=e52]: 申请列表
        - menuitem "申请审核" [ref=e54]:
          - generic [ref=e55] [cursor=pointer]:
            - img [ref=e56]
            - generic [ref=e58]: 申请审核
            - img [ref=e60]
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
              - link "比赛申请" [ref=e81]
              - text: /
            - link "申请列表" [ref=e83]
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
          - link "申请列表" [ref=e107] [cursor=pointer]:
            - /url: /competition-apply/competitionapply
            - text: 申请列表
            - img [ref=e109]
      - generic [ref=e112]:
        - generic [ref=e113]:
          - generic [ref=e114]:
            - generic [ref=e115]: 赛事名称
            - textbox "赛事名称" [ref=e119]:
              - /placeholder: 请输入赛事名称
          - generic [ref=e120]:
            - generic [ref=e121]: 赛事类别
            - generic [ref=e124] [cursor=pointer]:
              - generic:
                - combobox "赛事类别" [ref=e126]
                - generic [ref=e127]: 请选择赛事类别
              - img [ref=e130]
          - generic [ref=e132]:
            - generic [ref=e133]: 赛事级别
            - generic [ref=e136] [cursor=pointer]:
              - generic:
                - combobox "赛事级别" [ref=e138]
                - generic [ref=e139]: 请选择赛事级别
              - img [ref=e142]
          - generic [ref=e144]:
            - generic [ref=e145]: 适用范围
            - generic [ref=e148] [cursor=pointer]:
              - generic:
                - combobox "适用范围" [ref=e150]
                - generic [ref=e151]: 请选择适用范围
              - img [ref=e154]
          - generic [ref=e156]:
            - generic [ref=e157]: 审核状态
            - generic [ref=e160] [cursor=pointer]:
              - generic:
                - combobox "审核状态" [ref=e162]
                - generic [ref=e163]: 请选择审核状态
              - img [ref=e166]
          - generic [ref=e169]:
            - button "搜索" [ref=e170] [cursor=pointer]:
              - img [ref=e172]
              - generic [ref=e174]: 搜索
            - button "重置" [ref=e175] [cursor=pointer]:
              - img [ref=e177]
              - generic [ref=e179]: 重置
        - generic [ref=e180]:
          - button "新增" [ref=e182] [cursor=pointer]:
            - img [ref=e184]
            - generic [ref=e186]: 新增
          - button "修改" [disabled] [ref=e188]:
            - img [ref=e190]
            - generic [ref=e193]: 修改
          - button "删除" [disabled] [ref=e195]:
            - img [ref=e197]
            - generic [ref=e199]: 删除
          - button "导出" [ref=e201] [cursor=pointer]:
            - img [ref=e203]
            - generic [ref=e205]: 导出
          - generic [ref=e207]:
            - button [ref=e208] [cursor=pointer]:
              - img [ref=e210]
            - button [ref=e212] [cursor=pointer]:
              - img [ref=e214]
        - generic [ref=e217]:
          - table [ref=e219]:
            - rowgroup [ref=e235]:
              - row "选择所有行 申请人学号 申请人学院 赛事名称 年份 届次 赛事类别 盖章单位 赛事级别 适用范围 审核状态 标签 赛事说明 操作" [ref=e236]:
                - columnheader "选择所有行" [ref=e237]:
                  - generic "选择所有行" [ref=e239] [cursor=pointer]:
                    - generic [ref=e240]:
                      - checkbox "选择所有行"
                - columnheader "申请人学号" [ref=e242]:
                  - generic [ref=e243]: 申请人学号
                - columnheader "申请人学院" [ref=e244]:
                  - generic [ref=e245]: 申请人学院
                - columnheader "赛事名称" [ref=e246]:
                  - generic [ref=e247]: 赛事名称
                - columnheader "年份" [ref=e248]:
                  - generic [ref=e249]: 年份
                - columnheader "届次" [ref=e250]:
                  - generic [ref=e251]: 届次
                - columnheader "赛事类别" [ref=e252]:
                  - generic [ref=e253]: 赛事类别
                - columnheader "盖章单位" [ref=e254]:
                  - generic [ref=e255]: 盖章单位
                - columnheader "赛事级别" [ref=e256]:
                  - generic [ref=e257]: 赛事级别
                - columnheader "适用范围" [ref=e258]:
                  - generic [ref=e259]: 适用范围
                - columnheader "审核状态" [ref=e260]:
                  - generic [ref=e261]: 审核状态
                - columnheader "标签" [ref=e262]:
                  - generic [ref=e263]: 标签
                - columnheader "赛事说明" [ref=e264]:
                  - generic [ref=e265]: 赛事说明
                - columnheader "操作" [ref=e266]:
                  - generic [ref=e267]: 操作
          - table [ref=e272]:
            - rowgroup [ref=e288]:
              - row "选择当前行 待审核 修改 删除" [ref=e289]:
                - cell "选择当前行" [ref=e290]:
                  - generic "选择当前行" [ref=e292] [cursor=pointer]:
                    - generic [ref=e293]:
                      - checkbox "选择当前行"
                - cell [ref=e295]
                - cell [ref=e296]
                - cell [ref=e297]
                - cell [ref=e298]
                - cell [ref=e299]
                - cell [ref=e300]
                - cell [ref=e301]
                - cell [ref=e302]
                - cell [ref=e303]
                - cell "待审核" [ref=e304]:
                  - generic [ref=e308]: 待审核
                - cell [ref=e309]
                - cell [ref=e310]
                - cell "修改 删除" [ref=e311]:
                  - generic [ref=e312]:
                    - button "修改" [ref=e313] [cursor=pointer]:
                      - img [ref=e315]
                      - generic [ref=e318]: 修改
                    - button "删除" [ref=e319] [cursor=pointer]:
                      - img [ref=e321]
                      - generic [ref=e323]: 删除
        - generic [ref=e325]:
          - generic [ref=e326]: 共 1 条
          - generic [ref=e329] [cursor=pointer]:
            - generic:
              - combobox [ref=e331]
              - generic [ref=e332]: 10条/页
            - img [ref=e335]
          - button "上一页" [disabled] [ref=e337]:
            - generic:
              - img
          - list [ref=e338]:
            - listitem "第 1 页" [ref=e339]: "1"
          - button "下一页" [disabled] [ref=e340]:
            - generic:
              - img
          - generic [ref=e341]:
            - generic [ref=e342]: 前往
            - spinbutton "页" [ref=e345]: "1"
            - generic [ref=e346]: 页
  - img
  - dialog "添加赛事申请" [ref=e348]:
    - generic [active] [ref=e349]:
      - banner [ref=e350]:
        - heading "添加赛事申请" [level=2] [ref=e351]
        - button "关闭此对话框" [ref=e352] [cursor=pointer]:
          - img [ref=e354]
      - generic [ref=e357]:
        - generic [ref=e359]:
          - generic [ref=e360]:
            - generic [ref=e361]: "* 赛事名称"
            - generic [ref=e364]:
              - generic [ref=e365]:
                - combobox "* 赛事名称" [ref=e367]
                - generic [ref=e368]: 请选择已有赛事或直接输入新的赛事名称
              - img [ref=e371] [cursor=pointer]
          - generic [ref=e373]:
            - generic [ref=e374]: "* 年份"
            - generic [ref=e376]:
              - button "减少数值" [ref=e377] [cursor=pointer]:
                - img [ref=e379]
              - button "增加数值" [ref=e381] [cursor=pointer]:
                - img [ref=e383]
              - spinbutton "* 年份" [ref=e387]: "2026"
          - generic [ref=e388]:
            - generic [ref=e389]: "* 届次"
            - generic [ref=e390]:
              - textbox "* 届次" [ref=e393]:
                - /placeholder: 例如：2025、十二届
              - generic [ref=e394]: 请按照奖状/证书上的届次填写（如：第十五届）
        - generic [ref=e396]:
          - separator [ref=e397]:
            - generic [ref=e398]: 附件管理
          - generic [ref=e399]:
            - tablist [ref=e403]:
              - tab "附件类型A" [selected] [ref=e405]:
                - generic [ref=e406]: 附件类型A
              - tab "附件类型B" [ref=e408]:
                - generic [ref=e409]: 附件类型B
            - tabpanel "附件类型A" [ref=e412]:
              - generic [ref=e413]:
                - alert [ref=e414]:
                  - generic [ref=e416]: 请上传奖状 PDF 文件
                - generic [ref=e419]:
                  - button "选择文件" [ref=e421] [cursor=pointer]:
                    - button "选择文件" [ref=e422]:
                      - generic [ref=e423]: 选择文件
                  - generic [ref=e424]: 请上传 大小不超过 50MB 格式为 pdf 的文件
      - contentinfo [ref=e425]:
        - generic [ref=e426]:
          - button "确定" [ref=e427] [cursor=pointer]:
            - generic [ref=e428]: 确定
          - button "取消" [ref=e429] [cursor=pointer]:
            - generic [ref=e430]: 取消
```

# Test source

```ts
  19  |         contentType: 'application/json',
  20  |         body: JSON.stringify({
  21  |           rows: [
  22  |             { id: 1, name: "全国电子设计竞赛" }
  23  |           ]
  24  |         })
  25  |       });
  26  |     });
  27  | 
  28  |     // 3. Mock list of competition applications
  29  |     await page.route('**/dev-api/competition-apply/competitionapply/list*', async (route) => {
  30  |       await route.fulfill({
  31  |         status: 200,
  32  |         contentType: 'application/json',
  33  |         body: JSON.stringify({
  34  |           total: 1,
  35  |           rows: [
  36  |             {
  37  |               id: 50,
  38  |               competitionId: 1,
  39  |               competitionName: "全国电子设计竞赛",
  40  |               teamName: "逐梦团队",
  41  |               studentName: "张三",
  42  |               studentNo: "S1001",
  43  |               status: "0", // 待审核
  44  |               createTime: "2026-05-23"
  45  |             }
  46  |           ],
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
> 119 |     await dialog.getByPlaceholder('请选择已有赛事或直接输入新的赛事名称').fill('全国电子设计竞赛');
      |                                                         ^ Error: locator.fill: Test timeout of 30000ms exceeded.
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
  147 |     await confirmBtn.click();
  148 | 
  149 |     expect(isReviewCalled).toBe(true);
  150 |   });
  151 | });
  152 | 
```