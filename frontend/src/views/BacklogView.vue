<template>
  <section class="page-stack backlog-page">
    <div class="page-toolbar">
      <div>
        <p class="eyebrow">Product Backlog 产品待办事项列表</p>
        <h1>产品待办</h1>
        <p class="summary">{{ backlogSummary }}</p>
      </div>
      <a-segmented v-model:value="filter" :options="filterOptions" @change="loadBacklog" />
    </div>

    <div v-if="!projectStore.hasProject" class="soft-empty project-required">
      请先选择一个项目空间，再查看产品待办。
      <a-button type="primary" @click="router.push('/projects')">去选择项目</a-button>
    </div>

    <a-card v-else class="soft-card backlog-board">
      <div class="backlog-board-head">
        <div>
          <strong>故事优先级队列</strong>
          <span>拖动卡片调整顺序，松手后会自动保存。</span>
        </div>
        <a-button class="soft-outline-button" :loading="loading" @click="loadBacklog">刷新</a-button>
      </div>

      <div v-if="!loading && stories.length === 0" class="soft-empty">
        {{ filter === 'todo' ? '暂无待开发故事，猫猫今天的待办篮子很轻。' : '当前项目还没有用户故事。' }}
      </div>

      <Draggable
        v-else
        v-model="stories"
        item-key="id"
        handle=".drag-handle"
        class="backlog-list"
        ghost-class="drag-ghost"
        @end="handleSortEnd"
      >
        <article v-for="(story, index) in stories" :key="story.id" class="backlog-item">
          <button class="drag-handle" type="button" title="拖拽排序">⋮⋮</button>
          <div class="priority-badge">P{{ index + 1 }}</div>
          <div class="backlog-main">
            <div class="backlog-title-row">
              <strong>{{ story.title }}</strong>
              <a-tag :color="statusMeta[story.status]?.color">
                {{ statusMeta[story.status]?.label || story.status }}
              </a-tag>
            </div>
            <p>{{ story.description || '这个故事还没有描述，猫猫建议补一句用户价值。' }}</p>
            <div class="backlog-meta">
              <span>{{ story.storyPoint }} SP</span>
              <span>原优先级 {{ story.priority || '-' }}</span>
              <span v-if="story.ownerNickname">负责人 {{ story.ownerNickname }}</span>
              <span v-if="story.members?.length">协作 {{ story.members.join('、') }}</span>
              <span>{{ formatDateTime(story.updatedAt) }}</span>
            </div>
          </div>
        </article>
      </Draggable>
    </a-card>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { VueDraggableNext as Draggable } from 'vue-draggable-next'
import { fetchBacklogApi, updateBacklogPrioritiesApi } from '../api/backlog'
import { useProjectStore } from '../stores/project'

const router = useRouter()
const projectStore = useProjectStore()
const filterOptions = [
  { label: '全部', value: 'all' },
  { label: '待开发', value: 'todo' }
]

const statusMeta = {
  TODO: { label: '待开发', color: 'gold' },
  IN_PROGRESS: { label: '进行中', color: 'blue' },
  DONE: { label: '已完成', color: 'green' }
}

const stories = ref([])
const filter = ref('all')
const loading = ref(false)
const saving = ref(false)

onMounted(loadBacklog)
watch(() => projectStore.currentProjectId, loadBacklog)

const backlogSummary = computed(() => {
  if (!projectStore.hasProject) {
    return '选择项目空间后，这里展示该项目下的故事优先级队列。'
  }
  if (stories.value.length === 0) {
    return `${projectStore.currentProjectName} 当前没有符合筛选条件的故事。`
  }
  return `当前筛选下有 ${stories.value.length} 个故事，拖拽即可调整优先级。`
})

async function loadBacklog() {
  if (!projectStore.hasProject) {
    stories.value = []
    return
  }
  loading.value = true
  try {
    const response = await fetchBacklogApi(projectStore.currentProjectId, filter.value)
    stories.value = response.data || []
  } catch (error) {
    message.error(error.message || '产品待办加载失败')
  } finally {
    loading.value = false
  }
}

async function handleSortEnd() {
  if (saving.value || !projectStore.hasProject) {
    return
  }

  saving.value = true
  const items = stories.value.map((story, index) => ({
    storyId: story.id,
    priority: index + 1
  }))

  try {
    await updateBacklogPrioritiesApi(projectStore.currentProjectId, items)
    stories.value = stories.value.map((story, index) => ({ ...story, priority: index + 1 }))
    message.success('优先级已保存')
  } catch (error) {
    message.error(error.message || '优先级保存失败')
    await loadBacklog()
  } finally {
    saving.value = false
  }
}

function formatDateTime(value) {
  if (!value) {
    return '暂无更新时间'
  }
  return String(value).replace('T', ' ').slice(0, 16)
}
</script>
