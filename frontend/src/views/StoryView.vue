<template>
  <section class="page-stack">
    <div class="page-toolbar">
      <div>
        <p class="eyebrow">User Stories</p>
        <h1>用户故事管理</h1>
        <p class="summary">{{ storySummary }}</p>
      </div>
      <a-button type="primary" @click="openCreateModal">新增用户故事</a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="stories"
      :loading="loading"
      :pagination="{ pageSize: 8 }"
      row-key="id"
    >
      <template #emptyText>
        <div class="story-empty-state">还没有用户故事，猫猫在等你的第一个需求。</div>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'storyPoint'">
          <a-tag color="blue">{{ record.storyPoint }}</a-tag>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-select
            :value="record.status"
            size="small"
            style="width: 128px"
            @change="(value) => handleStatusChange(record, value)"
          >
            <a-select-option value="TODO">未开始</a-select-option>
            <a-select-option value="IN_PROGRESS">进行中</a-select-option>
            <a-select-option value="DONE">已完成</a-select-option>
          </a-select>
        </template>
        <template v-else-if="column.key === 'actions'">
          <a-space>
            <a-button size="small" @click="openEditModal(record)">编辑</a-button>
            <a-popconfirm title="确认删除这个用户故事？" @confirm="handleDelete(record.id)">
              <a-button size="small" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="modalOpen"
      :title="editingStory ? '编辑用户故事' : '新增用户故事'"
      @ok="handleSubmit"
      @cancel="closeModal"
    >
      <a-form layout="vertical" :model="form">
        <a-form-item label="标题" required>
          <a-input v-model:value="form.title" placeholder="创建用户故事" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea
            v-model:value="form.description"
            :rows="4"
            placeholder="作为……我想要……以便……"
          />
        </a-form-item>
        <a-form-item label="故事点" required>
          <a-select v-model:value="form.storyPoint">
            <a-select-option v-for="point in storyPointOptions" :key="point" :value="point">
              {{ point }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="优先级" required>
          <a-input-number v-model:value="form.priority" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="form.status">
            <a-select-option value="TODO">未开始</a-select-option>
            <a-select-option value="IN_PROGRESS">进行中</a-select-option>
            <a-select-option value="DONE">已完成</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  createStoryApi,
  deleteStoryApi,
  fetchStoriesApi,
  updateStoryApi,
  updateStoryStatusApi
} from '../api/story'

const storyPointOptions = [0.5, 1, 2, 3, 5, 8, 20, 40]
const columns = [
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '故事点', dataIndex: 'storyPoint', key: 'storyPoint', width: 110 },
  { title: '优先级', dataIndex: 'priority', key: 'priority', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 150 },
  { title: '操作', key: 'actions', width: 160 }
]

const stories = ref([])
const loading = ref(false)
const modalOpen = ref(false)
const editingStory = ref(null)
const form = reactive({
  title: '',
  description: '',
  storyPoint: 1,
  priority: 1,
  status: 'TODO'
})

onMounted(loadStories)

const storySummary = computed(() => {
  if (stories.value.length === 0) {
    return '还没有用户故事，猫猫在等你的第一个需求。'
  }
  return `已整理 ${stories.value.length} 个用户故事，可以继续补充、编辑和调整状态。`
})

async function loadStories() {
  loading.value = true
  try {
    const response = await fetchStoriesApi()
    stories.value = response.data || []
  } catch (error) {
    message.error(error.message || '用户故事加载失败')
  } finally {
    loading.value = false
  }
}

function openCreateModal() {
  editingStory.value = null
  Object.assign(form, {
    title: '',
    description: '',
    storyPoint: 1,
    priority: stories.value.length + 1,
    status: 'TODO'
  })
  modalOpen.value = true
}

function openEditModal(story) {
  editingStory.value = story
  Object.assign(form, {
    title: story.title,
    description: story.description,
    storyPoint: Number(story.storyPoint),
    priority: story.priority,
    status: story.status
  })
  modalOpen.value = true
}

function closeModal() {
  modalOpen.value = false
}

async function handleSubmit() {
  if (!form.title || !form.storyPoint || !form.priority) {
    message.error('请填写标题、故事点和优先级')
    return
  }

  const payload = {
    title: form.title,
    description: form.description,
    storyPoint: Number(form.storyPoint),
    priority: Number(form.priority),
    status: form.status
  }

  try {
    if (editingStory.value) {
      await updateStoryApi(editingStory.value.id, payload)
      message.success('用户故事已更新')
    } else {
      await createStoryApi(payload)
      message.success('用户故事已创建')
    }
    closeModal()
    await loadStories()
  } catch (error) {
    message.error(error.message || '保存失败')
  }
}

async function handleStatusChange(story, status) {
  try {
    await updateStoryStatusApi(story.id, status)
    message.success('状态已更新')
    await loadStories()
  } catch (error) {
    message.error(error.message || '状态更新失败')
  }
}

async function handleDelete(id) {
  try {
    await deleteStoryApi(id)
    message.success('用户故事已删除')
    await loadStories()
  } catch (error) {
    message.error(error.message || '删除失败')
  }
}
</script>
