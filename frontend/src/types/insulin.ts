import type { PageResponse } from './pet'

export interface InsulinLog {
  id: number
  petId: number
  userId: number
  insulinType: string
  amountUnits: number
  injectionTime: string
  batchNumber: string | null
  expirationDate: string | null
  notes: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateInsulinLogRequest {
  insulinType: string
  amountUnits: number
  injectionTime: string
  batchNumber?: string
  expirationDate?: string
  notes?: string
}

export interface InsulinHistoryQueryParams {
  page: number
  size: number
  sortBy?: string
  direction?: 'ASC' | 'DESC'
}

export interface InsulinRangeQueryParams {
  startTime: string
  endTime: string
}

export type InsulinHistoryPage = PageResponse<InsulinLog>
