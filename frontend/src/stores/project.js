import { defineStore } from 'pinia'

const PROJECT_KEY = 'scrumcat_current_project'

function loadStoredProject() {
  try {
    return JSON.parse(localStorage.getItem(PROJECT_KEY) || 'null')
  } catch {
    return null
  }
}

export const useProjectStore = defineStore('project', {
  state: () => {
    const storedProject = loadStoredProject()
    return {
      currentProjectId: storedProject?.id || null,
      currentProjectName: storedProject?.name || ''
    }
  },
  getters: {
    hasProject: (state) => Boolean(state.currentProjectId)
  },
  actions: {
    selectProject(project) {
      this.currentProjectId = project?.id || null
      this.currentProjectName = project?.name || ''
      if (project?.id) {
        localStorage.setItem(PROJECT_KEY, JSON.stringify({ id: project.id, name: project.name }))
      } else {
        localStorage.removeItem(PROJECT_KEY)
      }
    },
    clearProject() {
      this.selectProject(null)
    }
  }
})
