import request from './request'

export function fetchBurndownApi(sprintId) {
  return request.get(`/burndown/${sprintId}`)
}
