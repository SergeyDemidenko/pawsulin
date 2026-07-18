import type { PageResponse } from './pet'

export type AlertType = 'LOW_GLUCOSE' | 'HIGH_GLUCOSE' | 'CRITICAL_GLUCOSE' | 'MISSED_READING'

export interface GlucoseAlert {
  id: number
  petId: number
  userId: number
  alertType: AlertType
  lowThreshold: number | null
  highThreshold: number | null
  isEnabled: boolean
  description: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateGlucoseAlertRequest {
  alertType: AlertType
  lowThreshold?: number
  highThreshold?: number
  isEnabled?: boolean
  description?: string
}

export interface UpdateGlucoseAlertRequest {
  alertType?: AlertType
  lowThreshold?: number
  highThreshold?: number
  isEnabled?: boolean
  description?: string
}

export interface AlertsQueryParams {
  page: number
  size: number
  sortBy?: string
  direction?: 'ASC' | 'DESC'
}

export type GlucoseAlertPage = PageResponse<GlucoseAlert>

export interface NotificationMessage {
  type: string
  id: string
  petId: number
  petName: string
  alertType: AlertType
  glucoseValue: number
  message: string
  triggeredAt: string
}
