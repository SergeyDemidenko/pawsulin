import { Link } from 'react-router-dom'
import type { PageResponse, Pet } from '../../types/pet'

interface PetListProps {
  petsPage: PageResponse<Pet>
  currentPage: number
  isLoading: boolean
  onPageChange: (page: number) => void
}

function formatWeight(weightKg: number | null): string {
  return weightKg === null ? '—' : `${weightKg} kg`
}

export function PetList({ petsPage, currentPage, isLoading, onPageChange }: PetListProps) {
  const pages = Array.from({ length: petsPage.totalPages }, (_, index) => index)

  return (
    <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <h2 className="text-lg font-semibold text-slate-900">Your pets</h2>
          <p className="mt-1 text-sm text-slate-600">
            Showing {petsPage.numberOfElements} of {petsPage.totalElements} pet{petsPage.totalElements === 1 ? '' : 's'}.
          </p>
        </div>
        {isLoading ? <span className="text-sm text-slate-500">Refreshing…</span> : null}
      </div>
      {petsPage.empty ? (
        <div className="mt-6 rounded-lg border border-dashed border-slate-300 bg-slate-50 px-4 py-8 text-center text-sm text-slate-600">
          No pets yet. Add your first pet using the form.
        </div>
      ) : (
        <div className="mt-6 space-y-4">
          {petsPage.content.map((pet) => (
            <article key={pet.id} className="rounded-lg border border-slate-200 p-4">
              <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                  <h3 className="text-lg font-semibold text-slate-900">{pet.name}</h3>
                  <p className="mt-1 text-sm text-slate-600">
                    {pet.species} • {pet.breed || 'Breed not specified'}
                  </p>
                </div>
                <Link
                  to={`/pets/${pet.id}`}
                  className="rounded-md border border-indigo-200 bg-indigo-50 px-3 py-1.5 text-sm font-medium text-indigo-700 transition hover:bg-indigo-100"
                >
                  View details
                </Link>
              </div>
              <dl className="mt-4 grid grid-cols-1 gap-3 text-sm text-slate-700 sm:grid-cols-3">
                <div>
                  <dt className="text-slate-500">Age</dt>
                  <dd className="mt-1 font-medium">{pet.ageYears} years</dd>
                </div>
                <div>
                  <dt className="text-slate-500">Weight</dt>
                  <dd className="mt-1 font-medium">{formatWeight(pet.weightKg)}</dd>
                </div>
                <div>
                  <dt className="text-slate-500">Diabetes type</dt>
                  <dd className="mt-1 font-medium">{pet.diabetesType}</dd>
                </div>
              </dl>
              {pet.medicalNotes ? <p className="mt-4 text-sm text-slate-600">{pet.medicalNotes}</p> : null}
            </article>
          ))}
        </div>
      )}
      {petsPage.totalPages > 1 ? (
        <div className="mt-6 flex flex-wrap items-center gap-2">
          <button
            type="button"
            onClick={() => onPageChange(currentPage - 1)}
            disabled={petsPage.first}
            className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:text-slate-400"
          >
            Previous
          </button>
          {pages.map((page) => (
            <button
              key={page}
              type="button"
              onClick={() => onPageChange(page)}
              className={`rounded-md px-3 py-1.5 text-sm font-medium transition ${
                page === currentPage ? 'bg-indigo-600 text-white' : 'border border-slate-300 text-slate-700 hover:bg-slate-50'
              }`}
            >
              {page + 1}
            </button>
          ))}
          <button
            type="button"
            onClick={() => onPageChange(currentPage + 1)}
            disabled={petsPage.last}
            className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:text-slate-400"
          >
            Next
          </button>
        </div>
      ) : null}
    </section>
  )
}
