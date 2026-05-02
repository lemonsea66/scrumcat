import request from './request'

export function fetchSprintBoardApi(sprintId) {
  return request.get(`/sprint-board/${sprintId}`)
}

export function updateSprintBoardStoryStatusApi(sprintId, storyId, status) {
  return request.put(`/sprint-board/${sprintId}/stories/${storyId}/status`, { status })
}
