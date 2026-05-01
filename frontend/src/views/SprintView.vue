<template>
  <section class="page-stack sprint-page">
    <div class="page-toolbar">
      <div>
        <p class="eyebrow">Sprint Planning</p>
        <h1>迭代计划</h1>
        <p class="summary">{{ pageSummary }}</p>
      </div>
      <a-space v-if="projectStore.hasProject" wrap>
        <a-select
          v-model:value="currentSprintId"
          class="sprint-selector"
          placeholder="选择 Sprint"
          :options="sprintOptions"
          @change="loadSprintStories"
        />
        <a-button class="soft-outline-button" :disabled="!currentSprint" @click="openEditModal">编辑 Sprint</a-button>
        <a-button type="primary" @click="openCreateModal">创建 Sprint</a-button>
      </a-space>
    </div>

    <div v-if="!projectStore.hasProject" class="soft-empty project-required">
      请先选择一个项目空间，再规划 Sprint。
      <a-button type="primary" @click="router.push('/projects')">去选择项目</a-button>
    </div>

    <template v-else>
      <a-card v-if="currentSprint" class="soft-card sprint-summary-card">
        <div>
          <p class="eyebrow">{{ statusMeta[currentSprint.status]?.label || currentSprint.status }}</p>
          <h2>{{ currentSprint.name }}</h2>
          <p>{{ currentSprint.goal || '还没有写 Sprint 目标，猫猫建议补一个清晰的小方向。' }}</p>
        </div>
        <div class="sprint-summary-meta">
          <a-tag color="orange">{{ currentSprint.startDate }} 至 {{ currentSprint.endDate }}</a-tag>
          <a-tag v-if="currentSprint.ownerNickname" color="purple">负责人 {{ currentSprint.ownerNickname }}</a-tag>
          <a-tag color="blue">{{ sprintStories.length }} 个故事</a-tag>
          <a-popconfirm title="确认删除这个 Sprint？关联故事会从 Sprint 中移除。" @confirm="handleDeleteSprint">
            <a-button danger size="small">删除</a-button>
          </a-popconfirm>
        </div>
      </a-card>

      <div v-else class="soft-empty sprint-empty">
        当前项目还没有 Sprint，猫猫的冲刺跑道还没搭好。
      </div>

      <div class="sprint-planning-grid">
        <a-card class="soft-card planning-column" title="产品待办">
          <template #extra>
            <a-button size="small" class="soft-outline-button" @click="loadBacklog">刷新</a-button>
          </template>
          <div v-if="availableStories.length === 0" class="soft-empty planning-empty">
            暂无可加入的待办故事。
          </div>
          <div v-else class="planning-list">
            <article
              v-for="story in availableStories"
              :key="story.id"
              class="planning-story"
              draggable="true"
              @dragstart="handleDragStart(story)"
            >
              <div>
                <strong>{{ story.title }}</strong>
                <p>{{ story.storyPoint }} SP · P{{ story.priority || '-' }}</p>
              </div>
              <a-button size="small" :disabled="!currentSprint" @click="handleAddStory(story.id)">加入</a-button>
            </article>
          </div>
        </a-card>

        <a-card
          class="soft-card planning-column sprint-drop-zone"
          :class="{ 'drop-ready': currentSprint }"
          title="当前 Sprint 故事"
          @dragover.prevent
          @drop="handleDropToSprint"
        >
          <div v-if="!currentSprint" class="soft-empty planning-empty">
            请选择或创建 Sprint 后再加入故事。
          </div>
          <div v-else-if="sprintStories.length === 0" class="soft-empty planning-empty">
            这个 Sprint 还没有故事，把左侧卡片拖过来或点击加入。
          </div>
          <div v-else class="planning-list">
            <article v-for="story in sprintStories" :key="story.id" class="planning-story in-sprint">
              <div>
                <strong>{{ story.title }}</strong>
                <p>{{ story.storyPoint }} SP · {{ statusMetaStory[story.status]?.label || story.status }}</p>
              </div>
              <a-button size="small" danger @click="handleRemoveStory(story.id)">移除</a-button>
            </article>
          </div>
        </a-card>
      </div>
    </template>

    <a-modal
      v-model:open="modalOpen"
      :title="editingSprint ? '编辑 Sprint' : '创建 Sprint'"
      @ok="handleSubmitSprint"
      @cancel="closeModal"
    >
      <a-form layout="vertical" :model="form">
        <a-form-item label="名称" required>
          <a-input v-model:value="form.name" placeholder="Sprint 01" />
        </a-form-item>
        <a-form-item label="目标">
          <a-textarea v-model:value="form.goal" :rows="3" placeholder="本轮 Sprint 希望完成什么？" />
        </a-form-item>
        <a-form-item label="日期" required>
          <a-range-picker v-model:value="dateRange" style="width: 100%" value-format="YYYY-MM-DD" />
        </a-form-item>
        <a-form-item label="Sprint 负责人昵称">
          <a-input v-model:value="form.ownerNickname" placeholder="柠檬" />
        </a-form-item>
        <a-form-item label="参与成员昵称">
          <a-select v-model:value="form.members" mode="tags" placeholder="输入昵称后回车" style="width: 100%" />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="form.status">
            <a-select-option value="PLANNED">计划中</a-select-option>
            <a-select-option value="ACTIVE">进行中</a-select-option>
            <a-select-option value="CLOSED">已关闭</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { fetchBacklogApi } from '../api/backlog'
import {
  addSprintStoryApi,
  createSprintApi,
  deleteSprintApi,
  fetchSprintStoriesApi,
  fetchSprintsApi,
  removeSprintStoryApi,
  updateSprintApi
} from '../api/sprint'
import { useProjectStore } from '../stores/project'

const router = useRouter()
const projectStore = useProjectStore()
const statusMeta = {
  PLANNED: { label: '计划中', color: 'gold' },
  ACTIVE: { label: '进行中', color: 'blue' },
  CLOSED: { label: '已关闭', color: 'green' }
}

const statusMetaStory = {
  TODO: { label: '待开发' },
  IN_PROGRESS: { label: '进行中' },
  DONE: { label: '已完成' }
}

const sprints = ref([])
const backlogStories = ref([])
const sprintStories = ref([])
const currentSprintId = ref(null)
const draggedStoryId = ref(null)
const modalOpen = ref(false)
const editingSprint = ref(null)
const dateRange = ref([])
const form = reactive({
  name: '',
  goal: '',
  status: 'PLANNED',
  ownerNickname: '',
  members: []
})

onMounted(loadPageData)
watch(() => projectStore.currentProjectId, loadPageData)

const currentSprint = computed(() =>
  sprints.value.find((sprint) => sprint.id === currentSprintId.value)
)

const sprintOptions = computed(() =>
  sprints.value.map((sprint) => ({
    label: `${sprint.name}（${statusMeta[sprint.status]?.label || sprint.status}）`,
    value: sprint.id
  }))
)

const availableStories = computed(() => {
  const selectedIds = new Set(sprintStories.value.map((story) => story.id))
  return backlogStories.value.filter((story) => !selectedIds.has(story.id))
})

const pageSummary = computed(() => {
  if (!projectStore.hasProject) {
    return '选择项目空间后，再从该项目产品待办中挑选 Sprint 故事。'
  }
  if (!sprints.value.length) {
    return `${projectStore.currentProjectName} 还没有 Sprint。`
  }
  return `${projectStore.currentProjectName} 已有 ${sprints.value.length} 个 Sprint。`
})

async function loadPageData() {
  if (!projectStore.hasProject) {
    sprints.value = []
    backlogStories.value = []
    sprintStories.value = []
    currentSprintId.value = null
    return
  }
  await Promise.all([loadSprints(), loadBacklog()])
}

async function loadSprints() {
  try {
    const response = await fetchSprintsApi(projectStore.currentProjectId)
    sprints.value = response.data || []
    if (!sprints.value.some((sprint) => sprint.id === currentSprintId.value)) {
      currentSprintId.value = sprints.value[0]?.id || null
    }
    await loadSprintStories()
  } catch (error) {
    message.error(error.message || 'Sprint 加载失败')
  }
}

async function loadBacklog() {
  try {
    const response = await fetchBacklogApi(projectStore.currentProjectId, 'todo')
    backlogStories.value = response.data || []
  } catch (error) {
    message.error(error.message || '产品待办加载失败')
  }
}

async function loadSprintStories() {
  if (!currentSprintId.value) {
    sprintStories.value = []
    return
  }

  try {
    const response = await fetchSprintStoriesApi(currentSprintId.value)
    sprintStories.value = response.data || []
  } catch (error) {
    message.error(error.message || 'Sprint 故事加载失败')
  }
}

function openCreateModal() {
  editingSprint.value = null
  Object.assign(form, { name: '', goal: '', status: 'PLANNED', ownerNickname: '', members: [] })
  dateRange.value = []
  modalOpen.value = true
}

function openEditModal() {
  if (!currentSprint.value) {
    return
  }
  editingSprint.value = currentSprint.value
  Object.assign(form, {
    name: currentSprint.value.name,
    goal: currentSprint.value.goal,
    status: currentSprint.value.status,
    ownerNickname: currentSprint.value.ownerNickname,
    members: [...(currentSprint.value.members || [])]
  })
  dateRange.value = [currentSprint.value.startDate, currentSprint.value.endDate]
  modalOpen.value = true
}

function closeModal() {
  modalOpen.value = false
}

async function handleSubmitSprint() {
  if (!form.name || dateRange.value.length !== 2) {
    message.error('请填写名称和日期')
    return
  }

  const payload = {
    projectId: projectStore.currentProjectId,
    name: form.name,
    goal: form.goal,
    startDate: dateRange.value[0],
    endDate: dateRange.value[1],
    status: form.status,
    ownerNickname: form.ownerNickname,
    members: form.members || []
  }

  try {
    if (editingSprint.value) {
      await updateSprintApi(editingSprint.value.id, payload)
      message.success('Sprint 已更新')
    } else {
      const response = await createSprintApi(payload)
      currentSprintId.value = response.data?.id || null
      message.success('Sprint 已创建')
    }
    closeModal()
    await loadSprints()
  } catch (error) {
    message.error(error.message || 'Sprint 保存失败')
  }
}

async function handleDeleteSprint() {
  if (!currentSprint.value) {
    return
  }

  try {
    await deleteSprintApi(currentSprint.value.id)
    message.success('Sprint 已删除')
    currentSprintId.value = null
    sprintStories.value = []
    await loadSprints()
  } catch (error) {
    message.error(error.message || 'Sprint 删除失败')
  }
}

function handleDragStart(story) {
  draggedStoryId.value = story.id
}

async function handleDropToSprint() {
  if (!draggedStoryId.value) {
    return
  }
  await handleAddStory(draggedStoryId.value)
  draggedStoryId.value = null
}

async function handleAddStory(storyId) {
  if (!currentSprint.value) {
    message.info('请先选择或创建 Sprint')
    return
  }

  try {
    await addSprintStoryApi(currentSprint.value.id, storyId)
    message.success('故事已加入 Sprint')
    await loadSprintStories()
  } catch (error) {
    message.error(error.message || '加入 Sprint 失败')
  }
}

async function handleRemoveStory(storyId) {
  if (!currentSprint.value) {
    return
  }

  try {
    await removeSprintStoryApi(currentSprint.value.id, storyId)
    message.success('故事已移出 Sprint')
    await loadSprintStories()
  } catch (error) {
    message.error(error.message || '移除故事失败')
  }
}
</script>
