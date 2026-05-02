<template>
  <section class="workbench-page">
    <div class="workbench-hero compact-hero">
      <div class="hero-copy">
        <p class="eyebrow">ScrumCat 工作台</p>
        <h1>你好，{{ displayName }}</h1>
        <p class="summary">{{ heroSummary }}</p>
      </div>
      <div class="cat-illustration compact-cat image-slot image-slot-workbench-cat" aria-hidden="true">
        <div class="cat-ear cat-ear-left"></div>
        <div class="cat-ear cat-ear-right"></div>
        <div class="cat-face">
          <span class="cat-eye"></span>
          <span class="cat-eye"></span>
          <span class="cat-nose"></span>
          <span class="cat-mouth"></span>
        </div>
      </div>
    </div>

    <div v-if="!projectStore.hasProject" class="soft-empty project-required">
      请先选择一个项目空间，猫猫再为你整理 Sprint 工作台。
      <a-button type="primary" @click="router.push('/projects')">去选择项目</a-button>
    </div>

    <template v-else>
      <a-card class="soft-card dashboard-control-card">
        <div>
          <p class="eyebrow">当前项目</p>
          <strong>{{ projectStore.currentProjectName }}</strong>
        </div>
        <a-space wrap>
          <a-select
            v-model:value="currentSprintId"
            class="sprint-selector"
            placeholder="选择 Sprint"
            :options="sprintOptions"
            @change="loadSprintSummary"
          />
          <a-button class="soft-outline-button" @click="loadSprintSummary" :disabled="!currentSprint">
            重新同步数据
          </a-button>
        </a-space>
      </a-card>

      <a-row :gutter="[16, 16]">
        <a-col v-for="item in stats" :key="item.title" :xs="24" :sm="12" :xl="6">
          <a-card class="soft-card stat-card">
            <p>{{ item.title }}</p>
            <strong>{{ item.value }}</strong>
            <span>{{ item.hint }}</span>
          </a-card>
        </a-col>
      </a-row>

      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :xl="14">
          <a-card class="soft-card sprint-cat-card">
            <div class="sprint-cat-content">
              <p class="eyebrow">Sprint 状态猫 · {{ dashboard.catStatusLevel || '暂无 Sprint' }}</p>
              <h2>{{ dashboard.sprintName || '暂无活跃 Sprint' }}</h2>
              <p>{{ catStatusText }}</p>
              <a-space wrap>
                <a-button type="primary" @click="router.push('/analytics')" :disabled="!currentSprint">
                  查看统计分析
                </a-button>
                <a-button class="soft-outline-button" @click="router.push('/sprints')">调整迭代计划</a-button>
              </a-space>
            </div>
            <div class="mini-cat" aria-hidden="true">=^·ω·^=</div>
          </a-card>
        </a-col>
        <a-col :xs="24" :xl="10">
          <a-card class="soft-card focus-card" title="今日关注">
            <ul>
              <li v-for="item in focusItems" :key="item">{{ item }}</li>
            </ul>
          </a-card>
        </a-col>
      </a-row>

      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :xl="14">
          <a-card class="soft-card chart-card dashboard-burndown-card" title="燃尽趋势小图">
            <BurndownChart v-if="hasBurndownData" :data="burndown" compact />
            <div v-else class="soft-empty">还没有燃尽趋势数据，猫猫正在等你完成第一次迭代计划。</div>
          </a-card>
        </a-col>
        <a-col :xs="24" :xl="10">
          <a-card class="soft-card quick-card" title="快捷入口">
            <div class="quick-grid compact-quick-grid">
              <button class="quick-action primary-action" @click="router.push('/sprint-board')">
                <strong>进入故事看板</strong>
                <span>推进当前 Sprint 的故事状态</span>
              </button>
              <button class="quick-action" @click="router.push('/task-board')">
                <strong>查看任务看板</strong>
                <span>关注任务拆解和执行节奏</span>
              </button>
              <button class="quick-action" @click="router.push('/analytics')">
                <strong>查看统计分析</strong>
                <span>检查燃尽图和状态分布</span>
              </button>
              <button class="quick-action" @click="router.push('/backlog')">
                <strong>整理产品待办</strong>
                <span>为下一轮 Sprint 做准备</span>
              </button>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </template>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import BurndownChart from '../components/BurndownChart.vue'
import { fetchBurndownApi } from '../api/burndown'
import { fetchDashboardSummaryApi } from '../api/dashboard'
import { fetchSprintsApi } from '../api/sprint'
import { useAuthStore } from '../stores/auth'
import { useProjectStore } from '../stores/project'
import { useSprintSelectionStore } from '../stores/sprintSelection'

const router = useRouter()
const authStore = useAuthStore()
const projectStore = useProjectStore()
const sprintSelectionStore = useSprintSelectionStore()
const sprints = ref([])

const dashboard = reactive({
  projectName: '',
  sprintName: '',
  completionRate: 0,
  remainingPoints: 0,
  inProgressTasks: 0,
  doneTasks: 0,
  openIssues: 0,
  catStatusLevel: '',
  catStatus: ''
})

const burndown = reactive({
  dates: [],
  planned: [],
  actual: [],
  totalPoints: 0,
  remainingPoints: 0
})

onMounted(loadDashboard)
watch(() => projectStore.currentProjectId, loadDashboard)

const displayName = computed(() => authStore.user?.nickname || authStore.user?.username || 'ScrumCat 用户')

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
    label: `${sprint.name}（${sprint.startDate} 至 ${sprint.endDate}）`,
    value: sprint.id
  }))
)

const heroSummary = computed(() => {
  if (!projectStore.hasProject) {
    return '猫猫观察：先选择项目空间，工作台就能开始整理 Sprint 节奏。'
  }
  if (!currentSprint.value) {
    return `${projectStore.currentProjectName} 暂无 Sprint，猫猫的冲刺跑道还没搭好。`
  }
  return `${dashboard.projectName || projectStore.currentProjectName} · ${dashboard.sprintName} 当前完成率 ${dashboard.completionRate}% 。`
})

const catStatusText = computed(() =>
  dashboard.catStatus || '猫猫的冲刺跑道还没搭好。'
)

const hasBurndownData = computed(() => burndown.dates.length > 0)

const stats = computed(() => [
  {
    title: '当前 Sprint',
    value: dashboard.sprintName || '暂无',
    hint: dashboard.projectName || projectStore.currentProjectName || '等待选择项目'
  },
  {
    title: 'Sprint 完成率',
    value: `${dashboard.completionRate || 0}%`,
    hint: '按已完成故事点计算'
  },
  {
    title: '剩余故事点',
    value: `${dashboard.remainingPoints || 0} SP`,
    hint: '来自燃尽图实际线'
  },
  {
    title: '进行中任务',
    value: dashboard.inProgressTasks || 0,
    hint: `已完成任务 ${dashboard.doneTasks || 0} 个`
  }
])

const focusItems = computed(() => {
  if (!currentSprint.value) {
    return ['先创建或选择一个 Sprint，让工作台接入真实迭代数据。']
  }

  const items = []
  if ((dashboard.completionRate || 0) < 40) {
    items.push('当前完成率偏低，建议先检查故事看板中是否存在阻塞。')
  } else if ((dashboard.completionRate || 0) < 80) {
    items.push('Sprint 正在稳步推进，可以继续关注进行中故事和任务拆解。')
  } else if ((dashboard.completionRate || 0) < 100) {
    items.push('Sprint 已接近完成，适合收尾剩余故事点和未完成任务。')
  } else {
    items.push('本轮 Sprint 已完成，可以准备下一轮计划。')
  }
  items.push(`当前还有 ${dashboard.remainingPoints || 0} SP 未燃尽。`)
  items.push('待处理问题暂未接入，当前显示为暂无数据。')
  return items
})

async function loadDashboard() {
  resetData()
  if (!projectStore.hasProject) {
    sprints.value = []
    return
  }

  try {
    const sprintResponse = await fetchSprintsApi(projectStore.currentProjectId)
    sprints.value = sprintResponse.data || []
    syncSelectedSprint()
    if (!currentSprintId.value) {
      return
    }

    await loadSprintSummary()
  } catch (error) {
    message.error(error.message || '工作台数据加载失败')
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

async function loadSprintSummary() {
  resetSummaryData()
  if (!projectStore.hasProject || !currentSprintId.value) {
    return
  }

  try {
    const [dashboardResponse, burndownResponse] = await Promise.all([
      fetchDashboardSummaryApi(projectStore.currentProjectId, currentSprintId.value),
      fetchBurndownApi(currentSprintId.value)
    ])
    Object.assign(dashboard, dashboardResponse.data || {})
    Object.assign(burndown, burndownResponse.data || {})
  } catch (error) {
    message.error(error.message || '工作台数据加载失败')
  }
}

function resetData() {
  resetSummaryData()
}

function resetSummaryData() {
  Object.assign(dashboard, {
    projectName: '',
    sprintName: '',
    completionRate: 0,
    remainingPoints: 0,
    inProgressTasks: 0,
    doneTasks: 0,
    openIssues: 0,
    catStatusLevel: '',
    catStatus: ''
  })
  Object.assign(burndown, { dates: [], planned: [], actual: [], totalPoints: 0, remainingPoints: 0 })
}
</script>
