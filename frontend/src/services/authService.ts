import { apiClient } from './apiClient'
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types/auth'

const authBasePath = '/v1/auth'

export async function login(request: LoginRequest): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>(`${authBasePath}/login`, request)
  return response.data
}

export async function register(request: RegisterRequest): Promise<void> {
  await apiClient.post(`${authBasePath}/register`, request)
}

export async function loginWithGoogle(idToken: string): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>(`${authBasePath}/google`, { idToken })
  return response.data
}

export async function logout(): Promise<void> {
  await apiClient.post(`${authBasePath}/logout`)
}
