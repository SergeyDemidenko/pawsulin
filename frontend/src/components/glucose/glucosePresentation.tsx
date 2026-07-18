import type { GlucoseLevel } from '../../types/glucose'

export const glucoseLevelStyles: Record<GlucoseLevel, string> = {
  LOW: 'bg-amber-100 text-amber-800',
  NORMAL: 'bg-emerald-100 text-emerald-800',
  HIGH: 'bg-orange-100 text-orange-800',
  CRITICAL: 'bg-rose-100 text-rose-800',
}

export const glucoseLevelColors: Record<GlucoseLevel, string> = {
  LOW: '#f59e0b',
  NORMAL: '#10b981',
  HIGH: '#f97316',
  CRITICAL: '#f43f5e',
}

export function formatGlucoseLevel(level: GlucoseLevel): string {
  return level.charAt(0) + level.slice(1).toLowerCase()
}
