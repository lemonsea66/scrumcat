import request from './request'

export function fetchDashboardSummaryApi(projectId, sprintId) {
  return request.get('/dashboard/summary', { params: { projectId, sprintId } })
}
