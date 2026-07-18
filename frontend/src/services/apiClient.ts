import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import { isJwtExpired } from '../utils/jwt'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken

  if (token && !isJwtExpired(token)) {
    config.headers.Authorization = ['Bearer', token].join(' ')
  } else if (token) {
    useAuthStore.getState().clearAuth()
  }

  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().clearAuth()
    }

    return Promise.reject(error)
  },
)

export { apiClient }
