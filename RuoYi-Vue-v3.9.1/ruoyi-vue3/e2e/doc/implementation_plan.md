# Implementation Plan - Expanding E2E Test Coverage

Expand Playwright E2E test coverage to cover the remaining core business processes of the management system: **Achievement Management**, **Competition Apply & Review**, and **Reimbursement Management**. All tests will use mocked endpoints inside [`helpers.js`](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/helpers.js) to run independently without needing a running backend.

## User Review Required

> [!IMPORTANT]
> - This plan adds three new test spec files under `ruoyi-vue3/e2e/` and updates [`helpers.js`](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/helpers.js) to include additional mocked routes.
> - No existing application code or backend code will be modified.

## Proposed Changes

### E2E Test Expansion

---

#### [MODIFY] [helpers.js](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/helpers.js)
- Extend mock routers (`**/getRouters`) to include dynamic navigation menus for:
  - **成果管理 (Achievement):** `成果管理` under `/achievement/manage`
  - **比赛申请 (Apply):** `比赛申请` under `/competition-apply/competitionapply` and `申请审核` under `/competitionapply-review`
  - **报销管理 (Reimbursement):** `报销项目详情` under `/reimbursement/Reimbursement`
- Add mock dictionary endpoints for achievement types, application statuses, and reimbursement coefficients.

#### [NEW] [achievement.spec.js](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/achievement.spec.js)
- Test suite for Achievement Database Administration.
- Mock endpoints:
  - `GET **/dev-api/achievement/manage/list`
  - `GET **/dev-api/achievement/manage/100`
  - `POST **/dev-api/achievement/manage` (add)
  - `PUT **/dev-api/achievement/manage` (update)
  - `DELETE **/dev-api/achievement/manage/100` (delete)
- Test scenarios:
  1. Displaying the list table of achievements correctly.
  2. Adding a new achievement entry (filling out modal forms and submitting).
  3. Modifying an existing achievement name and saving changes.
  4. Deleting an achievement from the list.

#### [NEW] [apply.spec.js](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/apply.spec.js)
- Test suite for student application submission and administrative reviews.
- Mock endpoints:
  - `GET **/dev-api/competition/competitionapply/list`
  - `POST **/dev-api/competition/competitionapply`
  - `PUT **/dev-api/competition/competitionapply/review`
- Test scenarios:
  1. Navigating to "比赛申请", filling out registration fields (member lists, attachments), and submitting.
  2. Navigating to "申请审核", reviewing a pending application, and clicking "同意" (Approve) or "拒绝" (Reject).

#### [NEW] [reimbursement.spec.js](file:///f:/AMS/RuoYi-Vue-v3.9.1/ruoyi-vue3/e2e/reimbursement.spec.js)
- Test suite for Reimbursement Projects.
- Mock endpoints:
  - `GET **/dev-api/reimbursement/Reimbursement/list`
  - `POST **/dev-api/reimbursement/Reimbursement`
- Test scenarios:
  1. Displaying reimbursement projects.
  2. Adding a reimbursement profile and saving.

## Verification Plan

### Automated Tests
Run the entire E2E suite to verify that all 14 tests (including login, competition, achievement, application, and reimbursement tests) run successfully:
```bash
cd ruoyi-vue3
npm run test:e2e
```

### Manual Verification
- You can watch the execution flow step-by-step in the Playwright interactive runner:
  ```bash
  npm run test:e2e:ui
  ```
