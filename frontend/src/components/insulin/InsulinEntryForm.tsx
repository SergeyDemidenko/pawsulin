import { useEffect, useState, type ChangeEvent, type FormEvent } from 'react'
import type { CreateInsulinLogRequest } from '../../types/insulin'
import { formatDateTimeForInput, toApiDateTime } from '../../utils/dateTime'
import { commonInsulinTypes, minimumInsulinAmount } from './insulinConfig'

interface InsulinEntryFormProps {
  petName: string
  isSubmitting: boolean
  error: string | null
  onSubmit: (request: CreateInsulinLogRequest) => Promise<void>
}

interface InsulinFormState {
  insulinType: string
  amountUnits: string
  injectionTime: string
  batchNumber: string
  expirationDate: string
  notes: string
}

function createInitialState(): InsulinFormState {
  return {
    insulinType: '',
    amountUnits: '',
    injectionTime: formatDateTimeForInput(new Date()),
    batchNumber: '',
    expirationDate: '',
    notes: '',
  }
}

export function InsulinEntryForm({ petName, isSubmitting, error, onSubmit }: InsulinEntryFormProps) {
  const [formState, setFormState] = useState<InsulinFormState>(() => createInitialState())
  const [validationError, setValidationError] = useState<string | null>(null)

  useEffect(() => {
    setFormState(createInitialState())
    setValidationError(null)
  }, [petName])

  const handleChange =
    (field: keyof InsulinFormState) => (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
      setFormState((current) => ({ ...current, [field]: event.target.value }))
    }

  const handleReset = () => {
    setValidationError(null)
    setFormState(createInitialState())
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setValidationError(null)

    const insulinType = formState.insulinType.trim()
    if (!insulinType) {
      setValidationError('Insulin type is required.')
      return
    }

    const amountUnits = Number.parseFloat(formState.amountUnits)
    if (Number.isNaN(amountUnits) || amountUnits < minimumInsulinAmount) {
      setValidationError(`Amount must be at least ${minimumInsulinAmount} units.`)
      return
    }

    if (!formState.injectionTime) {
      setValidationError('Injection time is required.')
      return
    }

    await onSubmit({
      insulinType,
      amountUnits,
      injectionTime: toApiDateTime(formState.injectionTime),
      batchNumber: formState.batchNumber.trim() || undefined,
      expirationDate: formState.expirationDate || undefined,
      notes: formState.notes.trim() || undefined,
    })

    setFormState(createInitialState())
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4 rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div>
        <h2 className="text-lg font-semibold text-slate-900">Log an insulin injection</h2>
        <p className="mt-1 text-sm text-slate-600">Record a new injection for {petName}.</p>
      </div>
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Insulin type</span>
          <input
            required
            type="text"
            list="insulin-type-list"
            maxLength={100}
            value={formState.insulinType}
            onChange={handleChange('insulinType')}
            placeholder="e.g. Vetsulin, Glargine"
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
          <datalist id="insulin-type-list">
            {commonInsulinTypes.map((type) => (
              <option key={type} value={type} />
            ))}
          </datalist>
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Amount (units)</span>
          <input
            required
            type="number"
            min={minimumInsulinAmount}
            step={0.1}
            inputMode="decimal"
            value={formState.amountUnits}
            onChange={handleChange('amountUnits')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Injection time</span>
          <input
            required
            type="datetime-local"
            value={formState.injectionTime}
            onChange={handleChange('injectionTime')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Expiration date</span>
          <input
            type="date"
            value={formState.expirationDate}
            onChange={handleChange('expirationDate')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        <label className="block md:col-span-2">
          <span className="mb-1 block text-sm font-medium text-slate-700">Batch number</span>
          <input
            type="text"
            maxLength={100}
            value={formState.batchNumber}
            onChange={handleChange('batchNumber')}
            placeholder="Optional vial or lot number"
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
      </div>
      <label className="block">
        <span className="mb-1 block text-sm font-medium text-slate-700">Notes</span>
        <textarea
          rows={3}
          maxLength={500}
          value={formState.notes}
          onChange={handleChange('notes')}
          placeholder="Optional context such as injection site, pet's reaction, or meal timing."
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
          {isSubmitting ? 'Saving…' : 'Save injection'}
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
