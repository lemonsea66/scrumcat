# ScrumCat 可替换猫猫图片位置统计

生成时间：2026-05-02

## 1. 图片替换机制

当前真正可直接替换的图片入口由前端组件 `frontend/src/components/CatImage.vue` 提供。

它会自动读取：

```text
frontend/src/assets/imgs/*.png
```

只要把对应文件名的 PNG 放入 `frontend/src/assets/imgs/`，页面就会优先显示图片；如果文件不存在，则显示当前的文字占位。

建议统一风格：

- 格式：PNG，透明背景优先。
- 色调：低饱和、浅色、温柔治愈。
- 线条：圆润、轻量，不要复杂写实。
- 猫猫形象：原创小猫，适合 ScrumCat 产品气质，表情友好但不过度卖萌。
- 画面留白：图片会放在卡片或空状态区域内，边缘不要塞满主体。

## 2. 已接入的可替换图片

| 文件名 | 使用位置 | 页面/组件 | 当前尺寸样式 | 建议图片 |
|---|---|---|---|---|
| `cat-sprint-tip.png` | 故事看板 Sprint 摘要右侧提示牌 | `frontend/src/views/SprintBoardView.vue` | `variant="sign"`，约 236px 宽，提示牌样式 | 一只猫猫趴在小看板旁边，旁边有轻量便签或小白板，适合承载“推进故事状态”的提示感。 |
| `cat-empty-board.png` | 故事看板空状态 | `frontend/src/views/SprintBoardView.vue` | `variant="large"`，约 360px 宽 | 猫猫趴在空白 Scrum 看板上休息，周围有少量便签纸，表达“这里还没有故事”。 |
| `cat-empty-board.png` | 任务看板空状态 | `frontend/src/views/TaskBoardView.vue` | `variant="large"`，约 360px 宽 | 同一张图也用于任务看板空状态，因此画面应同时适配“暂无故事/暂无任务看板内容”，不要只画故事卡片。 |
| `cat-empty-task.png` | 故事详情抽屉里的任务空状态 | `frontend/src/components/TaskDrawer.vue` | `variant="small"`，约 280px 宽 | 猫猫坐在一张大用户故事卡旁，把它拆成几张小便签或小鱼干形状任务，表达“等待任务拆解”。 |

## 3. 已预留但当前未真正接入的图片

| 文件名 | 当前状态 | 建议后续用途 |
|---|---|---|
| `cat-done-task.png` | `frontend/src/assets/imgs/README.md` 中列出，但当前代码没有任何 `CatImage` 使用它 | 可用于未来“任务完成”弹层、完成态空状态、或任务卡片完成反馈。建议画猫猫拿到一条小鱼干或给任务盖章，但目前放入目录不会自动显示。 |

## 4. CSS 猫猫占位，不是直接图片接口

这些位置目前不是通过 `CatImage.vue` 加载图片，而是用 CSS 或文字画出的猫猫。若要替换成真实图片，需要后续改代码。

| 位置 | 文件 | 当前表现 | 推荐图片方向 |
|---|---|---|---|
| 工作台顶部猫猫 | `frontend/src/views/DashboardView.vue` + `frontend/src/styles/global.css` 中 `.cat-illustration` / `.image-slot-workbench-cat` | CSS 画出的猫脸，占位类名已经保留 | 工作台欢迎猫：半身小猫坐在仪表盘/便签旁，神态轻松，适合首页第一屏。建议横向构图，透明 PNG，约 220x170。 |
| 工作台 Sprint 状态猫 | `frontend/src/views/DashboardView.vue` 中 `.mini-cat` | 文本 `=^·ω·^=` | 可按完成率区间替换多张状态猫：需要关注、稳步推进、接近完成、已完成。当前只返回文案，没有图片映射。 |

## 5. Sprint 状态猫建议图片组

Milestone 04 已经实现按完成率返回状态文案，图片位目前保留在工作台。后续如果要做成多状态图片，建议新增以下文件名并建立前端映射：

| 建议文件名 | 对应状态 | 图片描述 |
|---|---|---|
| `cat-sprint-none.png` | 无 Sprint | 猫猫站在还没搭好的小跑道旁，旁边是空白计划板，表情期待。 |
| `cat-sprint-attention.png` | 0%-40%，需要关注 | 猫猫轻轻拍看板，几张便签略微倾斜，表达需要检查阻塞但不要焦虑。 |
| `cat-sprint-steady.png` | 40%-80%，稳步推进 | 猫猫和便签板一起前进，画面稳定、温和，适合“节奏正常”。 |
| `cat-sprint-almost.png` | 80%-99%，接近完成 | 猫猫看向终点或小鱼干奖励，旁边剩少量任务便签。 |
| `cat-sprint-done.png` | 100%，已完成 | 猫猫拿到小鱼干或给 Sprint 盖完成章，庆祝感轻一点，不要大面积高饱和。 |

## 6. 具体图片规格建议

### `cat-sprint-tip.png`

- 用途：故事看板右侧提示牌。
- 构图：横向小插图，猫猫 + 小看板/便签。
- 建议尺寸：472x208 或 708x312，透明背景。
- 注意：不要把文字画死在图片里，因为页面 fallback 文案会动态变化。

### `cat-empty-board.png`

- 用途：故事看板和任务看板空状态。
- 构图：猫猫趴在空看板上，旁边几张空便签。
- 建议尺寸：720x360，透明背景。
- 注意：要兼容“没有故事”和“没有任务看板内容”两种语义。

### `cat-empty-task.png`

- 用途：故事详情抽屉任务空状态。
- 构图：猫猫把大卡片拆成小任务便签，或者把小鱼干排成待办清单。
- 建议尺寸：560x280，透明背景。
- 注意：抽屉空间较窄，主体要居中，细节不要太密。

### 工作台顶部猫猫

- 当前状态：CSS 猫，不会自动读取图片。
- 若后续接入，建议文件名：`cat-workbench-hero.png`。
- 构图：猫猫坐在 Sprint 仪表盘、燃尽图小线条或便签旁。
- 建议尺寸：440x320，透明背景。
- 注意：这是工作台第一视觉，不要太抢眼，应服务于欢迎和状态概览。

## 7. 当前文件清单

当前图片目录：

```text
frontend/src/assets/imgs/
```

当前说明文件：

```text
frontend/src/assets/imgs/README.md
```

当前直接支持的文件名：

```text
cat-empty-board.png
cat-empty-task.png
cat-sprint-tip.png
```

当前 README 中额外预留但未接入：

```text
cat-done-task.png
```

