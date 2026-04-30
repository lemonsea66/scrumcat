import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { fetchMeApi, loginApi, registerApi } from '../api/auth'

const TOKEN_KEY = 'scrumcat_token'
const USER_KEY = 'scrumcat_user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref(JSON.parse(localStorage.getItem(USER_KEY) || 'null'))
  const isLoggedIn = computed(() => Boolean(token.value))

  async function register(payload) {
    return registerApi(payload)
  }

  async function login(payload) {
    const response = await loginApi(payload)
    token.value = response.data.token
    user.value = response.data.user
    localStorage.setItem(TOKEN_KEY, token.value)
    localStorage.setItem(USER_KEY, JSON.stringify(user.value))
    return response
  }

  async function fetchMe() {
    if (!token.value) {
      return null
    }
    const response = await fetchMeApi()
    user.value = response.data
    localStorage.setItem(USER_KEY, JSON.stringify(user.value))
    return user.value
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return {
    token,
    user,
    isLoggedIn,
    register,
    login,
    fetchMe,
    logout
  }
})
