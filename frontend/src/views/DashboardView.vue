<template>
  <section class="workbench-page">
    <div class="workbench-hero compact-hero">
      <div class="hero-copy">
        <p class="eyebrow">ScrumCat 工作台</p>
        <h1>你好，{{ displayName }}</h1>
        <p class="summary">{{ catObservation }}</p>
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
      <a-col :xs="24" :xl="12">
        <a-card class="soft-card chart-card" title="用户故事状态分布">
          <div v-if="storyCount" ref="statusChartRef" class="chart-box"></div>
          <div v-else class="soft-empty">还没有用户故事，猫猫在等你的第一个需求。</div>
        </a-card>
      </a-col>
      <a-col :xs="24" :xl="12">
        <a-card class="soft-card chart-card" title="故事点状态分布">
          <div v-if="storyCount" ref="pointChartRef" class="chart-box"></div>
          <div v-else class="soft-empty">创建故事后，这里会显示不同状态的故事点。</div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :xl="14">
        <a-card class="soft-card sprint-cat-card">
          <div class="sprint-cat-content">
            <p class="eyebrow">Sprint 状态猫</p>
            <h2>暂无活跃 Sprint</h2>
            <p>{{ sprintCatHint }}</p>
            <a-space wrap>
              <a-button type="primary" @click="router.push('/stories')">去整理故事</a-button>
              <a-button class="soft-outline-button" @click="showComingSoon('迭代计划')">创建迭代计划</a-button>
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
        <a-card class="soft-card story-preview-card" title="最近用户故事 / 产品待办预览">
          <div v-if="recentStories.length" class="story-preview-list">
            <div v-for="story in recentStories" :key="story.id" class="story-preview-item">
              <div>
                <strong>{{ story.title }}</strong>
                <span>P{{ story.priority || '-' }}</span>
              </div>
              <div class="story-preview-meta">
                <a-tag :color="statusMeta[story.status]?.color">{{ statusMeta[story.status]?.label }}</a-tag>
                <a-tag color="orange">{{ story.storyPoint }} SP</a-tag>
              </div>
            </div>
          </div>
          <div v-else class="soft-empty">还没有用户故事，猫猫在等你的第一个需求。</div>
        </a-card>
      </a-col>
      <a-col :xs="24" :xl="10">
        <a-card class="soft-card quick-card" title="快捷入口">
          <div class="quick-grid compact-quick-grid">
            <button class="quick-action primary-action" @click="router.push('/stories')">
              <strong>管理用户故事</strong>
              <span>把需求写成清晰的故事</span>
            </button>
            <button class="quick-action" @click="showComingSoon('产品待办')">
              <strong>整理产品待办</strong>
              <span>给故事排好优先级</span>
            </button>
            <button class="quick-action" @click="showComingSoon('迭代计划')">
              <strong>创建迭代计划</strong>
              <span>挑选合适的 Sprint 目标</span>
            </button>
            <button class="quick-action" @click="showComingSoon('故事看板')">
              <strong>进入故事看板</strong>
              <span>推进故事状态流转</span>
            </button>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </section>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { useAuthStore } from '../stores/auth'
import { fetchStoriesApi } from '../api/story'

const router = useRouter()
const authStore = useAuthStore()
const stories = ref([])
const statusChartRef = ref(null)
const pointChartRef = ref(null)
let statusChart = null
let pointChart = null

const statusMeta = {
  TODO: { label: '待办', color: 'gold' },
  IN_PROGRESS: { label: '进行中', color: 'blue' },
  DONE: { label: '已完成', color: 'green' }
}

const displayName = computed(() => authStore.user?.nickname || authStore.user?.username || 'ScrumCat 用户')
const storyCount = computed(() => stories.value.length)
const todoCount = computed(() => countByStatus('TODO'))
const inProgressCount = computed(() => countByStatus('IN_PROGRESS'))
const doneCount = computed(() => countByStatus('DONE'))
const totalStoryPoints = computed(() => sumPoints(stories.value))

const pointByStatus = computed(() => ({
  TODO: sumPoints(stories.value.filter((story) => story.status === 'TODO')),
  IN_PROGRESS: sumPoints(stories.value.filter((story) => story.status === 'IN_PROGRESS')),
  DONE: sumPoints(stories.value.filter((story) => story.status === 'DONE'))
}))

const catObservation = computed(() => {
  if (storyCount.value === 0) {
    return '猫猫观察：还没有用户故事，先写下第一个清晰的小目标吧。'
  }
  return `猫猫观察：你已经整理了 ${storyCount.value} 个用户故事，接下来可以开始排待办啦。`
})

const sprintCatHint = computed(() => {
  if (storyCount.value === 0) {
    return '猫猫的冲刺跑道还没搭好。你可以先整理用户故事，再挑选适合进入第一轮 Sprint 的小目标。'
  }
  return `故事池里已有 ${storyCount.value} 个故事，其中 ${todoCount.value} 个还在待办。可以先检查优先级，再为第一轮 Sprint 做准备。`
})

const stats = computed(() => [
  { title: '用户故事总数', value: storyCount.value, hint: '当前账号下的故事数量' },
  { title: '待办故事', value: todoCount.value, hint: '等待进入开发节奏' },
  { title: '进行中故事', value: inProgressCount.value, hint: '需要持续关注推进' },
  { title: '总故事点', value: `${totalStoryPoints.value} SP`, hint: '全部用户故事点合计' }
])

const recentStories = computed(() =>
  [...stories.value]
    .sort((a, b) => (a.priority || 999) - (b.priority || 999))
    .slice(0, 5)
)

const focusItems = computed(() => {
  if (storyCount.value === 0) {
    return ['先创建第一个用户故事，让需求有一个清楚的起点。']
  }

  const items = []
  if (todoCount.value > 0) {
    items.push(`还有 ${todoCount.value} 个待办故事，可以先整理产品待办优先级。`)
  }
  if (inProgressCount.value > 0) {
    items.push(`有 ${inProgressCount.value} 个故事正在进行，记得关注是否需要拆分任务。`)
  }
  if (doneCount.value > 0) {
    items.push(`已有 ${doneCount.value} 个故事完成，可以为下一轮 Sprint 做准备。`)
  }
  if (items.length < 3) {
    items.push('检查故事点是否合理，避免后续排期时猫猫迷路。')
  }
  return items
})

onMounted(async () => {
  await loadStories()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  statusChart?.dispose()
  pointChart?.dispose()
})

watch(stories, renderCharts, { deep: true })

async function loadStories() {
  try {
    const response = await fetchStoriesApi()
    stories.value = response.data || []
  } catch {
    stories.value = []
  }
}

function countByStatus(status) {
  return stories.value.filter((story) => story.status === status).length
}

function sumPoints(items) {
  return Number(items.reduce((sum, story) => sum + Number(story.storyPoint || 0), 0).toFixed(1))
}

async function renderCharts() {
  await nextTick()
  if (!storyCount.value) {
    statusChart?.dispose()
    pointChart?.dispose()
    statusChart = null
    pointChart = null
    return
  }

  if (statusChartRef.value) {
    statusChart = statusChart || echarts.init(statusChartRef.value)
    statusChart.setOption({
      color: ['#D7B69E', '#8FB8D8', '#92C6A0'],
      tooltip: { trigger: 'item' },
      legend: { bottom: 0, icon: 'circle' },
      series: [
        {
          type: 'pie',
          radius: ['48%', '68%'],
          center: ['50%', '42%'],
          data: [
            { name: '待办', value: todoCount.value },
            { name: '进行中', value: inProgressCount.value },
            { name: '已完成', value: doneCount.value }
          ],
          label: { formatter: '{b}: {c}' }
        }
      ]
    })
  }

  if (pointChartRef.value) {
    pointChart = pointChart || echarts.init(pointChartRef.value)
    pointChart.setOption({
      color: ['#B97855'],
      tooltip: { trigger: 'axis' },
      grid: { top: 28, right: 16, bottom: 32, left: 36 },
      xAxis: {
        type: 'category',
        data: ['待办', '进行中', '已完成'],
        axisTick: { show: false }
      },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        {
          type: 'bar',
          barWidth: 32,
          data: [pointByStatus.value.TODO, pointByStatus.value.IN_PROGRESS, pointByStatus.value.DONE],
          itemStyle: { borderRadius: [8, 8, 0, 0] }
        }
      ]
    })
  }
}

function resizeCharts() {
  statusChart?.resize()
  pointChart?.resize()
}

function showComingSoon(name) {
  message.info(`${name}正在整理中，猫猫马上就把它搬上来。`)
}
</script>
