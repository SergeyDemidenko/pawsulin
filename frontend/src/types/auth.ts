export interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  tokenType: string
  userId: number | null
  email: string | null
  role: string | null
}

export interface AuthActions {
  setSession: (session: AuthSession) => void
  clearAuth: () => void
}

export interface AuthSession {
  accessToken: string
  refreshToken: string
  tokenType: string
  userId: number
  email: string
  role: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  firstName: string
  lastName: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  userId: number
  email: string
  role: string
}
