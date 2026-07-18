import { useAuthStore } from '../store/authStore'

export function useAuth() {
  const accessToken = useAuthStore((state) => state.accessToken)
  const setAccessToken = useAuthStore((state) => state.setAccessToken)
  const clearAuth = useAuthStore((state) => state.clearAuth)

  return {
    accessToken,
    setAccessToken,
    clearAuth,
    isAuthenticated: Boolean(accessToken),
  }
}
