import request from './request'

export function fetchSprintsApi(projectId) {
  return request.get('/sprints', { params: { projectId } })
}

export function createSprintApi(data) {
  return request.post('/sprints', data)
}

export function fetchSprintApi(id) {
  return request.get(`/sprints/${id}`)
}

export function updateSprintApi(id, data) {
  return request.put(`/sprints/${id}`, data)
}

export function deleteSprintApi(id) {
  return request.delete(`/sprints/${id}`)
}

export function addSprintStoryApi(sprintId, storyId) {
  return request.post(`/sprints/${sprintId}/stories`, { storyId })
}

export function fetchSprintStoriesApi(sprintId) {
  return request.get(`/sprints/${sprintId}/stories`)
}

export function removeSprintStoryApi(sprintId, storyId) {
  return request.delete(`/sprints/${sprintId}/stories/${storyId}`)
}
