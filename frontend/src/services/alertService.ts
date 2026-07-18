import { apiClient } from './apiClient'
import type {
  AlertsQueryParams,
  CreateGlucoseAlertRequest,
  GlucoseAlert,
  GlucoseAlertPage,
  UpdateGlucoseAlertRequest,
} from '../types/alert'

function alertsBasePath(petId: number): string {
  return `/v1/pets/${petId}/alerts`
}

export async function getAlertsByPet(petId: number, params: AlertsQueryParams): Promise<GlucoseAlertPage> {
  const response = await apiClient.get<GlucoseAlertPage>(alertsBasePath(petId), { params })
  return response.data
}

export async function getAlertById(petId: number, alertId: number): Promise<GlucoseAlert> {
  const response = await apiClient.get<GlucoseAlert>(`${alertsBasePath(petId)}/${alertId}`)
  return response.data
}

export async function createAlert(petId: number, request: CreateGlucoseAlertRequest): Promise<GlucoseAlert> {
  const response = await apiClient.post<GlucoseAlert>(alertsBasePath(petId), request)
  return response.data
}

export async function updateAlert(
  petId: number,
  alertId: number,
  request: UpdateGlucoseAlertRequest,
): Promise<GlucoseAlert> {
  const response = await apiClient.put<GlucoseAlert>(`${alertsBasePath(petId)}/${alertId}`, request)
  return response.data
}

export async function deleteAlert(petId: number, alertId: number): Promise<void> {
  await apiClient.delete(`${alertsBasePath(petId)}/${alertId}`)
}
