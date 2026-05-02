import { defineStore } from 'pinia'

const SPRINT_SELECTION_KEY = 'scrumcat_current_sprints'

function loadStoredSelections() {
  try {
    return JSON.parse(localStorage.getItem(SPRINT_SELECTION_KEY) || '{}')
  } catch {
    return {}
  }
}

function persistSelections(selections) {
  localStorage.setItem(SPRINT_SELECTION_KEY, JSON.stringify(selections))
}

export const useSprintSelectionStore = defineStore('sprintSelection', {
  state: () => ({
    selectedByProject: loadStoredSelections()
  }),
  actions: {
    getSprintId(projectId) {
      return this.selectedByProject[String(projectId)]?.id || null
    },
    selectSprint(projectId, sprint) {
      if (!projectId || !sprint?.id) {
        return
      }
      this.selectedByProject[String(projectId)] = {
        id: sprint.id,
        name: sprint.name
      }
      persistSelections(this.selectedByProject)
    },
    clearSprint(projectId) {
      if (!projectId) {
        return
      }
      delete this.selectedByProject[String(projectId)]
      persistSelections(this.selectedByProject)
    }
  }
})
