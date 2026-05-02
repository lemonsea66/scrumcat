import request from './request'

export function fetchTaskBoardApi(sprintId) {
  return request.get(`/task-board/${sprintId}`)
}
