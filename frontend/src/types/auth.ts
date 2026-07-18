export interface AuthState {
  accessToken: string | null
}

export interface AuthActions {
  setAccessToken: (token: string | null) => void
  clearAuth: () => void
}
