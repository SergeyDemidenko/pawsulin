import type { GlucoseAlert, AlertType } from '../../types/alert'

const ALERT_TYPE_LABELS: Record<AlertType, string> = {
  LOW_GLUCOSE: 'Low Glucose',
  HIGH_GLUCOSE: 'High Glucose',
  CRITICAL_GLUCOSE: 'Critical Glucose',
  MISSED_READING: 'Missed Reading',
}

const ALERT_TYPE_COLORS: Record<AlertType, string> = {
  LOW_GLUCOSE: 'bg-blue-100 text-blue-800',
  HIGH_GLUCOSE: 'bg-orange-100 text-orange-800',
  CRITICAL_GLUCOSE: 'bg-red-100 text-red-800',
  MISSED_READING: 'bg-slate-100 text-slate-700',
}

interface AlertConfigListProps {
  alerts: GlucoseAlert[]
  isLoading: boolean
  onDelete: (alertId: number) => void
  isDeleting: boolean
}

export function AlertConfigList({ alerts, isLoading, onDelete, isDeleting }: AlertConfigListProps) {
  if (isLoading) {
    return <p className="text-sm text-slate-500">Loading alerts…</p>
  }

  if (alerts.length === 0) {
    return (
      <p className="rounded-xl border border-dashed border-slate-300 bg-white px-5 py-8 text-center text-sm text-slate-500">
        No alerts configured yet. Create one above to start receiving notifications.
      </p>
    )
  }

  return (
    <ul className="space-y-3">
      {alerts.map((alert) => (
        <li key={alert.id} className="flex items-start justify-between gap-4 rounded-xl border border-slate-200 bg-white px-5 py-4 shadow-sm">
          <div className="min-w-0 flex-1">
            <div className="flex flex-wrap items-center gap-2">
              <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${ALERT_TYPE_COLORS[alert.alertType]}`}>
                {ALERT_TYPE_LABELS[alert.alertType]}
              </span>
            </div>
            <div className="mt-1.5 flex flex-wrap gap-x-4 gap-y-0.5 text-sm text-slate-600">
              {alert.lowThreshold !== null && (
                <span>Low threshold: <strong>{alert.lowThreshold} mg/dL</strong></span>
              )}
              {alert.highThreshold !== null && (
                <span>High threshold: <strong>{alert.highThreshold} mg/dL</strong></span>
              )}
            </div>
            {alert.description && (
              <p className="mt-1 text-xs text-slate-500">{alert.description}</p>
            )}
          </div>
          <button
            type="button"
            onClick={() => onDelete(alert.id)}
            disabled={isDeleting}
            aria-label="Delete alert"
            className="shrink-0 rounded-md px-3 py-1.5 text-sm font-medium text-red-600 transition hover:bg-red-50 disabled:opacity-50"
          >
            Delete
          </button>
        </li>
      ))}
    </ul>
  )
}
