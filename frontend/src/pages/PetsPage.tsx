import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { PetForm } from '../components/pet/PetForm'
import { PetList } from '../components/pet/PetList'
import { useCreatePetMutation, usePets } from '../hooks/usePets'
import type { CreatePetRequest } from '../types/pet'
import { extractApiErrorMessage } from '../utils/apiError'

const pageSize = 5

export function PetsPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [submitError, setSubmitError] = useState<string | null>(null)
  const pageParam = Number.parseInt(searchParams.get('page') ?? '1', 10)
  const currentPage = Number.isNaN(pageParam) || pageParam < 1 ? 0 : pageParam - 1
  const petsQuery = usePets({
    page: currentPage,
    size: pageSize,
    sortBy: 'createdAt',
    direction: 'DESC',
  })
  const createPetMutation = useCreatePetMutation()

  const handleCreate = async (request: CreatePetRequest) => {
    setSubmitError(null)

    try {
      await createPetMutation.mutateAsync(request)
      setSearchParams((current) => {
        const next = new URLSearchParams(current)
        next.set('page', '1')
        return next
      })
    } catch (requestError) {
      setSubmitError(extractApiErrorMessage(requestError, 'Unable to create pet. Please try again.'))
    }
  }

  const handlePageChange = (page: number) => {
    const nextPage = page + 1
    setSearchParams(nextPage <= 1 ? new URLSearchParams() : new URLSearchParams({ page: nextPage.toString() }))
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold text-slate-900">Pet management</h1>
        <p className="mt-2 text-sm text-slate-600">Create and manage pet profiles before recording glucose and insulin data.</p>
      </div>
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)]">
        <PetForm
          includeSpeciesField
          submitLabel="Add pet"
          isSubmitting={createPetMutation.isPending}
          error={submitError}
          onSubmit={handleCreate}
        />
        {petsQuery.isLoading ? (
          <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-sm text-slate-600">Loading pets…</p>
          </section>
        ) : petsQuery.isError || !petsQuery.data ? (
          <section className="rounded-xl border border-red-200 bg-red-50 p-5 shadow-sm">
            <h2 className="text-lg font-semibold text-red-900">Could not load pets</h2>
            <p className="mt-2 text-sm text-red-700">Please refresh the page or try again later.</p>
          </section>
        ) : (
          <PetList
            petsPage={petsQuery.data}
            currentPage={currentPage}
            isLoading={petsQuery.isFetching}
            onPageChange={handlePageChange}
          />
        )}
      </div>
    </div>
  )
}
