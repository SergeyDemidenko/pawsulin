import type { PageResponse } from './pet'

export type GlucoseLevel = 'LOW' | 'NORMAL' | 'HIGH' | 'CRITICAL'

export interface GlucoseReading {
  id: number
  petId: number
  userId: number
  glucoseValue: number
  glucoseLevel: GlucoseLevel
  readingTime: string
  notes: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateGlucoseReadingRequest {
  glucoseValue: number
  readingTime: string
  notes?: string
}

export interface GlucoseHistoryQueryParams {
  page: number
  size: number
  sortBy?: string
  direction?: 'ASC' | 'DESC'
}

export interface GlucoseRangeQueryParams {
  startTime: string
  endTime: string
}

export interface GlucoseAnalytics {
  averageGlucose: number | null
  minGlucose: number | null
  maxGlucose: number | null
  readingsCount: number | null
  lowReadingsCount: number | null
  normalReadingsCount: number | null
  highReadingsCount: number | null
  criticalReadingsCount: number | null
  lowPercentage: number | null
  normalPercentage: number | null
  highPercentage: number | null
  criticalPercentage: number | null
}

export type GlucoseHistoryPage = PageResponse<GlucoseReading>
