import { formatDateTimeForInput, subtractDays, toApiDateTime } from '../../utils/dateTime'

export const minimumInsulinAmount = 0.1

export const insulinHistoryPageSize = 8
export const insulinRangeDays = 14
export const defaultPetPageSize = 100

export const commonInsulinTypes = [
  'NPH (Neutral Protamine Hagedorn)',
  'Glargine (Lantus)',
  'Detemir (Levemir)',
  'Lente',
  'ProZinc',
  'Caninsulin',
  'Vetsulin',
  'Humulin N',
  'Novolin N',
]

export function buildInsulinRange(days: number) {
  const endDate = new Date()
  const startDate = subtractDays(endDate, days)

  return {
    startTime: toApiDateTime(formatDateTimeForInput(startDate)),
    endTime: toApiDateTime(formatDateTimeForInput(endDate)),
  }
}
