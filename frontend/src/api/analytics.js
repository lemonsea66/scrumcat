import request from './request'

export function fetchAnalyticsSummaryApi(projectId, sprintId) {
  return request.get('/analytics/summary', { params: { projectId, sprintId } })
}
