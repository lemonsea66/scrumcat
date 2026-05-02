<template>
  <a-drawer
    :open="open"
    width="560"
    :title="story?.title || '故事详情'"
    @close="$emit('close')"
  >
    <template v-if="story">
      <div class="story-detail-block">
        <p>{{ story.description || '这个故事还没有描述，猫猫建议补一句用户价值。' }}</p>
        <div class="story-detail-meta">
          <a-tag color="gold">{{ story.storyPoint }} SP</a-tag>
          <a-tag>P{{ story.priority || '-' }}</a-tag>
          <a-tag v-if="story.ownerNickname" color="purple">负责人 {{ story.ownerNickname }}</a-tag>
          <a-tag v-if="story.members?.length" color="blue">协作 {{ story.members.join('、') }}</a-tag>
        </div>
      </div>

      <div class="drawer-section-head">
        <div>
          <strong>任务拆解</strong>
          <span>把大故事拆成可以推进的小任务。</span>
        </div>
        <a-button type="primary" size="small" @click="openCreateTask">新增任务</a-button>
      </div>

      <div v-if="loading" class="soft-empty task-drawer-empty">任务加载中...</div>

      <div v-else-if="tasks.length === 0" class="soft-empty task-drawer-empty">
        <CatImage
          name="cat-empty-task.png"
          alt="暂无任务"
          fallback="还没有拆解任务，猫猫在等你把大故事拆成小鱼干。"
          variant="small"
        />
      </div>

      <div v-else class="drawer-task-list">
        <article v-for="task in tasks" :key="task.id" class="drawer-task-item">
          <div>
            <strong>{{ task.title }}</strong>
            <p>{{ task.description || '暂无描述' }}</p>
            <div class="task-card-meta">
              <span>{{ task.estimatedHours }}h</span>
              <a-select
                :value="task.status"
                size="small"
                class="task-status-select"
                @change="(value) => handleTaskStatus(task, value)"
              >
                <a-select-option value="TODO">未开始</a-select-option>
                <a-select-option value="IN_PROGRESS">进行中</a-select-option>
                <a-select-option value="DONE">已完成</a-select-option>
              </a-select>
            </div>
          </div>
          <a-space>
            <a-button size="small" class="soft-outline-button" @click="openEditTask(task)">编辑</a-button>
            <a-popconfirm title="确认删除这个任务？" @confirm="handleDeleteTask(task.id)">
              <a-button size="small" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </article>
      </div>
    </template>

    <a-modal
      v-model:open="taskModalOpen"
      :title="editingTask ? '编辑任务' : '新增任务'"
      @ok="handleSubmitTask"
      @cancel="closeTaskModal"
    >
      <a-form layout="vertical" :model="taskForm">
        <a-form-item label="任务标题" required>
          <a-input v-model:value="taskForm.title" placeholder="登录接口开发" />
        </a-form-item>
        <a-form-item label="任务描述">
          <a-textarea v-model:value="taskForm.description" :rows="3" placeholder="说明任务内容和完成标准" />
        </a-form-item>
        <a-form-item label="预计工时" required>
          <a-input-number v-model:value="taskForm.estimatedHours" :min="1" :precision="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="taskForm.status">
            <a-select-option value="TODO">未开始</a-select-option>
            <a-select-option value="IN_PROGRESS">进行中</a-select-option>
            <a-select-option value="DONE">已完成</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </a-drawer>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import CatImage from './CatImage.vue'
import {
  createTaskApi,
  deleteTaskApi,
  fetchStoryTasksApi,
  updateTaskApi,
  updateTaskStatusApi
} from '../api/task'

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  story: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'changed'])

const tasks = ref([])
const loading = ref(false)
const taskModalOpen = ref(false)
const editingTask = ref(null)
const taskForm = reactive({
  title: '',
  description: '',
  estimatedHours: 1,
  status: 'TODO'
})

watch(
  () => [props.open, props.story?.id],
  () => {
    if (props.open && props.story?.id) {
      loadTasks()
    }
  }
)

async function loadTasks() {
  loading.value = true
  try {
    const response = await fetchStoryTasksApi(props.story.id)
    tasks.value = response.data || []
  } catch (error) {
    message.error(error.message || '任务加载失败')
  } finally {
    loading.value = false
  }
}

function openCreateTask() {
  editingTask.value = null
  Object.assign(taskForm, { title: '', description: '', estimatedHours: 1, status: 'TODO' })
  taskModalOpen.value = true
}

function openEditTask(task) {
  editingTask.value = task
  Object.assign(taskForm, {
    title: task.title,
    description: task.description,
    estimatedHours: task.estimatedHours,
    status: task.status
  })
  taskModalOpen.value = true
}

function closeTaskModal() {
  taskModalOpen.value = false
}

async function handleSubmitTask() {
  if (!taskForm.title || !taskForm.estimatedHours) {
    message.error('请填写任务标题和预计工时')
    return
  }

  const payload = {
    storyId: props.story.id,
    title: taskForm.title,
    description: taskForm.description,
    estimatedHours: Number(taskForm.estimatedHours),
    status: taskForm.status
  }

  try {
    if (editingTask.value) {
      await updateTaskApi(editingTask.value.id, payload)
      message.success('任务已更新')
    } else {
      await createTaskApi(payload)
      message.success('任务已新增')
    }
    closeTaskModal()
    await loadTasks()
    emit('changed')
  } catch (error) {
    message.error(error.message || '任务保存失败')
  }
}

async function handleTaskStatus(task, status) {
  try {
    await updateTaskStatusApi(task.id, status)
    message.success(status === 'DONE' ? '这个任务完成啦，猫猫获得一条小鱼干。' : '任务状态已更新')
    await loadTasks()
    emit('changed')
  } catch (error) {
    message.error(error.message || '任务状态更新失败')
  }
}

async function handleDeleteTask(id) {
  try {
    await deleteTaskApi(id)
    message.success('任务已删除')
    await loadTasks()
    emit('changed')
  } catch (error) {
    message.error(error.message || '任务删除失败')
  }
}

defineExpose({
  loadTasks
})
</script>
