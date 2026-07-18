import { apiClient } from './apiClient'
import type {
  CreateGlucoseReadingRequest,
  GlucoseAnalytics,
  GlucoseHistoryPage,
  GlucoseHistoryQueryParams,
  GlucoseRangeQueryParams,
  GlucoseReading,
} from '../types/glucose'

function glucoseBasePath(petId: number): string {
  return `/v1/pets/${petId}/glucose`
}

export async function createGlucoseReading(
  petId: number,
  request: CreateGlucoseReadingRequest,
): Promise<GlucoseReading> {
  const response = await apiClient.post<GlucoseReading>(glucoseBasePath(petId), request)
  return response.data
}

export async function getGlucoseReadings(
  petId: number,
  params: GlucoseHistoryQueryParams,
): Promise<GlucoseHistoryPage> {
  const response = await apiClient.get<GlucoseHistoryPage>(glucoseBasePath(petId), { params })
  return response.data
}

export async function getGlucoseReadingsByRange(
  petId: number,
  params: GlucoseRangeQueryParams,
): Promise<GlucoseReading[]> {
  const response = await apiClient.get<GlucoseReading[]>(`${glucoseBasePath(petId)}/range`, { params })
  return response.data
}

export async function getGlucoseAnalytics(
  petId: number,
  params: GlucoseRangeQueryParams,
): Promise<GlucoseAnalytics> {
  const response = await apiClient.get<GlucoseAnalytics>(`${glucoseBasePath(petId)}/analytics`, { params })
  return response.data
}
