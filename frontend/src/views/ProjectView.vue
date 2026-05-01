<template>
  <section class="page-stack project-page">
    <div class="page-toolbar">
      <div>
        <p class="eyebrow">Project Workspace</p>
        <h1>项目空间</h1>
        <p class="summary">先选择一个产品项目，再管理故事、待办和 Sprint。</p>
      </div>
      <a-button type="primary" @click="openCreateModal">新增项目</a-button>
    </div>

    <div v-if="!loading && projects.length === 0" class="soft-empty project-empty">
      还没有项目空间，创建一个“美食地图图鉴 App”这样的轻量项目吧。
    </div>

    <div v-else class="project-grid">
      <a-card
        v-for="project in projects"
        :key="project.id"
        class="soft-card project-card"
        :class="{ selected: projectStore.currentProjectId === project.id }"
      >
        <div class="project-card-head">
          <div>
            <p class="eyebrow">{{ projectStore.currentProjectId === project.id ? '当前项目' : '项目空间' }}</p>
            <h2>{{ project.name }}</h2>
          </div>
          <a-tag v-if="project.ownerNickname" color="orange">{{ project.ownerNickname }}</a-tag>
        </div>
        <p class="project-description">{{ project.description || '这个项目还没有简介。' }}</p>
        <div class="member-row">
          <a-tag v-for="member in project.members" :key="member">{{ member }}</a-tag>
          <span v-if="!project.members?.length">暂无团队成员昵称</span>
        </div>
        <div class="project-actions">
          <a-button type="primary" size="small" @click="selectProject(project)">选择</a-button>
          <a-button size="small" class="soft-outline-button" @click="openEditModal(project)">编辑</a-button>
          <a-popconfirm title="如果项目下已有故事或 Sprint，将无法删除。" @confirm="handleDelete(project)">
            <a-button size="small" danger>删除</a-button>
          </a-popconfirm>
        </div>
      </a-card>
    </div>

    <a-modal
      v-model:open="modalOpen"
      :title="editingProject ? '编辑项目空间' : '新增项目空间'"
      @ok="handleSubmit"
      @cancel="closeModal"
    >
      <a-form layout="vertical" :model="form">
        <a-form-item label="项目名称" required>
          <a-input v-model:value="form.name" placeholder="美食地图图鉴 App" />
        </a-form-item>
        <a-form-item label="项目简介">
          <a-textarea v-model:value="form.description" :rows="3" placeholder="帮助用户记录和发现附近美食" />
        </a-form-item>
        <a-form-item label="项目负责人昵称">
          <a-input v-model:value="form.ownerNickname" placeholder="柠檬" />
        </a-form-item>
        <a-form-item label="团队成员昵称">
          <a-select
            v-model:value="form.members"
            mode="tags"
            placeholder="输入昵称后回车，例如 小张、小王"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { createProjectApi, deleteProjectApi, fetchProjectsApi, updateProjectApi } from '../api/project'
import { useProjectStore } from '../stores/project'

const projectStore = useProjectStore()
const projects = ref([])
const loading = ref(false)
const modalOpen = ref(false)
const editingProject = ref(null)
const form = reactive({
  name: '',
  description: '',
  ownerNickname: '',
  members: []
})

onMounted(loadProjects)

async function loadProjects() {
  loading.value = true
  try {
    const response = await fetchProjectsApi()
    projects.value = response.data || []
    if (projectStore.currentProjectId && !projects.value.some((project) => project.id === projectStore.currentProjectId)) {
      projectStore.clearProject()
    }
  } catch (error) {
    message.error(error.message || '项目空间加载失败')
  } finally {
    loading.value = false
  }
}

function openCreateModal() {
  editingProject.value = null
  Object.assign(form, { name: '', description: '', ownerNickname: '', members: [] })
  modalOpen.value = true
}

function openEditModal(project) {
  editingProject.value = project
  Object.assign(form, {
    name: project.name,
    description: project.description,
    ownerNickname: project.ownerNickname,
    members: [...(project.members || [])]
  })
  modalOpen.value = true
}

function closeModal() {
  modalOpen.value = false
}

function selectProject(project) {
  projectStore.selectProject(project)
  message.success(`已选择项目：${project.name}`)
}

async function handleSubmit() {
  if (!form.name) {
    message.error('请填写项目名称')
    return
  }

  const payload = {
    name: form.name,
    description: form.description,
    ownerNickname: form.ownerNickname,
    members: form.members || []
  }

  try {
    if (editingProject.value) {
      const response = await updateProjectApi(editingProject.value.id, payload)
      if (projectStore.currentProjectId === editingProject.value.id) {
        projectStore.selectProject(response.data)
      }
      message.success('项目空间已更新')
    } else {
      const response = await createProjectApi(payload)
      projectStore.selectProject(response.data)
      message.success('项目空间已创建并选中')
    }
    closeModal()
    await loadProjects()
  } catch (error) {
    message.error(error.message || '项目空间保存失败')
  }
}

async function handleDelete(project) {
  try {
    await deleteProjectApi(project.id)
    if (projectStore.currentProjectId === project.id) {
      projectStore.clearProject()
    }
    message.success('项目空间已删除')
    await loadProjects()
  } catch (error) {
    message.error(error.message || '项目空间删除失败')
  }
}
</script>
