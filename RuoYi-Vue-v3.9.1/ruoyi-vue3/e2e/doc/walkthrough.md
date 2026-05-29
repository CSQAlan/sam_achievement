# Playwright E2E Setup Walkthrough

We have successfully built, refined, and verified a robust automated E2E testing framework for the RuoYi-Vue management system using **Playwright**.

## Covered Functions (已覆盖的测试功能)

The E2E test suite covers the following modules and specific functionalities:

### 1. Common Authentication & Mock Helpers ([`helpers.js`](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/helpers.js))
- **Verification Image Bypassing (验证码绕过):** Automatically intercepts the `/captchaImage` API requests and sets `captchaEnabled: false`, allowing headless testing without manual captcha solving.
- **Login Session Mocking:** Mocks `/login` (to return a fake JWT token), `/getInfo` (to mock user roles, permissions, and set `profileInitialized: 1` to prevent automatic redirection to the `/user/profile` setup page), and `/getRouters` (to bootstrap dynamic routes).
- **Global Dictionary Mocking:** Intercepts `**/dev-api/system/dict/data/type/*` to automatically supply mocked options for elements like categories, levels, statuses, tags, and deletion flags.

### 2. Login Module E2E Tests ([`login.spec.js`](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/login.spec.js))
- **Client-Side Form Validation:** Verifies Element-Plus triggers errors (like `"请输入您的账号"`) when fields are empty.
- **Successful Credentials Flow:** Tests standard credential entry and navigation redirection to the `/index` route.

### 3. Competition Management E2E Tests ([`competition.spec.js`](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/competition.spec.js))
- **Data Table Rendering:** Verifies that competition entries (e.g. `"第十届挑战杯大赛"`) and relation fields (e.g. `"计算机学院"`) populate correctly in the table view.
- **Scoped Dialog Actions (Add / Edit):** 
  - Scopes selectors inside `.el-dialog` modal components to avoid strict mode collisions with background query inputs.
  - Verifies filling in details, toggling category/level checkboxes, submitting, and triggering the backend post request.
- **Scoped Row Actions (Update / Delete):**
  - Scopes edit/delete button targets to `.el-table__body` to bypass disabled bulk buttons in the top toolbar.
  - Verifies opening edit modal, modifying information, and calling the update API.
  - Verifies clicking delete, clicking the browser pop-up box confirmation button, and calling the delete API.

---

## Remaining Modules To Be Tested (待测试的系统模块)

The following core modules of the management system do not have automated test scripts yet and should be added to the E2E/integration test suites in future phases:

| Module | Features to Test | Recommended Testing Approach |
| :--- | :--- | :--- |
| **比赛申请 (Competition Apply)** | Student/Teacher registrations, filling applicant details, submitting applications. | Playwright multi-role simulation (student applies, teacher reviews). |
| **成果管理 (Achievement)** | CRUD on research results/achievements, and "成果审核" (achievement review) state transitions. | Playwright E2E form filling, verifying state field updates. |
| **报销管理 (Reimbursement)** | Managing reimbursement projects, linking achievements, cost auditing. | E2E flow testing links between reimbursement forms and achievements. |
| **系统管理 (System)** | User profile edits, role assignments, department tree updates, menu permissions. | E2E permissions check: login as a standard user and assert that restricted menu items are hidden. |
| **系统工具 (Tools)** | Code generation config edits, form builder drag-and-drop. | Simple API/UI check for loading generator templates. |
| **监控与审计 (Monitor)** | Job scheduling, viewing log entries, checking "业务审计" (Biz Audit) logs. | Verify audit logs are populated after a mock CRUD action is completed. |

---

## Changes Made

1. **Shared Helpers Module:**
   - Created [helpers.js](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/helpers.js) for backend mocking and helper functions.
2. **Refactored Login Tests:**
   - Modified [login.spec.js](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/login.spec.js) to import and use the shared mock helpers.
3. **Competition Spec Created:**
   - Created [competition.spec.js](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/competition.spec.js) inside the `ruoyi-vue3/e2e` directory.
4. **Configuration File:**
   - Updated [playwright.config.js](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/playwright.config.js) to manage browser options and dev server execution.

## Test Validation Results

We executed the full test suite using `npm run test:e2e`. The tests successfully completed in **7.9 seconds** and all passed:

```bash
> ruoyi@3.9.1 test:e2e
> playwright test

Running 6 tests using 6 workers

[1/6] [chromium] › e2e\competition.spec.js:119:3 › Competition Management CRUD E2E Tests › should display the competition list table correctly
[2/6] [chromium] › e2e\login.spec.js:26:3 › Login E2E Tests (Mocked Backend) › should successfully log in with default credentials and redirect to dashboard
[3/6] [chromium] › e2e\competition.spec.js:132:3 › Competition Management CRUD E2E Tests › should support adding a new competition
[4/6] [chromium] › e2e\competition.spec.js:159:3 › Competition Management CRUD E2E Tests › should support updating an existing competition
[5/6] [chromium] › e2e\competition.spec.js:181:3 › Competition Management CRUD E2E Tests › should support deleting a competition
[6/6] [chromium] › e2e\login.spec.js:13:3 › Login E2E Tests (Mocked Backend) › should display validation error when submitting empty fields

  6 passed (7.9s)
```

## How to Run Tests in the Future

To run these tests on your machine:
1. Ensure you are in the `ruoyi-vue3` directory:
   ```bash
   cd ruoyi-vue3
   ```
2. **Command Line (Headless Mode):**
   ```bash
   npm run test:e2e
   ```
3. **Interactive UI Mode (Visual Debugging):**
   ```bash
   npm run test:e2e:ui
   ```
