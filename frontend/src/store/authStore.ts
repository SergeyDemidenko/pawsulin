import { create } from 'zustand'
import { createJSONStorage, persist } from 'zustand/middleware'
import type { AuthActions, AuthState } from '../types/auth'

type AuthStore = AuthState & AuthActions

const initialState: AuthState = {
  accessToken: null,
  refreshToken: null,
  tokenType: 'Bearer',
  userId: null,
  email: null,
  role: null,
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      ...initialState,
      setSession: (session) =>
        set({
          accessToken: session.accessToken,
          refreshToken: session.refreshToken,
          tokenType: session.tokenType,
          userId: session.userId,
          email: session.email,
          role: session.role,
        }),
      clearAuth: () => set(initialState),
    }),
    {
      name: 'pawsulin-auth',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        tokenType: state.tokenType,
        userId: state.userId,
        email: state.email,
        role: state.role,
      }),
    },
  ),
)
