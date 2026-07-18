import { create } from 'zustand'
import type { AuthActions, AuthState } from '../types/auth'

type AuthStore = AuthState & AuthActions

export const useAuthStore = create<AuthStore>((set) => ({
  accessToken: null,
  setAccessToken: (token) => set({ accessToken: token }),
  clearAuth: () => set({ accessToken: null }),
}))
