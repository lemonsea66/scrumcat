import { createRouter, createWebHistory } from 'vue-router'
import BasicLayout from '../layouts/BasicLayout.vue'
import DashboardView from '../views/DashboardView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import StoryView from '../views/StoryView.vue'
import BacklogView from '../views/BacklogView.vue'
import SprintView from '../views/SprintView.vue'
import ProjectView from '../views/ProjectView.vue'
import SprintBoardView from '../views/SprintBoardView.vue'
import TaskBoardView from '../views/TaskBoardView.vue'
import AnalyticsView from '../views/AnalyticsView.vue'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { public: true }
  },
  {
    path: '/',
    component: BasicLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: DashboardView,
        meta: { requiresAuth: true }
      },
      {
        path: 'stories',
        name: 'stories',
        component: StoryView,
        meta: { requiresAuth: true }
      },
      {
        path: 'projects',
        name: 'projects',
        component: ProjectView,
        meta: { requiresAuth: true }
      },
      {
        path: 'backlog',
        name: 'backlog',
        component: BacklogView,
        meta: { requiresAuth: true }
      },
      {
        path: 'sprints',
        name: 'sprints',
        component: SprintView,
        meta: { requiresAuth: true }
      },
      {
        path: 'sprint-board',
        name: 'sprint-board',
        component: SprintBoardView,
        meta: { requiresAuth: true }
      },
      {
        path: 'task-board',
        name: 'task-board',
        component: TaskBoardView,
        meta: { requiresAuth: true }
      },
      {
        path: 'analytics',
        name: 'analytics',
        component: AnalyticsView,
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (to.meta.public && authStore.isLoggedIn && to.name === 'login') {
    return { name: 'dashboard' }
  }

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.meta.requiresAuth && authStore.isLoggedIn && !authStore.user) {
    try {
      await authStore.fetchMe()
    } catch {
      return { name: 'login' }
    }
  }

  return true
})

export default router
