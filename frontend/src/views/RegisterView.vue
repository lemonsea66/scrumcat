<template>
  <main class="auth-page">
    <section class="auth-brand">
      <p class="eyebrow">ScrumCat</p>
      <h1>创建你的 ScrumCat 账号</h1>
      <p>让猫猫陪你整理第一个 Sprint。</p>
    </section>
    <section class="auth-card">
      <h2>注册</h2>
      <a-form layout="vertical" :model="form" @finish="handleRegister">
        <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="form.username" />
        </a-form-item>
        <a-form-item label="昵称" name="nickname" :rules="[{ required: true, message: '请输入昵称' }]">
          <a-input v-model:value="form.nickname" />
        </a-form-item>
        <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model:value="form.password" />
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword" :rules="[{ required: true, message: '请再次输入密码' }]">
          <a-input-password v-model:value="form.confirmPassword" />
        </a-form-item>
        <a-button type="primary" html-type="submit" block :loading="loading">注册</a-button>
      </a-form>
      <a-button type="link" block @click="router.push('/login')">已有账号，去登录</a-button>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

async function handleRegister() {
  if (form.password !== form.confirmPassword) {
    message.error('两次密码不一致')
    return
  }

  loading.value = true
  try {
    await authStore.register({
      username: form.username,
      nickname: form.nickname,
      password: form.password
    })
    message.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    message.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>
