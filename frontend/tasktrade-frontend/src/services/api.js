import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('tasktrade_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('tasktrade_token')
      localStorage.removeItem('tasktrade_user')
      window.dispatchEvent(new Event('tasktrade:logout'))
    }
    return Promise.reject(error)
  },
)

export const authApi = {
  login: (payload) => api.post('/auth/login', payload),
  register: (payload) => api.post('/auth/register', payload),
}

export const taskApi = {
  list: () => api.get('/tasks'),
  get: (id) => api.get(`/tasks/${id}`),
  contact: (id) => api.get(`/tasks/${id}/contact`),
  create: (payload) => api.post('/tasks', payload),
  update: (id, payload) => api.put(`/tasks/${id}`, payload),
  remove: (id) => api.delete(`/tasks/${id}`),
  complete: (id) => api.post(`/tasks/${id}/complete`),
}

export const applicationApi = {
  apply: (taskId, payload) => api.post(`/applications/task/${taskId}`, payload),
  forTask: (taskId) => api.get(`/applications/task/${taskId}`),
  mine: () => api.get('/applications/my'),
  accept: (applicationId) => api.put(`/applications/${applicationId}/accept`),
}

export const userApi = {
  me: () => api.get('/users/me'),
  update: (payload) => api.put('/users/me', payload),
}

export const reviewApi = {
  create: (payload) => api.post('/reviews', payload),
  forUser: (userId) => api.get(`/reviews/user/${userId}`),
  givenByUser: (userId) => api.get(`/reviews/given/${userId}`),
}

export const skillApi = {
  list: () => api.get('/skills'),
  add: (skillId) => api.post(`/skills/me/${skillId}`),
  remove: (skillId) => api.delete(`/skills/me/${skillId}`),
}

export function getErrorMessage(error, fallback = 'Something went wrong. Please try again.') {
  const data = error.response?.data
  if (typeof data === 'string') return data
  if (data?.message) return data.message
  if (data?.error) return data.error
  if (data?.errors) return Object.values(data.errors).join(' ')
  return fallback
}

export default api
