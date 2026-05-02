# ScrumCat Milestone 03 总结报告

## 1. 当前已完成的功能

ScrumCat 当前已经完成从基础管理到 Sprint 执行的核心流程：

- 登录注册、JWT 登录态保持、路由守卫、退出登录。
- 项目空间管理：创建、编辑、删除无关联数据的项目空间，选择当前项目。
- 用户故事管理：当前项目下用户故事新增、查询、编辑、删除、状态更新。
- 产品待办：当前项目下故事按优先级排序，支持全部 / 待开发筛选和拖拽排序保存。
- 迭代计划：当前项目下 Sprint 创建、编辑、删除、选择，并支持把当前项目故事加入 Sprint。
- 故事看板：按当前 Sprint 将故事分为未开始、进行中、已完成三列，支持拖拽更新故事状态。
- 任务拆解：在故事详情抽屉中为故事新增、编辑、删除任务，并更新任务状态。
- 任务看板：按用户故事分组展示任务，横向分为未开始任务、进行中任务、已完成任务，支持拖拽和按钮更新任务状态。
- 猫猫提示与插图占位：预留 `frontend/src/assets/imgs/`，支持后续替换猫猫插图。

## 2. 当前技术栈

- 前端：Vue 3、Vite、Ant Design Vue、Vue Router、Pinia、Axios、ECharts、VueDraggableNext。
- 后端：Spring Boot 3.x、Java 21、Maven、MyBatis-Plus、MySQL、JWT。
- 数据库：MySQL 8.x，字符集 `utf8mb4`。
- 版本管理：Git，当前开发分支为 `dev`，远端包含 GitHub `origin` 和 Gitee `gitee`。

## 3. 当前前后端启动方式

后端启动：

```powershell
cd E:\STAR\vibecoding\ScrumCat\backend
$env:SPRING_PROFILES_ACTIVE="local"
mvn spring-boot:run
```

前端启动：

```powershell
cd E:\STAR\vibecoding\ScrumCat\frontend
npm run dev -- --host 127.0.0.1 --port 5174
```

访问地址：

```text
http://127.0.0.1:5174
```

构建验证：

```powershell
cd E:\STAR\vibecoding\ScrumCat\backend
mvn -q -DskipTests package

cd E:\STAR\vibecoding\ScrumCat\frontend
npm run build
```

## 4. 当前数据库表和核心关系

当前核心表：

- `sys_user`：系统用户。
- `project`：项目空间。
- `collaboration_member`：项目、故事、Sprint 的昵称成员。
- `user_story`：用户故事，绑定 `project_id` 和 `creator_id`。
- `sprint`：Sprint，绑定 `project_id` 和 `creator_id`。
- `sprint_story`：Sprint 与用户故事的多对多关联。
- `story_status_log`：故事状态变更日志。
- `task`：技术任务，绑定 `story_id`。
- `issue`：问题追踪预留表。
- `retrospective`：迭代回顾预留表。

核心关系：

```text
sys_user 1 - N project
project 1 - N user_story
project 1 - N sprint
sprint N - N user_story，通过 sprint_story 关联
user_story 1 - N task
sprint + user_story 状态变化记录到 story_status_log
```

## 5. 各模块实现情况

项目空间：

- 已实现 `/projects` 页面和项目 CRUD。
- 支持当前项目选择，前端通过 Pinia 保存当前项目。

用户故事：

- 已实现 `/stories` 页面和故事 CRUD。
- 故事绑定当前项目，支持故事点、优先级、负责人昵称、协作成员、状态。

产品待办：

- 已实现 `/backlog` 页面。
- 支持按优先级展示、筛选待开发故事、拖拽调整优先级并保存。

Sprint：

- 已实现 `/sprints` 页面。
- 支持 Sprint 创建、编辑、删除、选择。
- 支持从当前项目待办中加入故事，并防止重复加入和跨项目加入。

故事看板：

- 已实现 `/sprint-board` 页面。
- 按当前 Sprint 将故事分为 `TODO / IN_PROGRESS / DONE`。
- 支持故事卡片拖拽流转，调用后端接口持久化。
- 故事状态变化会写入 `story_status_log`。
- Sprint 摘要卡右侧提供猫猫提示牌，后续可替换插图资源。

任务拆解：

- 已实现故事详情抽屉。
- 点击故事卡片后可查看故事信息和任务列表。
- 支持任务新增、编辑、删除、状态更新。

任务看板：

- 已实现 `/task-board` 页面。
- 每一行对应一个 Sprint 用户故事，横向展示未开始任务、进行中任务、已完成任务。
- 支持任务在所属故事行内拖拽流转，不允许跨故事行移动。
- 任务卡片提供状态按钮：开始、完成、退回、重新进行。

## 6. `story_status_log` 的实现情况

`story_status_log` 用于记录 Sprint 中故事状态变化。

当前写入字段：

- `sprint_id`
- `story_id`
- `old_status`
- `new_status`
- `changed_by`
- `changed_at`

写入时机：

- 在故事看板中拖拽故事卡片，调用 `PUT /api/sprint-board/{sprintId}/stories/{storyId}/status`。
- 后端校验 Sprint、Story 和 Project 归属后更新 `user_story.status`。
- 状态发生真实变化时写入 `story_status_log`。

该表后续可用于 Milestone 04 的燃尽图和统计分析。

## 7. `task` 表和任务状态流转的实现情况

`task` 表当前字段包括：

- `id`
- `story_id`
- `title`
- `description`
- `estimated_hours`
- `status`
- `creator_id`
- `created_at`
- `updated_at`

任务状态：

```text
TODO
IN_PROGRESS
DONE
```

任务流转入口：

- 故事详情抽屉中的任务状态下拉框。
- 任务看板中的拖拽流转。
- 任务看板任务卡片上的状态按钮。

任务看板按钮规则：

- `TODO`：显示“开始”“完成”。
- `IN_PROGRESS`：显示“退回”“完成”。
- `DONE`：显示“重新进行”。

所有任务状态变化均调用：

```text
PUT /api/tasks/{id}/status
```

## 8. 当前重要文件路径

后端核心文件：

- `backend/src/main/java/com/scrumcat/controller/SprintBoardController.java`
- `backend/src/main/java/com/scrumcat/controller/TaskController.java`
- `backend/src/main/java/com/scrumcat/controller/TaskBoardController.java`
- `backend/src/main/java/com/scrumcat/service/impl/SprintBoardServiceImpl.java`
- `backend/src/main/java/com/scrumcat/service/impl/TaskServiceImpl.java`
- `backend/src/main/java/com/scrumcat/service/impl/TaskBoardServiceImpl.java`
- `backend/src/main/java/com/scrumcat/entity/Task.java`
- `backend/src/main/java/com/scrumcat/entity/StoryStatusLog.java`
- `backend/src/main/java/com/scrumcat/config/WebMvcConfig.java`

前端核心文件：

- `frontend/src/views/SprintBoardView.vue`
- `frontend/src/views/TaskBoardView.vue`
- `frontend/src/components/StoryCard.vue`
- `frontend/src/components/TaskCard.vue`
- `frontend/src/components/TaskDrawer.vue`
- `frontend/src/components/CatImage.vue`
- `frontend/src/api/sprintBoard.js`
- `frontend/src/api/task.js`
- `frontend/src/api/taskBoard.js`
- `frontend/src/router/index.js`
- `frontend/src/layouts/BasicLayout.vue`
- `frontend/src/styles/global.css`

资源占位：

- `frontend/src/assets/imgs/README.md`

## 9. 已知问题或风险

- 前端构建仍有 chunk size warning，主要来自 Ant Design Vue、ECharts 等依赖体积，目前不影响运行。
- 任务当前只有预计工时 `estimated_hours`，尚未实现实际耗时 `actual_hours`。
- 故事状态可以在用户故事页直接修改，但故事看板只展示已加入当前 Sprint 的故事，用户需要理解该业务边界。
- 任务看板只允许任务在所属故事行内流转，不支持跨故事移动。
- 当前没有复杂权限系统，仅基于 JWT 和 `creator_id` 做当前用户数据隔离。
- 工作台 v1 仍未接入真实 Sprint、任务、问题统计数据。
- `issue` 和 `retrospective` 表仍为后续里程碑预留，尚未实现页面和接口。

## 10. 下一步计划：Milestone 04：燃尽图 + 统计分析 + 工作台最终版

Milestone 04 建议目标：

- 实现 Sprint 燃尽图。
- 基于 `story_status_log` 计算故事完成趋势。
- 基于 `task` 表计算任务状态分布、预计工时统计和完成情况。
- 实现统计分析页面，展示项目、Sprint、故事、任务多维度数据。
- 升级工作台最终版，将 Sprint、故事、任务、燃尽图摘要接入真实接口。
- 保持燃尽图和统计分析只读取现有数据，不提前实现迭代回顾和问题追踪。

## 11. 下一次 Codex 新会话需要重点阅读的文件

建议优先阅读：

- `docs/CODEX_HANDOFF.md`
- `docs/NEXT_CONTEXT_MILESTONE_03.md`
- `docs/04-database-design.md`
- `docs/05-api-design.md`
- `docs/06-frontend-page-design.md`
- `docs/15-milestone-03-board-and-task.md`
- `report/milestone-03-summary.md`

建议重点代码：

- `database/init.sql`
- `backend/src/main/java/com/scrumcat/controller/SprintBoardController.java`
- `backend/src/main/java/com/scrumcat/controller/TaskController.java`
- `backend/src/main/java/com/scrumcat/controller/TaskBoardController.java`
- `backend/src/main/java/com/scrumcat/service/impl/SprintBoardServiceImpl.java`
- `backend/src/main/java/com/scrumcat/service/impl/TaskBoardServiceImpl.java`
- `frontend/src/views/SprintBoardView.vue`
- `frontend/src/views/TaskBoardView.vue`
- `frontend/src/views/DashboardView.vue`
- `frontend/src/styles/global.css`
