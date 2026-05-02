import request from './request'

export function fetchStoryTasksApi(storyId) {
  return request.get(`/stories/${storyId}/tasks`)
}

export function createTaskApi(data) {
  return request.post('/tasks', data)
}

export function updateTaskApi(id, data) {
  return request.put(`/tasks/${id}`, data)
}

export function deleteTaskApi(id) {
  return request.delete(`/tasks/${id}`)
}

export function updateTaskStatusApi(id, status) {
  return request.put(`/tasks/${id}/status`, { status })
}
