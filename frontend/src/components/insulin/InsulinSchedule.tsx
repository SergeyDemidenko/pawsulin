import type { InsulinLog } from '../../types/insulin'
import { formatDateTime } from '../../utils/dateTime'

interface InsulinScheduleProps {
  recentLogs: InsulinLog[] | undefined
  isLoading: boolean
}

interface ScheduledEntry {
  label: string
  time: Date
  insulinType: string
  amountUnits: number
  isOverdue: boolean
}

const UPCOMING_COUNT = 5
const MIN_LOGS_FOR_INTERVAL = 2

function computeAverageIntervalMs(logs: InsulinLog[]): number | null {
  if (logs.length < MIN_LOGS_FOR_INTERVAL) {
    return null
  }

  const sorted = [...logs].sort(
    (a, b) => new Date(a.injectionTime).getTime() - new Date(b.injectionTime).getTime(),
  )

  let totalMs = 0
  for (let i = 1; i < sorted.length; i++) {
    totalMs += new Date(sorted[i].injectionTime).getTime() - new Date(sorted[i - 1].injectionTime).getTime()
  }

  return totalMs / (sorted.length - 1)
}

function buildSchedule(logs: InsulinLog[]): ScheduledEntry[] {
  if (!logs.length) return []

  const sorted = [...logs].sort(
    (a, b) => new Date(b.injectionTime).getTime() - new Date(a.injectionTime).getTime(),
  )
  const latest = sorted[0]
  const latestTime = new Date(latest.injectionTime)
  const intervalMs = computeAverageIntervalMs(logs)

  if (!intervalMs || intervalMs <= 0) return []

  const now = new Date()
  const entries: ScheduledEntry[] = []

  for (let i = 1; i <= UPCOMING_COUNT; i++) {
    const time = new Date(latestTime.getTime() + intervalMs * i)
    entries.push({
      label: `Injection ${i}`,
      time,
      insulinType: latest.insulinType,
      amountUnits: latest.amountUnits,
      isOverdue: time < now,
    })
  }

  return entries
}

function formatInterval(ms: number): string {
  const hours = ms / (1000 * 60 * 60)
  if (hours < 24) {
    return `~${Math.round(hours)}h`
  }
  const days = hours / 24
  return `~${days.toFixed(1)}d`
}

export function InsulinSchedule({ recentLogs, isLoading }: InsulinScheduleProps) {
  if (isLoading) {
    return (
      <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 className="text-lg font-semibold text-slate-900">Injection schedule</h2>
        <p className="mt-2 text-sm text-slate-600">Loading schedule…</p>
      </section>
    )
  }

  if (!recentLogs || recentLogs.length === 0) {
    return (
      <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 className="text-lg font-semibold text-slate-900">Injection schedule</h2>
        <p className="mt-2 text-sm text-slate-600">
          No recent injections found. Log at least two injections to see a projected schedule.
        </p>
      </section>
    )
  }

  const intervalMs = computeAverageIntervalMs(recentLogs)
  const schedule = buildSchedule(recentLogs)

  const sorted = [...recentLogs].sort(
    (a, b) => new Date(b.injectionTime).getTime() - new Date(a.injectionTime).getTime(),
  )
  const latest = sorted[0]

  return (
    <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div>
        <h2 className="text-lg font-semibold text-slate-900">Injection schedule</h2>
        <p className="mt-1 text-sm text-slate-600">
          Projected from the last {recentLogs.length} injection{recentLogs.length === 1 ? '' : 's'}.
          {intervalMs ? ` Average interval: ${formatInterval(intervalMs)}.` : ''}
        </p>
      </div>
      <dl className="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-3">
        <div className="rounded-lg border border-slate-100 bg-slate-50 px-4 py-3">
          <dt className="text-xs font-medium uppercase tracking-wide text-slate-500">Last injection</dt>
          <dd className="mt-1 text-sm font-semibold text-slate-900">{formatDateTime(latest.injectionTime)}</dd>
        </div>
        <div className="rounded-lg border border-slate-100 bg-slate-50 px-4 py-3">
          <dt className="text-xs font-medium uppercase tracking-wide text-slate-500">Insulin type</dt>
          <dd className="mt-1 text-sm font-semibold text-slate-900">{latest.insulinType}</dd>
        </div>
        <div className="rounded-lg border border-slate-100 bg-slate-50 px-4 py-3">
          <dt className="text-xs font-medium uppercase tracking-wide text-slate-500">Typical dose</dt>
          <dd className="mt-1 text-sm font-semibold text-slate-900">{latest.amountUnits} U</dd>
        </div>
      </dl>
      {schedule.length > 0 ? (
        <div className="mt-5">
          <h3 className="mb-3 text-sm font-medium text-slate-700">Upcoming injections</h3>
          <ol className="space-y-2">
            {schedule.map((entry, index) => (
              <li
                key={index}
                className={`flex items-center justify-between rounded-lg border px-4 py-3 text-sm ${
                  entry.isOverdue
                    ? 'border-rose-200 bg-rose-50'
                    : 'border-slate-200 bg-white'
                }`}
              >
                <div className="flex items-center gap-3">
                  {entry.isOverdue ? (
                    <span className="inline-flex rounded-full bg-rose-100 px-2 py-0.5 text-xs font-semibold text-rose-700">
                      Overdue
                    </span>
                  ) : (
                    <span className="inline-flex rounded-full bg-emerald-100 px-2 py-0.5 text-xs font-semibold text-emerald-700">
                      Upcoming
                    </span>
                  )}
                  <span className="font-medium text-slate-900">{formatDateTime(entry.time.toISOString())}</span>
                </div>
                <span className="text-slate-600">
                  {entry.insulinType} · {entry.amountUnits} U
                </span>
              </li>
            ))}
          </ol>
          <p className="mt-3 text-xs text-slate-500">
            Schedule is estimated from past injection intervals. Always follow your veterinarian's prescribed dosing schedule.
          </p>
        </div>
      ) : (
        <p className="mt-4 text-sm text-slate-600">
          Log at least two injections to see a projected schedule.
        </p>
      )}
    </section>
  )
}
