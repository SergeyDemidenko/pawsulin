import { apiClient } from './apiClient'
import type {
  CreateInsulinLogRequest,
  InsulinHistoryPage,
  InsulinHistoryQueryParams,
  InsulinLog,
  InsulinRangeQueryParams,
} from '../types/insulin'

function insulinBasePath(petId: number): string {
  return `/v1/pets/${petId}/insulin`
}

export async function createInsulinLog(
  petId: number,
  request: CreateInsulinLogRequest,
): Promise<InsulinLog> {
  const response = await apiClient.post<InsulinLog>(insulinBasePath(petId), request)
  return response.data
}

export async function getInsulinLogs(
  petId: number,
  params: InsulinHistoryQueryParams,
): Promise<InsulinHistoryPage> {
  const response = await apiClient.get<InsulinHistoryPage>(insulinBasePath(petId), { params })
  return response.data
}

export async function getInsulinLogsByRange(
  petId: number,
  params: InsulinRangeQueryParams,
): Promise<InsulinLog[]> {
  const response = await apiClient.get<InsulinLog[]>(`${insulinBasePath(petId)}/range`, { params })
  return response.data
}
