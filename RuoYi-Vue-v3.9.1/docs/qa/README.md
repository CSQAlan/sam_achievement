# QA 测试与性能分析交付物

本文档组对应 `1.md` 中角色 B 的任务。当前项目尚未进入部署阶段，因此已先完成可前置准备和可本地执行的内容：测试用例设计、JMeter 压测计划、Playwright mock E2E 验收覆盖、执行记录模板和截图留档规范。

## 当前完成项

| 交付物 | 路径 | 状态 |
| :-- | :-- | :-- |
| 功能测试用例表 | `docs/qa/functional-test-cases.md` | 已编写，已标注当前 E2E 覆盖状态 |
| QA 验收自动化用例 | `ruoyi-vue3/e2e/qa-acceptance.spec.js` | 已实现，14/14 通过 |
| 压测执行方案 | `docs/qa/performance-test-plan.md` | 已编写，待部署后执行 |
| JMeter 测试计划 | `docs/qa/jmeter/achievement-system-load-test.jmx` | 已编写，需按部署地址配置 |
| 测试执行报告模板 | `docs/qa/templates/test-execution-report.md` | 已编写，待执行后填充 |
| 截图留档清单 | `docs/qa/templates/screenshot-checklist.md` | 已编写，待执行后勾选 |
| 当前阶段验证记录 | `docs/qa/current-stage-verification.md` | 已完成 |

## 部署前不可完成项

- 无可访问的生产/验收环境时，无法实际执行 JMeter 压测。
- 无真实环境、账号、数据量和权限矩阵时，无法采集吞吐量、平均响应时间、P95/P99、错误率等最终指标。
- 无真实部署 UI 运行环境时，无法完成最终截图留档；当前阶段的 UI 行为已通过本地 mock E2E 先行验证。

## 待 A 部署完成后执行

1. 确认访问入口、后端接口前缀、测试账号、验证码策略和数据库测试数据。
2. 使用 JMeter 打开或命令行运行 `docs/qa/jmeter/achievement-system-load-test.jmx`。
3. 将结果写入 `docs/qa/templates/test-execution-report.md` 的副本。
4. 将聚合报告、吞吐量曲线、错误响应、核心 UI 正常页和越权拦截弹窗截图保存到 `docs/qa/screenshots/`。

## 当前阶段自动化验证

- `achievement-system-load-test.jmx` 已通过 XML 解析校验。
- `ruoyi-vue3/e2e/qa-acceptance.spec.js` 已覆盖登录失败、禁用账号、未登录访问、越权访问、成果筛选、必填校验、证书编号重复、年度统计、附件导出、证书编号超长、Excel 导出、赛事必填、分页边界、空导出和特殊字符查询。
- `npm.cmd run test:e2e -- e2e/qa-acceptance.spec.js --reporter=list --output=C:\tmp\ruoyi-vue3-qa-acceptance-results` 已通过 14/14 条 QA 验收用例。
- `npm.cmd run test:e2e -- --reporter=list --output=C:\tmp\ruoyi-vue3-full-e2e-results` 已通过 29/29 条 Playwright mock E2E 用例。
