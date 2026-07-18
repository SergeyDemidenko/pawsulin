import type { Pet } from '../../types/pet'

interface PetDetailProps {
  pet: Pet
}

function formatDate(dateValue: string): string {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(dateValue))
}

function formatWeight(weightKg: number | null): string {
  return weightKg === null ? 'Not provided' : `${weightKg} kg`
}

export function PetDetail({ pet }: PetDetailProps) {
  return (
    <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-sm font-medium uppercase tracking-wide text-indigo-600">{pet.species}</p>
          <h1 className="mt-1 text-3xl font-semibold text-slate-900">{pet.name}</h1>
          <p className="mt-2 text-sm text-slate-600">{pet.breed || 'Breed not specified'}</p>
        </div>
        <span className="rounded-full bg-emerald-50 px-3 py-1 text-sm font-medium text-emerald-700">
          {pet.isActive ? 'Active' : 'Inactive'}
        </span>
      </div>
      <dl className="mt-6 grid grid-cols-1 gap-4 text-sm text-slate-700 sm:grid-cols-2">
        <div className="rounded-lg bg-slate-50 p-4">
          <dt className="text-slate-500">Age</dt>
          <dd className="mt-1 text-base font-medium text-slate-900">{pet.ageYears} years</dd>
        </div>
        <div className="rounded-lg bg-slate-50 p-4">
          <dt className="text-slate-500">Weight</dt>
          <dd className="mt-1 text-base font-medium text-slate-900">{formatWeight(pet.weightKg)}</dd>
        </div>
        <div className="rounded-lg bg-slate-50 p-4">
          <dt className="text-slate-500">Diabetes type</dt>
          <dd className="mt-1 text-base font-medium text-slate-900">{pet.diabetesType}</dd>
        </div>
        <div className="rounded-lg bg-slate-50 p-4">
          <dt className="text-slate-500">Last updated</dt>
          <dd className="mt-1 text-base font-medium text-slate-900">{formatDate(pet.updatedAt)}</dd>
        </div>
      </dl>
      <div className="mt-6 rounded-lg border border-slate-200 p-4">
        <h2 className="text-sm font-medium uppercase tracking-wide text-slate-500">Medical notes</h2>
        <p className="mt-2 whitespace-pre-wrap text-sm text-slate-700">
          {pet.medicalNotes || 'No medical notes have been added for this pet yet.'}
        </p>
      </div>
    </section>
  )
}
