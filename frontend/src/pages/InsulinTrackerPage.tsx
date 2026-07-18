import { useEffect, useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { InsulinEntryForm } from '../components/insulin/InsulinEntryForm'
import { InsulinHistoryTable } from '../components/insulin/InsulinHistoryTable'
import { InsulinPetSelector } from '../components/insulin/InsulinPetSelector'
import { InsulinSchedule } from '../components/insulin/InsulinSchedule'
import { buildInsulinRange, defaultPetPageSize, insulinHistoryPageSize, insulinRangeDays } from '../components/insulin/insulinConfig'
import { useCreateInsulinLogMutation, useInsulinHistory, useInsulinRange } from '../hooks/useInsulin'
import { usePets } from '../hooks/usePets'
import type { CreateInsulinLogRequest } from '../types/insulin'
import { extractApiErrorMessage } from '../utils/apiError'

export function InsulinTrackerPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [historyPage, setHistoryPage] = useState(0)
  const [submitError, setSubmitError] = useState<string | null>(null)

  const petsQuery = usePets({
    page: 0,
    size: defaultPetPageSize,
    sortBy: 'name',
    direction: 'ASC',
  })

  const selectedPetId = Number(searchParams.get('petId'))
  const pets = useMemo(() => petsQuery.data?.content ?? [], [petsQuery.data?.content])
  const effectivePetId = pets.some((pet) => pet.id === selectedPetId) ? selectedPetId : (pets[0]?.id ?? Number.NaN)
  const selectedPet = pets.find((pet) => pet.id === effectivePetId)

  const range = useMemo(() => buildInsulinRange(insulinRangeDays), [])

  const createInsulinLogMutation = useCreateInsulinLogMutation(effectivePetId)

  const insulinHistoryQuery = useInsulinHistory(effectivePetId, {
    page: historyPage,
    size: insulinHistoryPageSize,
    sortBy: 'injectionTime',
    direction: 'DESC',
  })

  const insulinRangeQuery = useInsulinRange(effectivePetId, range)

  useEffect(() => {
    if (!pets.length) return

    if (!Number.isFinite(selectedPetId) || !pets.some((pet) => pet.id === selectedPetId)) {
      setSearchParams(
        (current) => {
          const next = new URLSearchParams(current)
          next.set('petId', pets[0].id.toString())
          return next
        },
        { replace: true },
      )
    }
  }, [pets, selectedPetId, setSearchParams])

  useEffect(() => {
    setHistoryPage(0)
  }, [effectivePetId])

  const handlePetChange = (petId: number) => {
    setHistoryPage(0)
    setSearchParams(new URLSearchParams({ petId: petId.toString() }))
  }

  const handleCreate = async (request: CreateInsulinLogRequest) => {
    setSubmitError(null)

    try {
      await createInsulinLogMutation.mutateAsync(request)
    } catch (requestError) {
      setSubmitError(extractApiErrorMessage(requestError, 'Unable to save injection. Please try again.'))
    }
  }

  if (petsQuery.isLoading) {
    return <p className="text-sm text-slate-600">Loading insulin tracking workspace…</p>
  }

  if (petsQuery.isError) {
    return (
      <section className="rounded-xl border border-red-200 bg-red-50 p-6">
        <h1 className="text-xl font-semibold text-red-900">Could not load pets</h1>
        <p className="mt-2 text-sm text-red-700">Refresh the page or try again later.</p>
      </section>
    )
  }

  if (!selectedPet) {
    return (
      <section className="rounded-xl border border-dashed border-slate-300 bg-white p-6 shadow-sm">
        <h1 className="text-2xl font-semibold text-slate-900">Insulin tracker</h1>
        <p className="mt-2 text-sm text-slate-600">Create a pet profile before logging insulin injections and reviewing the schedule.</p>
        <Link
          to="/pets"
          className="mt-4 inline-flex rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500"
        >
          Go to pet management
        </Link>
      </section>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-3xl font-semibold text-slate-900">Insulin tracker</h1>
          <p className="mt-2 text-sm text-slate-600">
            Log insulin injections, review history, and monitor the projected schedule for {selectedPet.name}.
          </p>
        </div>
        <div className="w-full max-w-xs">
          <InsulinPetSelector pets={pets} selectedPetId={selectedPet.id} onChange={handlePetChange} />
        </div>
      </div>
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
        <InsulinEntryForm
          petName={selectedPet.name}
          isSubmitting={createInsulinLogMutation.isPending}
          error={submitError}
          onSubmit={handleCreate}
        />
        <InsulinSchedule
          recentLogs={insulinRangeQuery.data}
          isLoading={insulinRangeQuery.isLoading}
        />
      </div>
      <InsulinHistoryTable
        historyPage={insulinHistoryQuery.data}
        currentPage={historyPage}
        isLoading={insulinHistoryQuery.isFetching}
        onPageChange={setHistoryPage}
      />
    </div>
  )
}
