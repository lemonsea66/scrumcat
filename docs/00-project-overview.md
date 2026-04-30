# 00-project-overview.md

# ScrumCat：轻量级 Scrum 过程管理平台项目总览

## 1. 项目基本信息

| 项目项 | 内容 |
|---|---|
| 项目名称 | ScrumCat：轻量级 Scrum 过程管理平台 |
| 项目类型 | Web 版 Scrum 过程管理平台 |
| 开发方式 | 个人独立开发 |
| 开发模式 | Builder 模式思路下的 AI 辅助开发 |
| 前端技术栈 | Vue 3 + Vite + Ant Design Vue + Vue Router + Pinia + Axios + ECharts + VueDraggableNext |
| 后端技术栈 | Spring Boot + MyBatis-Plus + MySQL + JWT |
| 架构模式 | 前后端分离架构 |
| 接口风格 | RESTful API |
| 版本管理 | Git，采用 main + dev 分支 |
| 远程仓库 | Gitee 主仓库 + GitHub 镜像；如果只能二选一，优先 GitHub |
| UI 风格 | 温柔治愈、简洁现代、轻量猫猫主题 |
| 任务估算方式 | 整数小时，如 2h、4h、8h |

---

## 2. 项目定位

ScrumCat 是一个面向课程实验与个人敏捷实践场景的轻量级 Scrum 过程管理平台。系统围绕 Scrum 开发过程中的用户故事、产品待办列表、迭代计划、迭代开发、任务拆解、燃尽图、迭代回顾与问题追踪等核心环节展开，帮助使用者以较低成本完成从需求管理到迭代复盘的完整流程。

本项目采用前后端分离架构，前端负责页面展示、交互逻辑、拖拽操作和图表展示，后端负责业务接口、数据持久化、身份认证和核心业务规则处理。系统在功能设计上按照 Scrum 业务流程拆分模块，但在工程实现上保持前端项目与后端项目独立开发、独立运行、通过接口通信。

在视觉设计上，ScrumCat 引入原创温柔治愈风格的猫猫元素，将猫猫作为系统的轻量化视觉辅助符号，用于登录页、Dashboard 状态提示、空状态提示、故事点展示等位置，在不影响系统专业性的前提下提升界面亲和力与项目辨识度。

---

## 3. 与任务书的关系说明

本项目以课程任务书为重要参考，核心功能覆盖 Scrum 过程管理平台的主要要求，包括用户故事管理、产品待办列表、迭代计划、看板管理、燃尽图、迭代回顾、任务拆解和问题追踪等内容。

若后续实际开发需求与任务书描述存在差异，将优先提醒差异点，并按照本项目最终确定的需求方案执行。当前项目已明确采用个人独立开发方式，并在功能范围、技术栈、版本管理和视觉风格上结合个人实际情况进行调整。

---

## 4. 项目目标

本项目的目标不是单纯完成一个静态管理页面，而是实现一个具备完整 Scrum 流程闭环的可运行系统。具体目标如下：

1. 实现用户注册、登录和基于 JWT 的访问认证。
2. 实现用户故事的新增、编辑、删除、查询和状态维护。
3. 实现产品待办列表的优先级排序、筛选和拖拽调整。
4. 实现 Sprint 迭代计划创建，并支持从产品待办列表拖拽用户故事加入迭代。
5. 实现故事级三列看板，支持用户故事在“未开始、进行中、已完成”之间拖拽流转。
6. 实现任务拆解功能，为用户故事拆分技术任务并设置整数工时估算。
7. 实现增强版任务看板，支持按用户故事分组管理技术任务。
8. 实现燃尽图，用计划线和实际线展示 Sprint 进度趋势。
9. 实现迭代回顾管理，记录做得好的内容和待改进内容。
10. 实现问题追踪管理，支持问题记录、状态流转和筛选查看。
11. 形成完整的 Git 版本提交记录，体现项目从初始化到最终交付的迭代过程。
12. 保留 AI 辅助开发过程记录，包括需求分析、代码生成、问题排查和人工修正案例。

---

## 5. 总体架构

### 5.1 架构选择

本项目采用前后端分离架构。

前端项目和后端项目分别独立存在：

```text
scrumcat/
├── frontend/        # Vue 3 前端项目
├── backend/         # Spring Boot 后端项目
├── database/        # 数据库脚本
├── docs/            # 项目文档
├── api-tests/       # 接口测试文件
└── README.md
```

前端通过 Axios 调用后端 RESTful API，后端通过 MyBatis-Plus 操作 MySQL 数据库。用户登录成功后，后端生成 JWT token，前端保存 token，并在后续请求中携带 token 访问受保护接口。

### 5.2 前端职责

前端主要负责：

- 登录页、注册页和系统主布局；
- 左侧导航栏和顶部用户信息区域；
- Dashboard 首页数据展示；
- 表格、表单、弹窗、筛选和搜索；
- 产品待办列表拖拽排序；
- 用户故事拖拽加入 Sprint；
- 故事看板和任务看板拖拽交互；
- 燃尽图可视化；
- 温柔治愈风格的猫猫主题展示；
- 前端路由守卫和 token 管理。

### 5.3 后端职责

后端主要负责：

- 用户注册、登录和 JWT 认证；
- 用户故事数据管理；
- 产品待办列表优先级更新；
- Sprint 迭代计划管理；
- 迭代与用户故事关联管理；
- 故事状态流转和状态变更记录；
- 技术任务管理；
- 问题追踪管理；
- 迭代回顾管理；
- 燃尽图数据计算；
- 接口参数校验和异常处理。

### 5.4 数据库职责

数据库主要负责持久化存储：

- 用户信息；
- 用户故事；
- Sprint 迭代信息；
- Sprint 与用户故事关联关系；
- 技术任务；
- 故事状态变更日志；
- 迭代回顾；
- 问题追踪记录。

---

## 6. 功能模块总览

虽然工程采用前后端分离架构，但业务功能按照模块拆分。每个模块通常同时包含前端页面、后端接口、数据库表和测试用例。

| 模块编号 | 模块名称 | 主要内容 |
|---|---|---|
| M01 | 登录注册模块 | 用户注册、登录、退出、JWT 认证、路由拦截 |
| M02 | Dashboard 首页 | 当前 Sprint、完成率、故事统计、问题统计、状态猫提示 |
| M03 | 用户故事管理 | 用户故事增删改查、故事点、优先级、状态管理 |
| M04 | 产品待办列表 | 优先级排序、筛选、拖拽调整优先级 |
| M05 | 迭代计划管理 | 创建 Sprint、设置周期、拖拽故事加入 Sprint |
| M06 | 故事看板 | 未开始、进行中、已完成三列拖拽看板 |
| M07 | 任务拆解 | 为用户故事拆分技术任务，设置整数工时 |
| M08 | 增强版任务看板 | 按用户故事分组，任务在所属故事行内拖拽流转 |
| M09 | 燃尽图 | 计划线、实际线、剩余故事点趋势 |
| M10 | 迭代回顾 | 做得好的三件事、待改进的三件事、历史回顾 |
| M11 | 问题追踪 | 问题记录、状态流转、来源分类、筛选查看 |

---

## 7. 核心业务流程

### 7.1 Scrum 主流程

系统核心流程如下：

```text
用户注册 / 登录
    ↓
创建用户故事
    ↓
进入产品待办列表
    ↓
拖拽调整故事优先级
    ↓
创建 Sprint 迭代
    ↓
拖拽用户故事加入 Sprint
    ↓
在故事看板中推进状态
    ↓
为故事拆解技术任务
    ↓
在任务看板中推进任务状态
    ↓
查看燃尽图
    ↓
记录问题追踪
    ↓
填写迭代回顾
```

### 7.2 用户故事状态流转

用户故事状态包括：

```text
未开始 TODO
进行中 IN_PROGRESS
已完成 DONE
```

状态流转方式：

```text
未开始 → 进行中 → 已完成
```

系统允许通过拖拽方式更新状态，状态变化后需要持久化保存到数据库，并记录状态变更日志，以便后续燃尽图计算和过程追溯。

### 7.3 技术任务状态流转

技术任务状态包括：

```text
未开始 TODO
进行中 IN_PROGRESS
已完成 DONE
```

技术任务必须隶属于某个用户故事。增强版任务看板采用“纵向按用户故事分组、横向按任务状态分列”的形式，任务只能在所属用户故事行内拖拽移动。

---

## 8. 页面规划

### 8.1 页面结构

系统前端采用左侧导航栏 + 顶部信息栏 + 主内容区的布局。

推荐页面如下：

```text
/login                  登录页
/register               注册页
/dashboard              首页 Dashboard
/stories                用户故事管理
/backlog                产品待办列表
/sprints                迭代计划管理
/sprint-board           故事看板
/task-board             增强版任务看板
/burndown               燃尽图
/retrospectives         迭代回顾
/issues                 问题追踪
```

### 8.2 Dashboard 首页设计

Dashboard 是系统的第一入口，重点展示当前 Sprint 的整体状态。

建议包含：

- 当前活跃 Sprint；
- Sprint 完成率；
- 总故事数；
- 已完成故事数；
- 剩余故事点；
- 待处理问题数；
- 任务状态概览；
- 燃尽图简要趋势；
- 状态猫提示卡片；
- 快捷入口按钮。

### 8.3 参考图借鉴方式

参考图中的左侧导航、卡片式信息布局、工作小结区域和右侧信息区可以作为视觉参考。本项目不会直接照搬其页面，而是在此基础上强化 Scrum 过程管理特征。

ScrumCat 相比参考图的增强点包括：

1. 更明确的 Scrum 流程闭环。
2. 增加用户故事、Backlog、Sprint、看板、燃尽图、回顾、问题追踪等过程模块。
3. 增加任务拆解和增强版任务看板。
4. 增加登录注册和 JWT 认证。
5. 增加温柔治愈风格的猫猫视觉系统。
6. 更适合形成实验报告、接口测试和录屏演示材料。

---

## 9. 猫猫主题设计

### 9.1 设计原则

猫猫元素只作为轻量视觉辅助，不改变系统本身的专业属性。设计应遵循：

- 温柔治愈；
- 原创风格；
- 低饱和色彩；
- 简洁线条；
- 不使用现成动漫 IP；
- 不影响页面可读性；
- 不干扰核心业务操作。

### 9.2 视觉落点

猫猫元素主要出现在以下位置：

1. 登录页欢迎区域。
2. Dashboard 状态猫卡片。
3. 空状态提示。
4. 用户故事卡片或任务卡片的小图标。
5. 故事点的轻量小鱼干视觉表达。
6. 页面局部装饰，如猫爪、圆角卡片、柔和背景。

### 9.3 状态猫文案示例

根据当前 Sprint 完成率显示不同状态：

| 完成率 | 状态 | 文案示例 |
|---|---|---|
| 0% - 40% | 需要关注 | 猫猫轻轻拍了拍看板，提醒你关注当前迭代进度。 |
| 40% - 80% | 稳步推进 | 猫猫正在陪你一起推进 Sprint，当前节奏比较稳定。 |
| 80% - 99% | 接近完成 | 猫猫已经看到小鱼干了，最后几个任务继续收尾。 |
| 100% | 已完成 | 本轮 Sprint 已完成，猫猫获得了一份小鱼干奖励。 |

---

## 10. 数据表初步规划

初步规划以下核心数据表：

| 表名 | 说明 |
|---|---|
| sys_user | 用户表 |
| user_story | 用户故事表 |
| sprint | 迭代表 |
| sprint_story | 迭代与用户故事关联表 |
| story_status_log | 用户故事状态变更日志表 |
| task | 技术任务表 |
| issue | 问题追踪表 |
| retrospective | 迭代回顾表 |

详细字段设计将在 `04-database-design.md` 中展开。

---

## 11. 接口设计初步规划

后端接口采用 RESTful 风格，统一以 `/api` 开头。

初步接口分组如下：

```text
/api/auth           登录注册相关接口
/api/dashboard      首页统计接口
/api/stories        用户故事接口
/api/backlog        产品待办列表接口
/api/sprints        迭代计划接口
/api/sprint-board   故事看板接口
/api/tasks          技术任务接口
/api/task-board     任务看板接口
/api/burndown       燃尽图接口
/api/retrospectives 迭代回顾接口
/api/issues         问题追踪接口
```

详细接口路径、请求参数和响应结构将在 `05-api-design.md` 中展开。

---

## 12. Git 版本管理策略

本项目采用 Git 进行版本管理，使用 `main + dev` 分支策略。

### 12.1 分支说明

| 分支 | 说明 |
|---|---|
| main | 稳定分支，用于保存阶段性可运行版本和最终交付版本 |
| dev | 开发分支，用于日常功能开发和调试 |

### 12.2 基本开发流程

```text
从 main 创建 dev
    ↓
在 dev 上开发功能
    ↓
完成一个模块后提交 commit
    ↓
阶段功能稳定后合并到 main
    ↓
推送到远程仓库
```

### 12.3 推荐提交记录

建议按照模块形成清晰的提交记录：

```text
init: initialize ScrumCat project structure
feat: add login and register module
feat: add dashboard layout
feat: implement user story management
feat: implement product backlog sorting
feat: implement sprint planning
feat: implement story drag into sprint
feat: implement sprint story board
feat: implement task breakdown
feat: implement grouped task board
feat: add burndown chart
feat: add retrospective management
feat: add issue tracking
test: add api test cases
fix: fix jwt token persistence issue
fix: fix drag update persistence issue
docs: add project documentation
```

---

## 13. AI 辅助开发策略

本项目采用 AI 辅助开发，但不直接依赖 AI 一次性生成完整项目。AI 的角色被拆分为规划型辅助和实现型辅助。

### 13.1 GPT 的主要作用

GPT 主要用于：

- 需求分析；
- 项目规划；
- 文档生成；
- 数据库设计；
- 接口设计；
- 页面结构设计；
- Codex 提示词设计；
- 错误排查思路分析；
- 报告素材整理。

### 13.2 Codex / Trae Builder 的主要作用

Codex / Trae Builder 主要用于：

- 根据模块文档生成代码；
- 补全 Spring Boot Controller、Service、Mapper；
- 生成 Vue 页面组件；
- 修复局部报错；
- 生成接口测试样例；
- 解释已有代码；
- 根据错误日志定位 bug。

### 13.3 使用原则

AI 辅助开发遵循以下原则：

1. 文档先行，代码随后。
2. 每次只让 AI 实现一个小模块。
3. 不让 AI 一次性生成完整系统。
4. 每次 AI 修改后人工运行和检查。
5. 每完成一个模块及时 Git 提交。
6. 保留 AI 指令、AI 输出、人工调整和修正原因。
7. 主动沉淀 AI 生成错误案例，用于实验报告反思。

---

## 14. 接口测试计划概览

接口测试采用 Postman 或 Apifox 进行。

测试范围包括：

- 登录注册接口；
- 用户故事管理接口；
- 产品待办列表排序接口；
- Sprint 创建与故事加入接口；
- 看板状态更新接口；
- 技术任务接口；
- 燃尽图接口；
- 迭代回顾接口；
- 问题追踪接口。

详细测试用例将在 `09-test-plan.md` 中展开。

---

## 15. 录屏演示流程概览

最终录屏建议按照 Scrum 主流程展示：

1. 登录系统。
2. 展示 Dashboard 首页。
3. 创建用户故事。
4. 在产品待办列表中拖拽调整优先级。
5. 创建 Sprint。
6. 拖拽用户故事加入 Sprint。
7. 在故事看板中拖拽推进状态。
8. 为用户故事拆解技术任务。
9. 在增强版任务看板中拖拽任务状态。
10. 查看燃尽图变化。
11. 新增问题追踪记录。
12. 填写迭代回顾。
13. 简要展示 Git 提交记录和接口测试结果。

---

## 16. 后续文档规划

后续将继续生成以下文档：

```text
01-requirement-analysis.md
02-technology-selection.md
03-system-architecture.md
04-database-design.md
05-api-design.md
06-frontend-page-design.md
07-git-workflow.md
08-ai-collaboration-plan.md
09-test-plan.md
10-demo-script.md
11-module-login.md
12-module-user-story.md
13-module-product-backlog.md
14-module-sprint-planning.md
15-module-sprint-board.md
16-module-task-board.md
17-module-burndown-chart.md
18-module-retrospective.md
19-module-issue-tracking.md
```

---

## 17. 当前确认结论

当前项目总方案确认如下：

```text
项目名称：ScrumCat：轻量级 Scrum 过程管理平台
开发方式：个人独立开发
架构模式：前后端分离
前端：Vue 3 + Vite + Ant Design Vue
后端：Spring Boot + MyBatis-Plus + MySQL
认证：简单 JWT
版本管理：Git，main + dev 分支
远程仓库：Gitee 主仓库 + GitHub 镜像；如只能二选一则优先 GitHub
功能范围：基本版 + 增强版 1
任务工时：整数小时
视觉风格：温柔治愈，原创猫猫主题
```
