import request from './request'

export function registerApi(data) {
  return request.post('/auth/register', data)
}

export function loginApi(data) {
  return request.post('/auth/login', data)
}

export function fetchMeApi() {
  return request.get('/auth/me')
}
