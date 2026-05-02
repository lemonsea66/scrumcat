<template>
  <a-layout class="app-shell">
    <a-layout-sider class="app-sidebar" width="220">
      <div class="sidebar-brand">ScrumCat</div>
      <a-menu :selectedKeys="[route.path]" mode="inline" class="sidebar-menu">
        <a-menu-item key="/dashboard" @click="go('/dashboard')">工作台</a-menu-item>
        <a-menu-item key="/projects" @click="go('/projects')">项目空间</a-menu-item>
        <a-menu-item key="/stories" @click="go('/stories')">用户故事</a-menu-item>
        <a-menu-item key="/backlog" @click="go('/backlog')">产品待办</a-menu-item>
        <a-menu-item key="/sprints" @click="go('/sprints')">迭代计划</a-menu-item>
        <a-menu-item key="/sprint-board" @click="go('/sprint-board')">故事看板</a-menu-item>
        <a-menu-item key="/task-board" @click="go('/task-board')">任务看板</a-menu-item>
        <a-menu-item key="/analytics" @click="go('/analytics')">统计分析</a-menu-item>
        <a-menu-item key="/review" disabled>复盘中心</a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="app-header">
        <div class="stage">
          <span class="paw-mark" aria-hidden="true"></span>
          <span>{{ projectStore.currentProjectName || '先选择一个项目空间' }}</span>
        </div>
        <div class="user-area">
          <span>{{ authStore.user?.nickname || authStore.user?.username }}</span>
          <a-button class="soft-outline-button" size="small" @click="handleLogout">退出</a-button>
        </div>
      </a-layout-header>
      <a-layout-content class="app-content">
        <RouterView />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useProjectStore } from '../stores/project'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const projectStore = useProjectStore()

function go(path) {
  router.push(path)
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
