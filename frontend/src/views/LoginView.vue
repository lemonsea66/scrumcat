<template>
  <main class="auth-page">
    <section class="auth-brand">
      <p class="eyebrow">ScrumCat</p>
      <h1>欢迎回到 ScrumCat</h1>
      <p>猫猫已经准备好陪你开始新的 Sprint。</p>
    </section>
    <section class="auth-card">
      <h2>登录</h2>
      <a-form layout="vertical" :model="form" @finish="handleLogin">
        <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="form.username" placeholder="testuser" />
        </a-form-item>
        <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model:value="form.password" placeholder="请输入密码" />
        </a-form-item>
        <a-button type="primary" html-type="submit" block :loading="loading">登录</a-button>
      </a-form>
      <a-button type="link" block @click="router.push('/register')">去注册</a-button>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

async function handleLogin() {
  loading.value = true
  try {
    await authStore.login(form)
    message.success('登录成功')
    router.push(route.query.redirect || '/dashboard')
  } catch (error) {
    message.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>
