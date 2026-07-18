import { useState } from 'react'
import type { AlertType, CreateGlucoseAlertRequest } from '../../types/alert'

const ALERT_TYPE_LABELS: Record<AlertType, string> = {
  LOW_GLUCOSE: 'Low Glucose',
  HIGH_GLUCOSE: 'High Glucose',
  CRITICAL_GLUCOSE: 'Critical Glucose',
  MISSED_READING: 'Missed Reading',
}

interface AlertConfigFormProps {
  petName: string
  isSubmitting: boolean
  error: string | null
  onSubmit: (request: CreateGlucoseAlertRequest) => void
  onCancel?: () => void
}

export function AlertConfigForm({ petName, isSubmitting, error, onSubmit, onCancel }: AlertConfigFormProps) {
  const [alertType, setAlertType] = useState<AlertType>('LOW_GLUCOSE')
  const [lowThreshold, setLowThreshold] = useState('')
  const [highThreshold, setHighThreshold] = useState('')
  const [description, setDescription] = useState('')

  const needsLow = alertType === 'LOW_GLUCOSE' || alertType === 'CRITICAL_GLUCOSE'
  const needsHigh = alertType === 'HIGH_GLUCOSE' || alertType === 'CRITICAL_GLUCOSE'

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const request: CreateGlucoseAlertRequest = {
      alertType,
      isEnabled: true,
      description: description.trim() || undefined,
    }
    if (needsLow && lowThreshold) request.lowThreshold = Number(lowThreshold)
    if (needsHigh && highThreshold) request.highThreshold = Number(highThreshold)
    onSubmit(request)
  }

  return (
    <form onSubmit={handleSubmit} className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <h2 className="text-lg font-semibold text-slate-900">New alert for {petName}</h2>
      <p className="mt-1 text-sm text-slate-600">Configure a threshold that will trigger a real-time notification.</p>

      <div className="mt-4 space-y-4">
        <div>
          <label className="block text-sm font-medium text-slate-700" htmlFor="alertType">
            Alert type
          </label>
          <select
            id="alertType"
            value={alertType}
            onChange={(e) => setAlertType(e.target.value as AlertType)}
            className="mt-1 block w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          >
            {(Object.keys(ALERT_TYPE_LABELS) as AlertType[]).map((t) => (
              <option key={t} value={t}>
                {ALERT_TYPE_LABELS[t]}
              </option>
            ))}
          </select>
        </div>

        {needsLow && (
          <div>
            <label className="block text-sm font-medium text-slate-700" htmlFor="lowThreshold">
              Low threshold (mg/dL)
            </label>
            <input
              id="lowThreshold"
              type="number"
              min="0"
              max="999"
              step="1"
              value={lowThreshold}
              onChange={(e) => setLowThreshold(e.target.value)}
              placeholder="e.g. 70"
              className="mt-1 block w-full rounded-md border border-slate-300 px-3 py-2 text-sm text-slate-900 shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
            />
          </div>
        )}

        {needsHigh && (
          <div>
            <label className="block text-sm font-medium text-slate-700" htmlFor="highThreshold">
              High threshold (mg/dL)
            </label>
            <input
              id="highThreshold"
              type="number"
              min="0"
              max="999"
              step="1"
              value={highThreshold}
              onChange={(e) => setHighThreshold(e.target.value)}
              placeholder="e.g. 250"
              className="mt-1 block w-full rounded-md border border-slate-300 px-3 py-2 text-sm text-slate-900 shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
            />
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-slate-700" htmlFor="description">
            Description <span className="font-normal text-slate-500">(optional)</span>
          </label>
          <input
            id="description"
            type="text"
            maxLength={500}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Custom notification message"
            className="mt-1 block w-full rounded-md border border-slate-300 px-3 py-2 text-sm text-slate-900 shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          />
        </div>
      </div>

      {error && (
        <p className="mt-3 text-sm text-red-600" role="alert">
          {error}
        </p>
      )}

      <div className="mt-5 flex gap-3">
        <button
          type="submit"
          disabled={isSubmitting}
          className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500 disabled:opacity-50"
        >
          {isSubmitting ? 'Saving…' : 'Save alert'}
        </button>
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="rounded-md bg-slate-100 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-200"
          >
            Cancel
          </button>
        )}
      </div>
    </form>
  )
}
