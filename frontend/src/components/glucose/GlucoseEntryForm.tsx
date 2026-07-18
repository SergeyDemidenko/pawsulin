import { useEffect, useState, type ChangeEvent, type FormEvent } from 'react'
import type { CreateGlucoseReadingRequest } from '../../types/glucose'
import { formatDateTimeForInput, toApiDateTime } from '../../utils/dateTime'
import { minimumGlucoseValue } from './glucoseConfig'

interface GlucoseEntryFormProps {
  petName: string
  isSubmitting: boolean
  error: string | null
  onSubmit: (request: CreateGlucoseReadingRequest) => Promise<void>
}

interface GlucoseFormState {
  glucoseValue: string
  readingTime: string
  notes: string
}

function createInitialState(): GlucoseFormState {
  return {
    glucoseValue: '',
    readingTime: formatDateTimeForInput(new Date()),
    notes: '',
  }
}

export function GlucoseEntryForm({ petName, isSubmitting, error, onSubmit }: GlucoseEntryFormProps) {
  const [formState, setFormState] = useState<GlucoseFormState>(() => createInitialState())
  const [validationError, setValidationError] = useState<string | null>(null)

  useEffect(() => {
    setFormState(createInitialState())
    setValidationError(null)
  }, [petName])

  const handleChange =
    (field: keyof GlucoseFormState) => (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setFormState((currentState) => ({
        ...currentState,
        [field]: event.target.value,
      }))
    }

  const handleReset = () => {
    setValidationError(null)
    setFormState(createInitialState())
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setValidationError(null)

    const glucoseValue = Number.parseFloat(formState.glucoseValue)

    if (Number.isNaN(glucoseValue) || glucoseValue < minimumGlucoseValue) {
      setValidationError(`Glucose value must be at least ${minimumGlucoseValue} mg/dL.`)
      return
    }

    if (!formState.readingTime) {
      setValidationError('Reading time is required.')
      return
    }

    await onSubmit({
      glucoseValue,
      readingTime: toApiDateTime(formState.readingTime),
      notes: formState.notes.trim() || undefined,
    })

    setFormState(createInitialState())
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4 rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div>
        <h2 className="text-lg font-semibold text-slate-900">Log a glucose reading</h2>
        <p className="mt-1 text-sm text-slate-600">Add a new reading for {petName} and keep the latest trend up to date.</p>
      </div>
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Glucose (mg/dL)</span>
          <input
            required
            type="number"
            min={minimumGlucoseValue}
            step={0.1}
            inputMode="decimal"
            value={formState.glucoseValue}
            onChange={handleChange('glucoseValue')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Reading time</span>
          <input
            required
            type="datetime-local"
            value={formState.readingTime}
            onChange={handleChange('readingTime')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
      </div>
      <label className="block">
        <span className="mb-1 block text-sm font-medium text-slate-700">Notes</span>
        <textarea
          rows={4}
          maxLength={500}
          value={formState.notes}
          onChange={handleChange('notes')}
          placeholder="Optional context such as meal timing, symptoms, or stressors."
          className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
        />
      </label>
      {validationError ? <p className="text-sm text-red-600">{validationError}</p> : null}
      {error ? <p className="text-sm text-red-600">{error}</p> : null}
      <div className="flex flex-wrap gap-3">
        <button
          type="submit"
          disabled={isSubmitting}
          className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:bg-indigo-400"
        >
          {isSubmitting ? 'Saving…' : 'Save reading'}
        </button>
        <button
          type="button"
          onClick={handleReset}
          disabled={isSubmitting}
          className="rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:text-slate-400"
        >
          Reset
        </button>
      </div>
    </form>
  )
}
