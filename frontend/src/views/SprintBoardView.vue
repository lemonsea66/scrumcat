<template>
  <section class="page-stack board-page">
    <div class="page-toolbar">
      <div>
        <p class="eyebrow">Sprint Board 故事看板</p>
        <h1>故事看板</h1>
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
      请先选择一个项目空间，再查看故事看板。
      <a-button type="primary" @click="router.push('/projects')">去选择项目</a-button>
    </div>

    <template v-else>
      <a-card v-if="currentSprint" class="soft-card sprint-summary-card board-summary-card">
        <div>
          <p class="eyebrow">{{ statusMetaSprint[currentSprint.status]?.label || currentSprint.status }}</p>
          <h2>{{ currentSprint.name }}</h2>
          <p>{{ currentSprint.goal || '还没有写 Sprint 目标，猫猫建议补一个清晰的小方向。' }}</p>
        </div>
        <div class="sprint-summary-side">
          <div class="sprint-summary-meta">
            <a-tag color="orange">{{ currentSprint.startDate }} 至 {{ currentSprint.endDate }}</a-tag>
            <a-tag v-if="currentSprint.ownerNickname" color="purple">负责人 {{ currentSprint.ownerNickname }}</a-tag>
            <a-tag color="blue">{{ storyCount }} 个故事</a-tag>
          </div>
          <CatImage
            name="cat-sprint-tip.png"
            alt="故事看板提示"
            :fallback="`猫猫提示\n${catTipText}`"
            variant="sign"
          />
        </div>
      </a-card>

      <div v-if="!currentSprint" class="soft-empty sprint-empty">
        当前项目还没有 Sprint，请先到迭代计划中创建 Sprint。
        <a-button type="primary" @click="router.push('/sprints')">去迭代计划</a-button>
      </div>

      <div v-else-if="!loading && storyCount === 0" class="soft-empty board-empty">
        <CatImage
          name="cat-empty-board.png"
          alt="暂无故事"
          fallback="这里还没有故事，猫猫暂时趴在看板上休息。"
          variant="large"
        />
      </div>

      <div v-else class="sprint-board-grid">
        <a-card
          v-for="column in columns"
          :key="column.status"
          class="soft-card board-column"
          :title="`${column.label} · ${board[column.key].length}`"
        >
          <Draggable
            v-model="board[column.key]"
            item-key="id"
            group="sprint-stories"
            class="board-column-list"
            ghost-class="drag-ghost"
            @change="(event) => handleStoryColumnChange(column.status, event)"
          >
            <StoryCard
              v-for="story in board[column.key]"
              :key="story.id"
              :story="story"
              :status-meta="statusMetaStory"
              @open="openStoryDrawer"
            />
          </Draggable>
          <div v-if="!loading && board[column.key].length === 0" class="board-column-empty">
            暂无{{ column.label }}故事
          </div>
        </a-card>
      </div>
    </template>

    <TaskDrawer
      :open="drawerOpen"
      :story="selectedStory"
      @close="drawerOpen = false"
      @changed="loadBoard"
    />
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { VueDraggableNext as Draggable } from 'vue-draggable-next'
import CatImage from '../components/CatImage.vue'
import StoryCard from '../components/StoryCard.vue'
import TaskDrawer from '../components/TaskDrawer.vue'
import { fetchSprintsApi } from '../api/sprint'
import { fetchSprintBoardApi, updateSprintBoardStoryStatusApi } from '../api/sprintBoard'
import { useProjectStore } from '../stores/project'
import { useSprintSelectionStore } from '../stores/sprintSelection'

const router = useRouter()
const projectStore = useProjectStore()
const sprintSelectionStore = useSprintSelectionStore()
const statusMetaSprint = {
  PLANNED: { label: '计划中' },
  ACTIVE: { label: '进行中' },
  CLOSED: { label: '已关闭' }
}
const statusMetaStory = {
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
const loading = ref(false)
const drawerOpen = ref(false)
const selectedStory = ref(null)
const catTipText = ref('拖动故事卡片，或点击卡片拆解任务。')
const board = reactive({
  todo: [],
  inProgress: [],
  done: []
})

onMounted(loadPageData)
watch(() => projectStore.currentProjectId, loadPageData)

const currentSprint = computed(() =>
  sprints.value.find((sprint) => sprint.id === currentSprintId.value)
)

const currentSprintId = computed({
  get: () => sprintSelectionStore.getSprintId(projectStore.currentProjectId),
  set: (id) => {
    const sprint = sprints.value.find((item) => item.id === id)
    if (sprint) {
      sprintSelectionStore.selectSprint(projectStore.currentProjectId, sprint)
    }
  }
})

const sprintOptions = computed(() =>
  sprints.value.map((sprint) => ({
    label: `${sprint.name}（${statusMetaSprint[sprint.status]?.label || sprint.status}）`,
    value: sprint.id
  }))
)

const storyCount = computed(() => board.todo.length + board.inProgress.length + board.done.length)

const pageSummary = computed(() => {
  if (!projectStore.hasProject) {
    return '选择项目空间后，这里展示当前 Sprint 的故事执行状态。'
  }
  if (!currentSprint.value) {
    return `${projectStore.currentProjectName} 当前还没有可查看的 Sprint。`
  }
  return `${projectStore.currentProjectName} · ${currentSprint.value.name}，共 ${storyCount.value} 个故事。`
})

async function loadPageData() {
  resetBoard()
  if (!projectStore.hasProject) {
    sprints.value = []
    return
  }
  try {
    const response = await fetchSprintsApi(projectStore.currentProjectId)
    sprints.value = response.data || []
    syncSelectedSprint()
    await loadBoard()
  } catch (error) {
    message.error(error.message || 'Sprint 加载失败')
  }
}

function syncSelectedSprint() {
  if (!sprints.value.length) {
    sprintSelectionStore.clearSprint(projectStore.currentProjectId)
    return
  }
  if (!sprints.value.some((sprint) => sprint.id === currentSprintId.value)) {
    sprintSelectionStore.selectSprint(projectStore.currentProjectId, sprints.value[0])
  }
}

async function loadBoard() {
  resetBoard()
  if (!currentSprintId.value) {
    return
  }
  loading.value = true
  try {
    const response = await fetchSprintBoardApi(currentSprintId.value)
    setBoard(response.data || {})
  } catch (error) {
    message.error(error.message || '故事看板加载失败')
  } finally {
    loading.value = false
  }
}

async function handleStoryColumnChange(status, event) {
  const story = event?.added?.element
  if (!story || story.status === status) {
    return
  }

  try {
    await updateSprintBoardStoryStatusApi(currentSprintId.value, story.id, status)
    catTipText.value = getCatTipText(status)
    message.success('故事状态已保存')
    await loadBoard()
  } catch (error) {
    message.error(error.message || '故事状态保存失败')
    await loadBoard()
  }
}

function openStoryDrawer(story) {
  selectedStory.value = story
  drawerOpen.value = true
}

function setBoard(data) {
  board.todo = data.todo || []
  board.inProgress = data.inProgress || []
  board.done = data.done || []
}

function resetBoard() {
  setBoard({})
}

function getCatTipText(status) {
  if (status === 'IN_PROGRESS') {
    return '开工啦，猫猫在旁边给你加油。'
  }
  if (status === 'DONE') {
    return '这个故事完成啦，猫猫已经看到小鱼干了。'
  }
  return '先放回未开始也没关系，整理清楚再推进。'
}
</script>
