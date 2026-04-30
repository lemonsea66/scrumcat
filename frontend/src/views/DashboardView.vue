<template>
  <section class="page-stack">
    <div class="page-title">
      <p class="eyebrow">Dashboard</p>
      <h1>你好，{{ authStore.user?.nickname || 'ScrumCat 用户' }}</h1>
      <p class="summary">当前已经完成认证基础，猫猫正在等你的第一个用户故事。</p>
    </div>
    <a-row :gutter="16">
      <a-col :xs="24" :md="8">
        <a-card title="当前阶段">Milestone 01：认证基础 + 用户故事管理</a-card>
      </a-col>
      <a-col :xs="24" :md="8">
        <a-card title="已启用能力">登录、注册、JWT、用户故事 CRUD</a-card>
      </a-col>
      <a-col :xs="24" :md="8">
        <a-card title="下一步">
          <a-button type="primary" @click="router.push('/stories')">管理用户故事</a-button>
        </a-card>
      </a-col>
    </a-row>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
</script>
