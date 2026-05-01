import request from './request'

export function fetchBacklogApi(projectId, filter = 'all') {
  return request.get('/backlog', { params: { projectId, filter } })
}

export function updateBacklogPrioritiesApi(projectId, items) {
  return request.put('/backlog/priorities', { projectId, items })
}
