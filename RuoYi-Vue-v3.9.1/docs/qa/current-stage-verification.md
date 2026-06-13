# 当前阶段 QA 验证记录

记录时间：2026-06-09

## 已完成验证

| 项目 | 命令/动作 | 结果 | 说明 |
| :-- | :-- | :-- | :-- |
| JMeter 文件格式 | `[xml](Get-Content -Path docs\qa\jmeter\achievement-system-load-test.jmx -Raw -Encoding UTF8)` | 通过 | `.jmx` 至少为合法 XML，可交给 JMeter 进一步打开执行 |
| QA 验收自动化 E2E | `npm.cmd run test:e2e -- e2e/qa-acceptance.spec.js --reporter=list --output=C:\tmp\ruoyi-vue3-qa-acceptance-results` | 14/14 通过 | 覆盖登录失败、禁用账号、未登录访问、越权、成果筛选、必填、重复证书编号、统计、附件 ZIP、Excel 导出、分页、空导出、特殊字符和超长输入 |
| 全量 mock E2E | `npm.cmd run test:e2e -- --reporter=list --output=C:\tmp\ruoyi-vue3-full-e2e-results` | 29/29 通过 | 覆盖既有业务 E2E 与本次新增 QA 验收 E2E，用时约 1.4m |
| 前端边界校验 | `ruoyi-vue3/src/views/achievement/component/AchievementForm.vue` | 已实现 | 证书编号新增 100 字符上限，超长时提示 `证书编号不能超过100个字符` |

## 注意事项

- 默认 Playwright 配置已固定为单 worker，避免 mock 路由和页面加载在并发执行下产生不稳定超时。
- 本次 E2E 使用前端 mock 后端，不代表真实后端和数据库已通过验收。
- JMeter 压测尚未执行，因为当前还没有 A 部署完成后的可测环境、稳定账号、验证码策略和压测数据。

## 后续部署后动作

1. 按 `docs/qa/performance-test-plan.md` 配置真实环境地址和账号，执行 JMeter Smoke/Baseline/Load/Stress。
2. 将 JMeter 聚合报告和 HTML Dashboard 截图放入 `docs/qa/screenshots/performance/`。
3. 按 `docs/qa/functional-test-cases.md` 执行功能、边界和越权用例，并把截图放入 `docs/qa/screenshots/functional/`。
4. 复制 `docs/qa/templates/test-execution-report.md` 作为正式报告，填入实际结果。
