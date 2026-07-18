import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  ReferenceArea,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import type { GlucoseAnalytics, GlucoseReading, GlucoseLevel } from '../../types/glucose'
import { formatDateForChart } from '../../utils/dateTime'
import { targetGlucoseRange } from './glucoseConfig'
import { formatGlucoseLevel, glucoseLevelColors } from './glucosePresentation'

interface GlucoseChartsProps {
  readings: GlucoseReading[] | undefined
  analytics: GlucoseAnalytics | undefined
  isLoading: boolean
}

export function GlucoseCharts({ readings, analytics, isLoading }: GlucoseChartsProps) {
  if (isLoading) {
    return <p className="text-sm text-slate-600">Loading glucose charts…</p>
  }

  if (!readings?.length) {
    return (
      <section className="rounded-xl border border-dashed border-slate-300 bg-slate-50 p-6 text-sm text-slate-600">
        Charts will appear after you log glucose readings for the selected time range.
      </section>
    )
  }

  const trendData = [...readings]
    .sort((left, right) => new Date(left.readingTime).getTime() - new Date(right.readingTime).getTime())
    .map((reading) => ({
      id: reading.id,
      label: formatDateForChart(reading.readingTime),
      glucoseValue: reading.glucoseValue,
      glucoseLevel: reading.glucoseLevel,
    }))

  const distributionData: Array<{ level: GlucoseLevel; count: number }> = [
    { level: 'LOW', count: analytics?.lowReadingsCount ?? 0 },
    { level: 'NORMAL', count: analytics?.normalReadingsCount ?? 0 },
    { level: 'HIGH', count: analytics?.highReadingsCount ?? 0 },
    { level: 'CRITICAL', count: analytics?.criticalReadingsCount ?? 0 },
  ]

  return (
    <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
      <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <div>
          <h2 className="text-lg font-semibold text-slate-900">Trend chart</h2>
          <p className="mt-1 text-sm text-slate-600">
            Track changes over time and compare readings to the {targetGlucoseRange.min}–{targetGlucoseRange.max} mg/dL target band.
          </p>
        </div>
        <div className="mt-6 h-80">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={trendData} margin={{ top: 16, right: 16, left: 0, bottom: 16 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <ReferenceArea y1={targetGlucoseRange.min} y2={targetGlucoseRange.max} fill="#d1fae5" fillOpacity={0.45} />
              <XAxis dataKey="label" minTickGap={32} />
              <YAxis unit=" mg/dL" width={84} />
              <Tooltip formatter={(value) => [`${value ?? '—'} mg/dL`, 'Glucose']} />
              <Legend />
              <Line
                type="monotone"
                dataKey="glucoseValue"
                name="Glucose"
                stroke="#4f46e5"
                strokeWidth={3}
                dot={{ r: 4 }}
                activeDot={{ r: 6 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </section>
      <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <div>
          <h2 className="text-lg font-semibold text-slate-900">Level distribution</h2>
          <p className="mt-1 text-sm text-slate-600">Review how often readings fall into low, normal, high, and critical ranges.</p>
        </div>
        <div className="mt-6 h-80">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={distributionData} margin={{ top: 16, right: 16, left: 0, bottom: 16 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="level" tickFormatter={(value: GlucoseLevel) => formatGlucoseLevel(value)} />
              <YAxis allowDecimals={false} />
              <Tooltip formatter={(value) => [value ?? '—', 'Readings']} />
              <Bar dataKey="count" radius={[8, 8, 0, 0]}>
                {distributionData.map((entry) => (
                  <Cell key={entry.level} fill={glucoseLevelColors[entry.level]} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section>
    </div>
  )
}
