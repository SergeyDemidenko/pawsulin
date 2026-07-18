import { formatDateTimeForInput, subtractDays, toApiDateTime } from '../../utils/dateTime'

export const minimumGlucoseValue = 20

export const targetGlucoseRange = {
  min: 70,
  max: 180,
} as const

export const glucoseRangeOptions = [7, 14, 30] as const

export const defaultPetPageSize = 100
export const dashboardHistoryPageSize = 5
export const glucoseHistoryPageSize = 8

export function buildGlucoseRange(days: number) {
  const endDate = new Date()
  const startDate = subtractDays(endDate, days)

  return {
    startTime: toApiDateTime(formatDateTimeForInput(startDate)),
    endTime: toApiDateTime(formatDateTimeForInput(endDate)),
  }
}
