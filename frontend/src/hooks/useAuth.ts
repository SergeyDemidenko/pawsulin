import { useAuthStore } from '../store/authStore'
import { isJwtExpired } from '../utils/jwt'

export function useAuth() {
  const accessToken = useAuthStore((state) => state.accessToken)
  const refreshToken = useAuthStore((state) => state.refreshToken)
  const tokenType = useAuthStore((state) => state.tokenType)
  const userId = useAuthStore((state) => state.userId)
  const email = useAuthStore((state) => state.email)
  const role = useAuthStore((state) => state.role)
  const setSession = useAuthStore((state) => state.setSession)
  const clearAuth = useAuthStore((state) => state.clearAuth)
  const isAuthenticated = Boolean(accessToken) && !isJwtExpired(accessToken)

  return {
    accessToken,
    refreshToken,
    tokenType,
    userId,
    email,
    role,
    setSession,
    clearAuth,
    isAuthenticated,
  }
}
