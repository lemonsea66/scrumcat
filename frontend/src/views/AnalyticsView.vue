<template>
  <section class="page-stack analytics-page">
    <div class="page-toolbar">
      <div>
        <p class="eyebrow">Analytics 统计分析</p>
        <h1>统计分析</h1>
        <p class="summary">{{ pageSummary }}</p>
      </div>
      <a-space v-if="projectStore.hasProject" wrap>
        <a-select
          v-model:value="currentSprintId"
          class="sprint-selector"
          placeholder="选择 Sprint"
          :options="sprintOptions"
          @change="loadAnalytics"
        />
        <a-button class="soft-outline-button" :loading="loading" @click="loadAnalytics">刷新</a-button>
      </a-space>
    </div>

    <div v-if="!projectStore.hasProject" class="soft-empty project-required">
      请先选择一个项目空间，再查看统计分析。
      <a-button type="primary" @click="router.push('/projects')">去选择项目</a-button>
    </div>

    <template v-else>
      <div v-if="!currentSprint" class="soft-empty sprint-empty">
        当前项目还没有 Sprint，猫猫正在等你完成第一次迭代计划。
        <a-button type="primary" @click="router.push('/sprints')">去迭代计划</a-button>
      </div>

      <template v-else>
        <a-card class="soft-card sprint-summary-card">
          <div>
            <p class="eyebrow">{{ currentSprint.name }}</p>
            <h2>{{ projectStore.currentProjectName }}</h2>
            <p>{{ currentSprint.goal || '还没有写 Sprint 目标，猫猫建议补一个清晰的小方向。' }}</p>
          </div>
          <div class="sprint-summary-meta">
            <a-tag color="orange">{{ currentSprint.startDate }} 至 {{ currentSprint.endDate }}</a-tag>
            <a-tag color="blue">{{ burndown.totalPoints || 0 }} SP</a-tag>
            <a-tag color="green">剩余 {{ burndown.remainingPoints || 0 }} SP</a-tag>
          </div>
        </a-card>

        <a-card class="soft-card chart-card analytics-burndown-card" title="Sprint 燃尽图">
          <BurndownChart v-if="hasBurndownData" :data="burndown" />
          <div v-else class="soft-empty">还没有可用的 Sprint 数据，猫猫正在等你完成第一次迭代计划。</div>
        </a-card>

        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :xl="8">
            <a-card class="soft-card chart-card" title="用户故事状态分布">
              <StoryStatusChart v-if="hasStoryData" :data="summary.storyStatus" />
              <div v-else class="soft-empty">这个 Sprint 还没有故事状态数据。</div>
            </a-card>
          </a-col>
          <a-col :xs="24" :xl="8">
            <a-card class="soft-card chart-card" title="故事点状态分布">
              <div v-if="hasStoryData" ref="pointChartRef" class="chart-box"></div>
              <div v-else class="soft-empty">这个 Sprint 还没有故事点数据。</div>
            </a-card>
          </a-col>
          <a-col :xs="24" :xl="8">
            <a-card class="soft-card chart-card" title="任务状态分布">
              <TaskStatusChart v-if="hasTaskData" :data="summary.taskStatus" />
              <div v-else class="soft-empty">这个 Sprint 还没有任务数据。</div>
            </a-card>
          </a-col>
        </a-row>
      </template>
    </template>
  </section>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import BurndownChart from '../components/BurndownChart.vue'
import StoryStatusChart from '../components/StoryStatusChart.vue'
import TaskStatusChart from '../components/TaskStatusChart.vue'
import { fetchAnalyticsSummaryApi } from '../api/analytics'
import { fetchBurndownApi } from '../api/burndown'
import { fetchSprintsApi } from '../api/sprint'
import { useProjectStore } from '../stores/project'
import { useSprintSelectionStore } from '../stores/sprintSelection'

const router = useRouter()
const projectStore = useProjectStore()
const sprintSelectionStore = useSprintSelectionStore()
const sprints = ref([])
const loading = ref(false)
const pointChartRef = ref(null)
let pointChart = null

const summary = reactive({
  storyStatus: { todo: 0, inProgress: 0, done: 0 },
  storyPoints: { todo: 0, inProgress: 0, done: 0 },
  taskStatus: { todo: 0, inProgress: 0, done: 0 }
})

const burndown = reactive({
  dates: [],
  planned: [],
  actual: [],
  totalPoints: 0,
  remainingPoints: 0
})

onMounted(loadPageData)
onBeforeUnmount(() => {
  pointChart?.dispose()
  window.removeEventListener('resize', resizeCharts)
})
watch(() => projectStore.currentProjectId, loadPageData)
watch(() => summary.storyPoints, renderPointChart, { deep: true })

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

const hasStoryData = computed(() =>
  summary.storyStatus.todo + summary.storyStatus.inProgress + summary.storyStatus.done > 0
)

const hasTaskData = computed(() =>
  summary.taskStatus.todo + summary.taskStatus.inProgress + summary.taskStatus.done > 0
)

const hasBurndownData = computed(() => burndown.dates.length > 0)

const pageSummary = computed(() => {
  if (!projectStore.hasProject) {
    return '选择项目空间后，这里会展示 Sprint 的燃尽趋势和执行分布。'
  }
  if (!currentSprint.value) {
    return `${projectStore.currentProjectName} 当前还没有 Sprint。`
  }
  return `${projectStore.currentProjectName} · ${currentSprint.value.name} 的执行数据。`
})

async function loadPageData() {
  resetData()
  if (!projectStore.hasProject) {
    sprints.value = []
    return
  }
  try {
    const response = await fetchSprintsApi(projectStore.currentProjectId)
    sprints.value = response.data || []
    syncSelectedSprint()
    await loadAnalytics()
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

async function loadAnalytics() {
  resetData()
  if (!projectStore.hasProject || !currentSprintId.value) {
    return
  }
  loading.value = true
  try {
    const [analyticsResponse, burndownResponse] = await Promise.all([
      fetchAnalyticsSummaryApi(projectStore.currentProjectId, currentSprintId.value),
      fetchBurndownApi(currentSprintId.value)
    ])
    Object.assign(summary.storyStatus, analyticsResponse.data?.storyStatus || {})
    Object.assign(summary.storyPoints, analyticsResponse.data?.storyPoints || {})
    Object.assign(summary.taskStatus, analyticsResponse.data?.taskStatus || {})
    Object.assign(burndown, burndownResponse.data || {})
    await renderPointChart()
  } catch (error) {
    message.error(error.message || '统计数据加载失败')
  } finally {
    loading.value = false
  }
}

function resetData() {
  Object.assign(summary.storyStatus, { todo: 0, inProgress: 0, done: 0 })
  Object.assign(summary.storyPoints, { todo: 0, inProgress: 0, done: 0 })
  Object.assign(summary.taskStatus, { todo: 0, inProgress: 0, done: 0 })
  Object.assign(burndown, { dates: [], planned: [], actual: [], totalPoints: 0, remainingPoints: 0 })
}

async function renderPointChart() {
  await nextTick()
  if (!pointChartRef.value || !hasStoryData.value) {
    pointChart?.dispose()
    pointChart = null
    return
  }
  pointChart = pointChart || echarts.init(pointChartRef.value)
  pointChart.setOption({
    color: ['#B97855'],
    tooltip: { trigger: 'axis' },
    grid: { top: 28, right: 16, bottom: 32, left: 36 },
    xAxis: {
      type: 'category',
      data: ['未开始', '进行中', '已完成'],
      axisTick: { show: false }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        type: 'bar',
        barWidth: 32,
        data: [
          Number(summary.storyPoints.todo || 0),
          Number(summary.storyPoints.inProgress || 0),
          Number(summary.storyPoints.done || 0)
        ],
        itemStyle: { borderRadius: [8, 8, 0, 0] }
      }
    ]
  })
  window.addEventListener('resize', resizeCharts)
}

function resizeCharts() {
  pointChart?.resize()
}
</script>
