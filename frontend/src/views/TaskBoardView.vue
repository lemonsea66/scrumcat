<template>
  <section class="page-stack task-board-page">
    <div class="page-toolbar">
      <div>
        <p class="eyebrow">Task Board 任务看板</p>
        <h1>任务看板</h1>
        <p class="summary">{{ pageSummary }}</p>
      </div>
      <a-space v-if="projectStore.hasProject" wrap>
        <a-select
          v-model:value="currentSprintId"
          class="sprint-selector"
          placeholder="选择 Sprint"
          :options="sprintOptions"
          @change="loadBoard"
        />
        <a-button class="soft-outline-button" :loading="loading" @click="loadBoard">刷新</a-button>
      </a-space>
    </div>

    <div v-if="!projectStore.hasProject" class="soft-empty project-required">
      请先选择一个项目空间，再查看任务看板。
      <a-button type="primary" @click="router.push('/projects')">去选择项目</a-button>
    </div>

    <template v-else>
      <a-card v-if="currentSprint" class="soft-card sprint-summary-card board-summary-card">
        <div>
          <p class="eyebrow">{{ statusMetaSprint[currentSprint.status]?.label || currentSprint.status }}</p>
          <h2>{{ currentSprint.name }}</h2>
          <p>{{ currentSprint.goal || '还没有写 Sprint 目标。' }}</p>
        </div>
        <div class="sprint-summary-meta">
          <a-tag color="orange">{{ currentSprint.startDate }} 至 {{ currentSprint.endDate }}</a-tag>
          <a-tag color="blue">总工时 {{ totalHours }}h</a-tag>
          <a-tag color="green">已完成 {{ doneHours }}h</a-tag>
        </div>
      </a-card>

      <div v-if="!currentSprint" class="soft-empty sprint-empty">
        当前项目还没有 Sprint，请先到迭代计划中创建 Sprint。
        <a-button type="primary" @click="router.push('/sprints')">去迭代计划</a-button>
      </div>

      <div v-else-if="!loading && rows.length === 0" class="soft-empty board-empty">
        <CatImage
          name="cat-empty-board.png"
          alt="暂无任务看板内容"
          fallback="这个 Sprint 还没有故事，任务看板暂时很安静。"
          variant="large"
        />
      </div>

      <div v-else class="task-board-table">
        <article v-for="row in rows" :key="row.storyId" class="task-board-row">
          <div class="task-story-cell">
            <strong>{{ row.storyTitle }}</strong>
            <span>{{ row.storyPoint }} SP · P{{ row.priority || '-' }}</span>
            <span v-if="row.ownerNickname">负责人 {{ row.ownerNickname }}</span>
          </div>

          <div v-for="column in columns" :key="`${row.storyId}-${column.key}`" class="task-status-cell">
            <div class="task-column-title">{{ column.label }}任务 · {{ row[column.key].length }}</div>
            <Draggable
              v-model="row[column.key]"
              item-key="id"
              :group="{ name: `task-row-${row.storyId}` }"
              class="task-column-list"
              ghost-class="drag-ghost"
              @change="(event) => handleTaskColumnChange(row, column.status, event)"
            >
              <TaskCard
                v-for="task in row[column.key]"
                :key="task.id"
                :task="task"
                :status-meta="statusMetaTask"
                @change-status="handleTaskStatusButton"
              />
            </Draggable>
            <div v-if="row[column.key].length === 0" class="task-column-empty">
              暂无{{ column.label }}任务
            </div>
          </div>
        </article>
      </div>
    </template>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { VueDraggableNext as Draggable } from 'vue-draggable-next'
import CatImage from '../components/CatImage.vue'
import TaskCard from '../components/TaskCard.vue'
import { fetchSprintsApi } from '../api/sprint'
import { fetchTaskBoardApi } from '../api/taskBoard'
import { updateTaskStatusApi } from '../api/task'
import { useProjectStore } from '../stores/project'

const router = useRouter()
const projectStore = useProjectStore()
const statusMetaSprint = {
  PLANNED: { label: '计划中' },
  ACTIVE: { label: '进行中' },
  CLOSED: { label: '已关闭' }
}
const statusMetaTask = {
  TODO: { label: '未开始', color: 'gold' },
  IN_PROGRESS: { label: '进行中', color: 'blue' },
  DONE: { label: '已完成', color: 'green' }
}
const columns = [
  { key: 'todo', status: 'TODO', label: '未开始' },
  { key: 'inProgress', status: 'IN_PROGRESS', label: '进行中' },
  { key: 'done', status: 'DONE', label: '已完成' }
]

const sprints = ref([])
const rows = ref([])
const currentSprintId = ref(null)
const loading = ref(false)

onMounted(loadPageData)
watch(() => projectStore.currentProjectId, loadPageData)

const currentSprint = computed(() =>
  sprints.value.find((sprint) => sprint.id === currentSprintId.value)
)

const sprintOptions = computed(() =>
  sprints.value.map((sprint) => ({
    label: `${sprint.name}（${statusMetaSprint[sprint.status]?.label || sprint.status}）`,
    value: sprint.id
  }))
)

const totalHours = computed(() =>
  rows.value.reduce((sum, row) => sum + allTasks(row).reduce((taskSum, task) => taskSum + Number(task.estimatedHours || 0), 0), 0)
)

const doneHours = computed(() =>
  rows.value.reduce((sum, row) => sum + row.done.reduce((taskSum, task) => taskSum + Number(task.estimatedHours || 0), 0), 0)
)

const pageSummary = computed(() => {
  if (!projectStore.hasProject) {
    return '选择项目空间后，这里按用户故事分组展示任务流转。'
  }
  if (!currentSprint.value) {
    return `${projectStore.currentProjectName} 当前还没有可查看的 Sprint。`
  }
  return `${projectStore.currentProjectName} · ${currentSprint.value.name}，总工时 ${totalHours.value}h。`
})

async function loadPageData() {
  rows.value = []
  if (!projectStore.hasProject) {
    sprints.value = []
    currentSprintId.value = null
    return
  }
  try {
    const response = await fetchSprintsApi(projectStore.currentProjectId)
    sprints.value = response.data || []
    if (!sprints.value.some((sprint) => sprint.id === currentSprintId.value)) {
      currentSprintId.value = sprints.value[0]?.id || null
    }
    await loadBoard()
  } catch (error) {
    message.error(error.message || 'Sprint 加载失败')
  }
}

async function loadBoard() {
  rows.value = []
  if (!currentSprintId.value) {
    return
  }
  loading.value = true
  try {
    const response = await fetchTaskBoardApi(currentSprintId.value)
    rows.value = response.data || []
  } catch (error) {
    message.error(error.message || '任务看板加载失败')
  } finally {
    loading.value = false
  }
}

async function handleTaskColumnChange(row, status, event) {
  const task = event?.added?.element
  if (!task || task.status === status) {
    return
  }
  if (task.storyId !== row.storyId) {
    message.error('任务不能拖到其他故事下面')
    await loadBoard()
    return
  }

  try {
    await updateTaskStatusApi(task.id, status)
    message.success(status === 'DONE' ? '这个任务完成啦，猫猫获得一条小鱼干。' : '任务状态已保存')
    await loadBoard()
  } catch (error) {
    message.error(error.message || '任务状态保存失败')
    await loadBoard()
  }
}

async function handleTaskStatusButton(task, status) {
  try {
    await updateTaskStatusApi(task.id, status)
    message.success(status === 'DONE' ? '这个任务完成啦，猫猫获得一条小鱼干。' : '任务状态已保存')
    await loadBoard()
  } catch (error) {
    message.error(error.message || '任务状态保存失败')
    await loadBoard()
  }
}

function allTasks(row) {
  return [...row.todo, ...row.inProgress, ...row.done]
}
</script>
