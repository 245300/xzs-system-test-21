# 学之思系统测试（21软测小组）

软件测试技术课程 2026-2027-1-001 · 实验八 基础级综合实验（COMPREHENSIVE_WEB）

> 题目要求见仓库根目录 `readme.md`；本文件是小组协作与工程说明。

## 小组分工

| 成员 | 学号 | 分工 |
|---|---|---|
| 熊锦豪 | 5120245300 | 测试负责人：方案、进度、Web 自动化、报告汇总、评分提交 |
| 邓亮 | 5120244905 | 接口测试（Apifox）、性能测试（JMeter + ServerAgent） |
| 陶大财 | 5120245301 | 手工功能测试、界面与兼容性测试、Excel 记录、禅道录入 |

## 被测系统

- 学之思在线考试系统 3.9.0（官方 release，本地部署）
- 学生端：<http://127.0.0.1:8065/student/index.html>（student / 123456）
- 管理端：<http://localhost:8065/admin/index.html>（admin / 123456）
- 源码：<https://gitee.com/mindskip/xzs-mysql>
- 依赖：MySQL 8.0.28、Redis 5.0.14.1；启动脚本 `C:\xzs-local\start.bat`

## 目录结构

```text
.
├── pom.xml                 Maven 工程（Selenium 4.18.1 + JUnit 5.10.2 + JaCoCo 0.8.12）
├── src/main/java           page object、校验工具、Main 入口说明
├── src/test/java           Selenium 端到端 + 参数化/边界单元测试
├── src/test/ui             录制导出的原始脚本（过程证据）
├── api/                    Apifox CLI 集合（3 个场景）
├── jmeter/                 JMeter 脚本、CSV 数据、ServerAgent
├── doc/                    测试方案、手工记录、综合报告、截图
└── report/                 运行结果（不入库）
```

## 测试工具

| 类型 | 工具 |
|---|---|
| 手工/缺陷 | Excel、禅道（主记录）、GitHub Issue（镜像） |
| Web 自动化 | Selenium IDE 录制 + Selenium WebDriver 4.18.1 + JUnit 5 + Maven + JaCoCo |
| 接口 | Apifox 桌面版 + Apifox CLI 2.2.9 |
| 性能 | JMeter 5.6.3 + JMeter Plugins + ServerAgent-2.2.3 |
| 浏览器 | Chrome 157、Edge 157、Firefox 160、360 极速浏览器X 23.1 |

## 常用命令

```bash
# 自动化与单元测试
mvn test

# 接口场景（需先启动本地 8065）
apifox run "api/场景1-管理端登录-首页.json" -r cli
apifox run "api/场景2-管理端登录-留言列表.json" -r cli
apifox run "api/场景3-管理端登录-用户与卷题管理.json" -r cli

# 性能测试
jmeter -n -t jmeter/xzs_admin_perf.jmx -l report/jmeter/result.jtl -e -o report/jmeter/html
```

## 分支模型

- `main`：可交付版本，保护分支
- `feature/manual`：手工、界面、兼容
- `feature/ui-auto`：Selenium 录制与自动化
- `feature/api`：Apifox 场景与导出
- `feature/jmeter`：性能脚本与服务器监控
- `docs/report`：测试方案与综合报告

每个分支通过 Pull Request 合入，至少 1 人评审；提交信息带用例/缺陷编号，例如
`feat(ui): AT-01 登录进首页`。

## 说明

- `.qa/`、`report/`、`target/`、`chromedriver.exe` 为评分元数据或构建产物，不入库。
- 测试数据统一使用 `性能测试_1113_` 等专用前缀，避免与演示数据混淆。
