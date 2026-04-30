<template>
  <a-layout class="app-shell">
    <a-layout-sider class="app-sidebar" width="220">
      <div class="sidebar-brand">ScrumCat</div>
      <a-menu :selectedKeys="[route.path]" mode="inline" class="sidebar-menu">
        <a-menu-item key="/dashboard" @click="go('/dashboard')">Dashboard</a-menu-item>
        <a-menu-item key="/stories" @click="go('/stories')">用户故事</a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="app-header">
        <div class="stage">Milestone 01</div>
        <div class="user-area">
          <span>{{ authStore.user?.nickname || authStore.user?.username }}</span>
          <a-button size="small" @click="handleLogout">退出</a-button>
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

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

function go(path) {
  router.push(path)
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
