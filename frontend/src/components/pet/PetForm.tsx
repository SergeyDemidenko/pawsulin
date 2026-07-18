import { useMemo, useState, type FormEvent } from 'react'
import type { CreatePetRequest, Pet, UpdatePetRequest } from '../../types/pet'

interface PetFormProps {
  initialPet?: Pet
  includeSpeciesField: boolean
  submitLabel: string
  isSubmitting: boolean
  error: string | null
  onSubmit: (request: CreatePetRequest | UpdatePetRequest) => Promise<void>
}

interface PetFormState {
  name: string
  species: string
  breed: string
  ageYears: string
  weightKg: string
  diabetesType: string
  medicalNotes: string
}

function toFormState(pet?: Pet): PetFormState {
  return {
    name: pet?.name ?? '',
    species: pet?.species ?? '',
    breed: pet?.breed ?? '',
    ageYears: pet?.ageYears?.toString() ?? '',
    weightKg: pet?.weightKg?.toString() ?? '',
    diabetesType: pet?.diabetesType ?? '',
    medicalNotes: pet?.medicalNotes ?? '',
  }
}

function normalizeOptionalString(value: string): string | undefined {
  const trimmed = value.trim()
  return trimmed.length > 0 ? trimmed : undefined
}

export function PetForm({
  initialPet,
  includeSpeciesField,
  submitLabel,
  isSubmitting,
  error,
  onSubmit,
}: PetFormProps) {
  const initialState = useMemo(() => toFormState(initialPet), [initialPet])
  const [formState, setFormState] = useState<PetFormState>(initialState)
  const [validationError, setValidationError] = useState<string | null>(null)

  const handleChange =
    (field: keyof PetFormState) => (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setFormState((currentState) => ({
        ...currentState,
        [field]: event.target.value,
      }))
    }

  const handleReset = () => {
    setValidationError(null)
    setFormState(initialState)
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setValidationError(null)

    const ageYears = Number.parseInt(formState.ageYears, 10)
    const weightValue = formState.weightKg.trim()
    const weightKg = weightValue.length > 0 ? Number.parseFloat(weightValue) : undefined

    if (Number.isNaN(ageYears) || ageYears < 0) {
      setValidationError('Age must be a valid non-negative number.')
      return
    }

    if (weightKg !== undefined && (Number.isNaN(weightKg) || weightKg <= 0)) {
      setValidationError('Weight must be greater than 0.')
      return
    }

    const baseRequest = {
      name: formState.name.trim(),
      breed: normalizeOptionalString(formState.breed),
      ageYears,
      weightKg,
      diabetesType: formState.diabetesType.trim(),
      medicalNotes: normalizeOptionalString(formState.medicalNotes),
    }

    if (includeSpeciesField) {
      await onSubmit({
        ...baseRequest,
        species: formState.species.trim(),
      })
      return
    }

    await onSubmit(baseRequest)
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4 rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div>
        <h2 className="text-lg font-semibold text-slate-900">{initialPet ? 'Edit pet' : 'Add a pet'}</h2>
        <p className="mt-1 text-sm text-slate-600">
          {initialPet ? 'Update the profile and health details for this pet.' : 'Create a new pet profile to start tracking care.'}
        </p>
      </div>
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Name</span>
          <input
            required
            minLength={2}
            maxLength={100}
            value={formState.name}
            onChange={handleChange('name')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Species</span>
          <input
            required={includeSpeciesField}
            minLength={2}
            maxLength={50}
            disabled={!includeSpeciesField}
            value={formState.species}
            onChange={handleChange('species')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200 disabled:cursor-not-allowed disabled:bg-slate-100"
          />
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Breed</span>
          <input
            maxLength={100}
            value={formState.breed}
            onChange={handleChange('breed')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Diabetes type</span>
          <input
            required
            minLength={2}
            maxLength={50}
            value={formState.diabetesType}
            onChange={handleChange('diabetesType')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Age (years)</span>
          <input
            required
            min={0}
            step={1}
            inputMode="numeric"
            value={formState.ageYears}
            onChange={handleChange('ageYears')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Weight (kg)</span>
          <input
            min={0.1}
            step={0.1}
            inputMode="decimal"
            value={formState.weightKg}
            onChange={handleChange('weightKg')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
      </div>
      <label className="block">
        <span className="mb-1 block text-sm font-medium text-slate-700">Medical notes</span>
        <textarea
          rows={4}
          value={formState.medicalNotes}
          onChange={handleChange('medicalNotes')}
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
          {isSubmitting ? 'Saving…' : submitLabel}
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
