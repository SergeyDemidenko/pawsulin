function pad(value: number): string {
  return value.toString().padStart(2, '0')
}

export function formatDateTime(dateValue: string): string {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(dateValue))
}

export function formatDateTimeForInput(date: Date): string {
  return [
    date.getFullYear(),
    pad(date.getMonth() + 1),
    pad(date.getDate()),
  ].join('-') + `T${pad(date.getHours())}:${pad(date.getMinutes())}`
}

export function formatDateForChart(dateValue: string): string {
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  }).format(new Date(dateValue))
}

export function toApiDateTime(value: string): string {
  return value.length === 16 ? `${value}:00` : value
}

export function subtractDays(date: Date, days: number): Date {
  const nextDate = new Date(date)
  nextDate.setDate(nextDate.getDate() - days)
  return nextDate
}
