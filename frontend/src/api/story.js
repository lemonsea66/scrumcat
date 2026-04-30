import request from './request'

export function fetchStoriesApi() {
  return request.get('/stories')
}

export function createStoryApi(data) {
  return request.post('/stories', data)
}

export function updateStoryApi(id, data) {
  return request.put(`/stories/${id}`, data)
}

export function deleteStoryApi(id) {
  return request.delete(`/stories/${id}`)
}

export function updateStoryStatusApi(id, status) {
  return request.put(`/stories/${id}/status`, { status })
}
