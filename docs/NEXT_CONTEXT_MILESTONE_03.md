# ScrumCat 下一阶段开发上下文

## 当前分支与远端状态

- 当前主开发分支：`dev`
- 当前 Milestone 02 功能提交：`fa16f94 feat: add project workspace backlog and sprint planning`
- GitHub 远端：`origin https://github.com/lemonsea66/scrumcat.git`
- Gitee 远端：`gitee https://gitee.com/lemon-sea-is-not-salty/scrum-cat.git`
- `dev` 已同步到 GitHub 和 Gitee。
- `main` 需要包含 `dev` 的最新成果。

## 已完成里程碑

- Step 0：项目骨架创建
- Milestone 01：登录注册 + 用户故事管理
- Milestone 01.5：工作台 v1 信息架构与 UI
- Milestone 02：项目空间 + 产品待办 + 迭代计划

## 当前系统能力

- 登录、注册、JWT 登录态保持、路由守卫。
- 项目空间 `/projects`：
  - 创建、编辑、删除无关联数据的项目空间。
  - 选择当前项目。
  - 支持项目负责人昵称和团队成员昵称。
- 用户故事 `/stories`：
  - 当前项目下用户故事列表、新增、编辑、删除、状态更新。
  - 支持需求负责人昵称和协作成员昵称。
- 产品待办 `/backlog`：
  - 当前项目下用户故事按 priority 排序。
  - 支持全部 / 待开发筛选。
  - 支持拖拽排序并保存到数据库。
- 迭代计划 `/sprints`：
  - 当前项目下 Sprint 创建、编辑、删除、选择。
  - 支持 Sprint 负责人昵称和参与成员昵称。
  - 支持从当前项目待办加入故事，防止重复加入和跨项目加入。

## 数据库与迁移

Milestone 02 增加了：

- `project`
- `collaboration_member`
- `user_story.project_id`
- `user_story.owner_nickname`
- `sprint.project_id`
- `sprint.owner_nickname`

旧数据库必须执行非破坏性迁移脚本：

```powershell
mysql --default-character-set=utf8mb4 -u root -p -e "source E:/STAR/vibecoding/ScrumCat/database/migrate-milestone-02-project.sql"
```

迁移脚本会：

- 创建项目空间和成员昵称表。
- 为旧用户故事 / Sprint 创建并绑定默认项目。
- 不清空数据库。

## 关键文件索引

- 数据库：`database/init.sql`
- 迁移脚本：`database/migrate-milestone-02-project.sql`
- 后端项目空间：`ProjectController.java`, `ProjectServiceImpl.java`
- 后端用户故事：`UserStoryController.java`, `UserStoryServiceImpl.java`
- 后端产品待办：`BacklogController.java`, `BacklogServiceImpl.java`
- 后端 Sprint：`SprintController.java`, `SprintServiceImpl.java`
- 成员昵称支持：`CollaborationMemberSupport.java`
- 前端项目空间：`frontend/src/views/ProjectView.vue`
- 前端用户故事：`frontend/src/views/StoryView.vue`
- 前端产品待办：`frontend/src/views/BacklogView.vue`
- 前端迭代计划：`frontend/src/views/SprintView.vue`
- 当前项目 store：`frontend/src/stores/project.js`

## 启动方式

后端：

```powershell
cd E:\STAR\vibecoding\ScrumCat\backend
$env:SPRING_PROFILES_ACTIVE="local"
mvn spring-boot:run
```

前端：

```powershell
cd E:\STAR\vibecoding\ScrumCat\frontend
npm run dev -- --host 127.0.0.1 --port 5174
```

## 测试方式

```powershell
cd E:\STAR\vibecoding\ScrumCat\backend
mvn -q -DskipTests package
```

```powershell
cd E:\STAR\vibecoding\ScrumCat\frontend
npm run build
```

手工验证建议：

- 创建项目空间。
- 在当前项目下新增用户故事。
- 在产品待办中拖拽排序并刷新确认顺序保持。
- 创建 Sprint，加入当前项目故事，验证重复加入失败。
- 创建另一个项目，验证不能把其他项目故事加入当前 Sprint。

## 不要重复做的内容

- 不要重复实现登录注册。
- 不要重做工作台 v1。
- 不要重做项目空间、用户故事、产品待办、Sprint 的基础 CRUD。
- 不要创建 `product` 表。
- 不要实现产品线、评审人、真实成员邀请或复杂权限系统。
- 当前仍不要实现故事看板、任务拆解、任务看板、燃尽图、回顾、问题追踪，除非下一阶段明确要求。

## 下一阶段建议

下一块开发可以进入：

- 故事看板：基于当前项目和 Sprint，按 `TODO / IN_PROGRESS / DONE` 展示故事列。
- 或任务拆解准备：在 Sprint 故事下新增轻量任务，并为后续任务看板打基础。

建议优先顺序：

1. 先实现故事看板，复用已有 Sprint 和用户故事状态。
2. 再实现任务拆解和任务看板。
3. 最后实现燃尽图、回顾、问题追踪。

