import type { GlucoseAnalytics, GlucoseLevel } from '../../types/glucose'
import { formatGlucoseLevel, glucoseLevelStyles } from './glucosePresentation'

interface GlucoseAnalyticsCardsProps {
  analytics: GlucoseAnalytics | undefined
  isLoading: boolean
}

interface SummaryCard {
  label: string
  value: string
  helper: string
}

function formatValue(value: number | null | undefined, suffix = 'mg/dL'): string {
  return value === null || value === undefined ? '—' : `${value} ${suffix}`
}

function formatCount(value: number | null | undefined): string {
  return value === null || value === undefined ? '—' : value.toString()
}

export function GlucoseAnalyticsCards({ analytics, isLoading }: GlucoseAnalyticsCardsProps) {
  if (isLoading) {
    return <p className="text-sm text-slate-600">Loading analytics…</p>
  }

  if (!analytics?.readingsCount) {
    return (
      <section className="rounded-xl border border-dashed border-slate-300 bg-slate-50 p-6 text-sm text-slate-600">
        Add glucose readings to unlock analytics, level breakdowns, and trend insights.
      </section>
    )
  }

  const summaryCards: SummaryCard[] = [
    {
      label: 'Average',
      value: formatValue(analytics.averageGlucose),
      helper: 'Across the selected period',
    },
    {
      label: 'Minimum',
      value: formatValue(analytics.minGlucose),
      helper: 'Lowest recorded reading',
    },
    {
      label: 'Maximum',
      value: formatValue(analytics.maxGlucose),
      helper: 'Highest recorded reading',
    },
    {
      label: 'Readings',
      value: formatCount(analytics.readingsCount),
      helper: 'Total logged entries',
    },
  ]

  const levelCards: Array<{ level: GlucoseLevel; count: number | null | undefined; percentage: number | null | undefined }> = [
    { level: 'LOW', count: analytics.lowReadingsCount, percentage: analytics.lowPercentage },
    { level: 'NORMAL', count: analytics.normalReadingsCount, percentage: analytics.normalPercentage },
    { level: 'HIGH', count: analytics.highReadingsCount, percentage: analytics.highPercentage },
    { level: 'CRITICAL', count: analytics.criticalReadingsCount, percentage: analytics.criticalPercentage },
  ]

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
        {summaryCards.map((card) => (
          <section key={card.label} className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-sm font-medium text-slate-500">{card.label}</p>
            <p className="mt-2 text-2xl font-semibold text-slate-900">{card.value}</p>
            <p className="mt-1 text-xs text-slate-500">{card.helper}</p>
          </section>
        ))}
      </div>
      <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <div className="flex flex-wrap gap-3">
          {levelCards.map(({ level, count, percentage }) => (
            <div key={level} className="flex min-w-[10rem] flex-1 flex-col rounded-lg bg-slate-50 p-4">
              <span
                className={`inline-flex w-fit rounded-full px-2.5 py-1 text-xs font-semibold uppercase tracking-wide ${glucoseLevelStyles[level]}`}
              >
                {formatGlucoseLevel(level)}
              </span>
              <span className="mt-3 text-xl font-semibold text-slate-900">{formatCount(count)}</span>
              <span className="mt-1 text-sm text-slate-500">
                {percentage === null || percentage === undefined ? '—' : `${percentage}%`} of readings
              </span>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}
