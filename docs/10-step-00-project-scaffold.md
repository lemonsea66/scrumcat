# 10-step-00-project-scaffold.md

# 第 0 步：创建 ScrumCat 项目骨架

## 1. 当前任务定位

本文件是 ScrumCat 项目的第 0 步开发任务单，目标是指导 Codex / Trae Builder 创建一个可运行、可扩展、结构清晰的前后端分离项目骨架。

本阶段只完成项目基础结构搭建，不实现具体业务功能。

---

## 2. 项目基本信息

| 项目项 | 内容 |
|---|---|
| 项目名称 | ScrumCat：轻量级 Scrum 过程管理平台 |
| 开发方式 | 个人独立开发 |
| 架构模式 | 前后端分离 |
| 前端技术栈 | Vue 3 + Vite + Ant Design Vue + Vue Router + Pinia + Axios + ECharts + VueDraggableNext |
| 后端技术栈 | Spring Boot + MyBatis-Plus + MySQL + JWT |
| 数据库 | MySQL 8.x |
| JDK | JDK 17 |
| 前端包管理器 | npm |
| 版本管理 | Git，main + dev 分支 |
| UI 风格 | 温柔治愈、简洁现代、轻量猫猫主题 |
| 当前阶段 | 仅创建项目骨架 |

---

## 3. 本阶段目标

第 0 步需要完成以下目标：

1. 创建项目根目录结构。
2. 初始化 Vue 3 前端项目。
3. 安装前端基础依赖。
4. 初始化 Spring Boot 后端项目。
5. 添加后端基础依赖。
6. 创建前后端基础目录结构。
7. 创建数据库脚本目录和占位 SQL 文件。
8. 创建接口测试目录。
9. 创建基础 README.md。
10. 创建 `.gitignore`。
11. 验证前端和后端能够分别启动。
12. 不实现任何具体业务模块。

---

## 4. 严格范围控制

请 Codex 严格遵守以下限制：

1. 当前只创建项目骨架。
2. 不要实现登录注册模块。
3. 不要实现用户故事管理模块。
4. 不要实现产品待办列表模块。
5. 不要实现 Sprint 迭代计划模块。
6. 不要实现故事看板或任务看板。
7. 不要实现燃尽图。
8. 不要实现迭代回顾。
9. 不要实现问题追踪。
10. 不要创建完整业务数据库表。
11. 不要写复杂权限系统。
12. 不要一次性生成完整系统。
13. 允许创建占位页面、占位目录、占位配置文件。
14. 所有真实业务功能应留到后续模块任务单中实现。

本阶段的核心原则是：

```text
先搭骨架，不写业务。
先能启动，不追求完整。
先保持清晰，不提前复杂化。
```

---

## 5. 推荐项目根目录结构

请在项目根目录下形成如下结构：

```text
ScrumCat/
├── frontend/                 # Vue 3 前端项目
├── backend/                  # Spring Boot 后端项目
├── database/                 # 数据库脚本目录
├── docs/                     # 项目文档目录
├── api-tests/                # 接口测试目录
├── README.md                 # 项目说明
└── .gitignore                # Git 忽略规则
```

说明：

- `frontend/` 和 `backend/` 应该是独立项目。
- 前端和后端通过 RESTful API 通信。
- 当前阶段可以暂时不配置真实业务接口，但后端需要预留跨域配置位置。
- `docs/` 中应保留已有的项目文档，例如 `00-project-overview.md` 和本文件。
- `api-tests/` 当前只需要创建目录，可以预留 `README.md`。

---

## 6. 前端项目骨架要求

### 6.1 初始化方式

在 `frontend/` 目录下创建 Vue 3 + Vite 项目。

建议技术要求：

```text
Vue 3
Vite
JavaScript 优先
```

为了降低实现难度，当前项目建议优先使用 **JavaScript**。如果 Codex 判断使用 TypeScript 更合适，需要先说明理由，不要擅自切换复杂方案。

### 6.2 需要安装的前端依赖

请安装以下依赖：

```text
ant-design-vue
@ant-design/icons-vue
vue-router
pinia
axios
echarts
vue-draggable-next
```

如需安装其他依赖，必须说明原因。

### 6.3 前端基础目录结构

请在 `frontend/src/` 下创建如下目录：

```text
frontend/src/
├── api/                 # Axios 请求封装与接口文件
├── assets/              # 静态资源
├── components/          # 公共组件
├── layouts/             # 页面布局组件
├── router/              # 路由配置
├── stores/              # Pinia 状态管理
├── styles/              # 全局样式
├── utils/               # 前端工具函数
└── views/               # 页面视图
```

### 6.4 前端占位页面要求

当前阶段只需要创建最小可运行页面，不实现业务功能。

建议创建：

```text
frontend/src/views/HomeView.vue
frontend/src/layouts/BasicLayout.vue
frontend/src/router/index.js
frontend/src/stores/index.js
frontend/src/api/request.js
frontend/src/styles/global.css
```

首页占位内容可以包含：

```text
ScrumCat
轻量级 Scrum 过程管理平台
当前阶段：项目骨架已创建
猫猫正在整理 Sprint 的第一块积木。
```

### 6.5 前端启动命令

前端应支持以下命令启动：

```bash
cd frontend
npm install
npm run dev
```

启动成功后，浏览器应能看到 ScrumCat 占位首页。

---

## 7. 后端项目骨架要求

### 7.1 初始化方式

在 `backend/` 目录下创建 Spring Boot 项目。

建议版本：

```text
JDK 17
Spring Boot 3.x
Maven
```

### 7.2 后端基础依赖

请在 `pom.xml` 中添加以下基础依赖：

```text
Spring Web
MyBatis-Plus
MySQL Driver
Validation
Lombok
JWT 相关依赖
```

JWT 依赖可以使用常见方案，例如：

```text
io.jsonwebtoken:jjwt-api
io.jsonwebtoken:jjwt-impl
io.jsonwebtoken:jjwt-jackson
```

或者使用其他稳定 JWT 依赖，但需要说明原因。

### 7.3 后端基础包结构

请在后端主包下创建如下目录：

```text
backend/src/main/java/.../
├── controller/          # Controller 层
├── service/             # Service 接口层
├── service/impl/        # Service 实现层
├── mapper/              # MyBatis-Plus Mapper
├── entity/              # 数据库实体类
├── dto/                 # 请求参数对象
├── vo/                  # 响应视图对象
├── config/              # 配置类
├── common/              # 通用返回结果、常量等
├── exception/           # 全局异常处理
└── utils/               # 工具类
```

当前阶段只需要创建目录和必要的占位类，不要实现具体业务 Controller。

### 7.4 后端配置文件

请创建或配置：

```text
backend/src/main/resources/application.yml
```

基础配置建议如下,注意这里我把密码给你了，你必须准备两份配置文件，实际提交github的时候不允许提交代码（用占位符）：

```yaml
server:
  port: 8080

spring:
  application:
    name: scrumcat-backend
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/scrumcat?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: yuxuan2003

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto

jwt:
  secret: scrumcat-dev-secret-change-later
  expiration: 86400000
```

注意：

- `password` 必须使用占位符 `your_password`。
- 不要写入真实数据库密码。
- 如果需要本地修改密码，由开发者手动修改，不提交真实密码。
- 当前阶段可以先保留数据库连接配置；如果后端启动时因数据库未创建失败，应提醒先创建数据库。

### 7.5 后端基础类建议

可以创建以下非业务基础类：

```text
common/Result.java
exception/GlobalExceptionHandler.java
config/CorsConfig.java
utils/JwtUtils.java
```

说明：

- `Result.java` 用于统一接口返回结构，可以先保留基础结构。
- `GlobalExceptionHandler.java` 可以先做简单异常返回。
- `CorsConfig.java` 用于后续前后端联调。
- `JwtUtils.java` 当前可以只预留工具类结构，不必接入登录业务。

不要创建：

```text
AuthController
UserStoryController
SprintController
TaskController
IssueController
```

这些业务 Controller 应在后续模块任务中创建。

### 7.6 后端启动命令

后端应支持以下命令启动：

```bash
cd backend
mvn spring-boot:run
```

如果数据库不存在，需要先执行：

```sql
CREATE DATABASE scrumcat DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## 8. 数据库目录要求

请创建：

```text
database/
├── init.sql
└── README.md
```

### 8.1 init.sql 当前内容要求

当前阶段 `init.sql` 只做占位，不创建完整业务表。

建议内容：

```sql
-- ScrumCat database initialization script
-- Current stage: project scaffold only.
-- Business tables will be added in later database design documents.

CREATE DATABASE IF NOT EXISTS scrumcat
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

后续完整表结构会在 `04-database-design.md` 中确定。

---

## 9. 接口测试目录要求

请创建：

```text
api-tests/
└── README.md
```

当前阶段只需要说明：

```text
This directory is used to store Postman or Apifox API test collections.
API test cases will be added after backend modules are implemented.
```

不要生成具体测试用例。

---

## 10. README.md 要求

请在项目根目录创建或更新 `README.md`。

README 至少包含以下内容：

1. 项目名称。
2. 项目简介。
3. 技术栈。
4. 项目结构。
5. 前端启动方式。
6. 后端启动方式。
7. 数据库初始化方式。
8. Git 分支策略。
9. 当前阶段说明。

### 10.1 README 中的当前阶段说明

需要明确写出：

```text
当前阶段为第 0 步：项目骨架创建。
当前尚未实现登录注册、用户故事管理、Sprint 计划、看板、燃尽图、回顾和问题追踪等业务功能。
```

---

## 11. `.gitignore` 要求

请在项目根目录创建 `.gitignore`。

至少包含：

```gitignore
# Node
node_modules/
dist/
npm-debug.log*
yarn-debug.log*
yarn-error.log*
pnpm-debug.log*

# Java / Maven
target/
*.class

# IDE
.idea/
.vscode/
*.iml

# Logs
*.log

# OS
.DS_Store
Thumbs.db

# Environment
.env
.env.local
application-local.yml
```

如果前端或后端子项目生成了自己的 `.gitignore`，也可以保留，但根目录 `.gitignore` 必须存在。

---

## 12. Git 初始化建议

如果当前项目尚未初始化 Git，可以执行：

```bash
git init
git branch -M main
```

骨架创建完成并验证通过后，建议先提交到 `main`：

```bash
git add .
git commit -m "init: create ScrumCat project scaffold"
```

然后创建开发分支：

```bash
git checkout -b dev
```

后续日常开发在 `dev` 分支进行。

如果已经存在 Git 仓库，则不要重复初始化，只需要确认当前分支状态。

---

## 13. 验收标准

完成第 0 步后，需要满足以下标准：

### 13.1 目录结构验收

项目根目录应包含：

```text
frontend/
backend/
database/
docs/
api-tests/
README.md
.gitignore
```

### 13.2 前端验收

在 `frontend/` 目录下执行：

```bash
npm install
npm run dev
```

验收结果：

- 前端可以正常启动。
- 浏览器可以访问前端页面。
- 页面显示 ScrumCat 项目占位信息。
- 控制台无明显启动报错。

### 13.3 后端验收

在 `backend/` 目录下执行：

```bash
mvn spring-boot:run
```

验收结果：

- 后端可以正常启动。
- 服务端口为 `8080`。
- 如果因数据库不存在导致启动失败，应先创建 `scrumcat` 数据库。
- 当前阶段不要求业务接口可用。

### 13.4 范围验收

本阶段不应出现完整业务功能。

不应出现：

```text
完整登录注册流程
完整用户故事 CRUD
完整 Sprint 计划功能
完整看板拖拽功能
完整燃尽图功能
完整问题追踪功能
```

如果 Codex 误实现了业务功能，应在输出中明确说明，并建议回退或保留到后续模块中处理。

---

## 14. Codex 执行 Prompt

可以将以下内容直接复制给 Codex：

```text
你现在是 ScrumCat 项目的代码实现助手。

请阅读 docs/00-project-overview.md 和 docs/10-step-00-project-scaffold.md。

现在开始执行第 0 步：创建项目骨架。

请严格遵守以下要求：

1. 当前只创建前后端分离项目骨架，不实现任何业务功能。
2. 不要实现登录注册、用户故事、产品待办、Sprint、看板、燃尽图、迭代回顾、问题追踪等模块。
3. 不要创建完整业务数据库表，只在 database/ 中创建 init.sql 占位文件。
4. 不要写复杂权限系统，只预留 JWT 相关依赖和工具类位置。
5. 不要一次性生成完整系统。

本地环境默认如下：

- Node.js：18+ 或 20+
- 包管理器：npm
- JDK：17
- Maven：3.8+
- MySQL：8.x
- MySQL 用户名：root
- MySQL 端口：3306
- 数据库名：scrumcat
- MySQL 密码：使用占位符 your_password，不要写死真实密码
- 接口测试工具：暂定 Apifox/Postman，当前只创建 api-tests/ 目录

请完成：

1. 创建或确认项目根目录结构：
   - frontend/
   - backend/
   - database/
   - docs/
   - api-tests/

2. 初始化 frontend/：
   - Vue 3 + Vite
   - 安装 Ant Design Vue、Vue Router、Pinia、Axios、ECharts、VueDraggableNext
   - 创建基础目录：
     - src/api/
     - src/assets/
     - src/components/
     - src/layouts/
     - src/router/
     - src/stores/
     - src/styles/
     - src/utils/
     - src/views/
   - 创建最小可运行首页占位，不做业务功能

3. 初始化 backend/：
   - Spring Boot + JDK 17 + Maven
   - 添加 Spring Web、MyBatis-Plus、MySQL Driver、Validation、Lombok、JWT 相关依赖
   - 创建基础包结构：
     - controller
     - service
     - service.impl
     - mapper
     - entity
     - dto
     - vo
     - config
     - common
     - exception
     - utils
   - 创建 application.yml
   - 数据库名使用 scrumcat
   - 数据库密码使用 your_password
   - 不要实现具体业务接口

4. 在 database/ 下创建 init.sql 和 README.md。

5. 在 api-tests/ 下创建 README.md。

6. 创建或更新根目录 README.md，说明项目结构、启动方式、数据库初始化方式、Git 分支策略和当前阶段说明。

7. 创建或更新根目录 .gitignore。

8. 完成后请输出：
   - 新增和修改的文件列表
   - 如何启动前端
   - 如何启动后端
   - 如何初始化数据库
   - 当前没有实现哪些业务功能
   - 下一步建议
```

---

## 15. 完成后的人工检查清单

开发者需要人工检查：

```text
[ ] frontend/ 是否存在
[ ] backend/ 是否存在
[ ] database/init.sql 是否存在
[ ] api-tests/README.md 是否存在
[ ] 根目录 README.md 是否存在
[ ] 根目录 .gitignore 是否存在
[ ] frontend 能否 npm install
[ ] frontend 能否 npm run dev
[ ] backend 能否 mvn spring-boot:run
[ ] application.yml 是否没有真实密码
[ ] Codex 是否没有提前实现业务模块
[ ] 是否可以进行第一次 git commit
```

---

## 16. 第 0 步完成后的推荐 Git 提交

如果项目骨架已经创建完成，并且前后端能够启动，建议执行：

```bash
git status
git add .
git commit -m "init: create ScrumCat project scaffold"
```

如果准备采用 `main + dev` 分支策略，建议随后执行：

```bash
git checkout -b dev
```

后续开发都在 `dev` 分支进行。

---

## 17. 下一步计划

第 0 步完成后，进入系统设计文档阶段。

下一批建议生成：

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
```

其中最关键的是：

```text
04-database-design.md
05-api-design.md
06-frontend-page-design.md
```

这三个文件会作为后续各功能模块开发的核心依据。
