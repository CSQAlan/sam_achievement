# 成果管理系统 JMeter 压测方案

## 当前阶段说明

当前尚未进入部署阶段，本方案只完成压测设计和 JMeter 测试计划准备，不填充真实性能结论。实际吞吐量、响应时间和错误率需要等待 A 完成环境部署后执行采集。

## 压测目标

- 验证登录、成果查询、赛事查询、成果导出等高频接口在并发访问下的稳定性。
- 采集吞吐量、平均响应时间、P90/P95/P99、错误率、最大响应时间等关键指标。
- 产出聚合报告截图、HTML 报告和 JTL 原始结果，支撑最终答辩展示。

## 接口范围

| 场景 | 方法 | 接口 | 说明 | 主要断言 |
| :-- | :-- | :-- | :-- | :-- |
| 登录 | POST | `/login` | 获取 token，供后续接口使用 | HTTP 200，响应包含 `token` |
| 成果查询 | GET | `/achievement/manage/list-responsible` | 高频成果列表查询 | HTTP 200，响应包含 `rows` |
| 赛事查询 | GET | `/competition/competition/list` | 查询赛事列表 | HTTP 200，响应包含 `rows` |
| 成果导出 | POST | `/achievement/manage/export` | Excel 导出，文件型响应 | HTTP 200，响应为 blob/Excel |
| 首页统计 | GET | `/achievement/manage/stats/dashboard` | 首页统计面板 | HTTP 200 |

## 环境参数

JMeter 计划使用属性参数，可在 GUI 中编辑，也可通过命令行覆盖。

| 参数 | 默认值 | 说明 |
| :-- | :-- | :-- |
| `protocol` | `http` | 协议 |
| `host` | `localhost` | 部署域名或 IP |
| `port` | `80` | Nginx/后端端口 |
| `base_path` | `/dev-api` | 前端代理路径；直连后端时可置空 |
| `username` | `admin` | 压测账号 |
| `password` | `admin123` | 压测密码 |
| `captcha_code` | 空 | 若验证码未关闭，需要填真实值或走预置验证码策略 |
| `captcha_uuid` | 空 | 与验证码配套的 uuid |
| `threads` | `50` | 并发用户数 |
| `ramp_up` | `60` | 启动爬坡秒数 |
| `loops` | `10` | 每线程循环次数 |

## 建议压测梯度

| 轮次 | 并发数 | Ramp-up | 循环次数 | 目标 |
| :-- | --: | --: | --: | :-- |
| Smoke | 5 | 10s | 2 | 验证脚本、账号、token 和接口路径 |
| Baseline | 20 | 30s | 5 | 建立基础性能基线 |
| Load | 50 | 60s | 10 | 常规并发压力 |
| Stress | 100 | 120s | 10 | 探测系统瓶颈 |
| Soak | 30 | 60s | 60 | 观察长时间稳定性和内存/连接泄漏 |

## 命令行执行示例

```powershell
jmeter -n -t docs/qa/jmeter/achievement-system-load-test.jmx `
  -l docs/qa/results/load-test.jtl `
  -e -o docs/qa/results/html-report `
  -Jprotocol=http `
  -Jhost=部署地址 `
  -Jport=80 `
  -Jbase_path=/dev-api `
  -Jusername=压测账号 `
  -Jpassword=压测密码 `
  -Jthreads=50 `
  -Jramp_up=60 `
  -Jloops=10
```

## 通过标准

以下阈值是验收建议值，最终阈值可按实际服务器配置调整。

| 指标 | 建议阈值 |
| :-- | :-- |
| 错误率 | `< 1%` |
| 登录 P95 | `< 1500 ms` |
| 成果查询 P95 | `< 1000 ms` |
| 赛事查询 P95 | `< 1000 ms` |
| 成果导出 P95 | `< 5000 ms`，大数据量可单独说明 |
| 服务端稳定性 | 无持续 5xx、无数据库连接耗尽、无 OOM |

## 截图留档要求

每轮压测至少保存以下截图：

- JMeter Aggregate Report，文件名建议 `PERF-轮次-aggregate.png`。
- JMeter Summary Report 或 HTML Dashboard 首页，文件名建议 `PERF-轮次-summary.png`。
- Response Time Percentiles 或响应时间曲线，文件名建议 `PERF-轮次-response-time.png`。
- Errors 面板或失败采样详情，若无错误也截图证明错误率为 0。
- 服务器 CPU、内存、磁盘、网络或容器监控截图，若由 A 提供，引用到测试报告。

## 执行风险与注意事项

- 若验证码在验收环境开启，登录压测需提前配置固定验证码、关闭验证码或改用预置 token。
- 导出接口会产生文件 IO 和数据库查询压力，不应在生产真实数据高峰期做 Stress 轮次。
- 压测账号权限必须覆盖成果查询、赛事查询和导出，否则结果会变成权限拦截性能而非业务接口性能。
- 执行前清理或单独标识压测产生的业务数据，避免污染验收演示数据。

