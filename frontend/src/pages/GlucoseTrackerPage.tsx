import { useEffect, useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { GlucoseAnalyticsCards } from '../components/glucose/GlucoseAnalyticsCards'
import { GlucoseCharts } from '../components/glucose/GlucoseCharts'
import {
  buildGlucoseRange,
  defaultPetPageSize,
  glucoseHistoryPageSize,
  glucoseRangeOptions,
} from '../components/glucose/glucoseConfig'
import { GlucoseEntryForm } from '../components/glucose/GlucoseEntryForm'
import { GlucoseHistoryTable } from '../components/glucose/GlucoseHistoryTable'
import { GlucosePetSelector } from '../components/glucose/GlucosePetSelector'
import { useCreateGlucoseReadingMutation, useGlucoseAnalytics, useGlucoseHistory, useGlucoseRange } from '../hooks/useGlucose'
import { usePets } from '../hooks/usePets'
import { extractApiErrorMessage } from '../utils/apiError'

export function GlucoseTrackerPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [historyPage, setHistoryPage] = useState(0)
  const [submitError, setSubmitError] = useState<string | null>(null)
  const petsQuery = usePets({
    page: 0,
    size: defaultPetPageSize,
    sortBy: 'name',
    direction: 'ASC',
  })

  const selectedDays = glucoseRangeOptions.includes(Number(searchParams.get('days')) as (typeof glucoseRangeOptions)[number])
    ? Number(searchParams.get('days'))
    : 14
  const selectedPetId = Number(searchParams.get('petId'))
  const pets = useMemo(() => petsQuery.data?.content ?? [], [petsQuery.data?.content])
  const effectivePetId = pets.some((pet) => pet.id === selectedPetId) ? selectedPetId : (pets[0]?.id ?? Number.NaN)
  const selectedPet = pets.find((pet) => pet.id === effectivePetId)
  const range = useMemo(() => buildGlucoseRange(selectedDays), [selectedDays])
  const createGlucoseReadingMutation = useCreateGlucoseReadingMutation(effectivePetId)
  const glucoseHistoryQuery = useGlucoseHistory(effectivePetId, {
    page: historyPage,
    size: glucoseHistoryPageSize,
    sortBy: 'readingTime',
    direction: 'DESC',
  })
  const glucoseRangeQuery = useGlucoseRange(effectivePetId, range)
  const glucoseAnalyticsQuery = useGlucoseAnalytics(effectivePetId, range)

  useEffect(() => {
    if (!pets.length) {
      return
    }

    if (!Number.isFinite(selectedPetId) || !pets.some((pet) => pet.id === selectedPetId)) {
      setSearchParams((current) => {
        const next = new URLSearchParams(current)
        next.set('petId', pets[0].id.toString())
        next.set('days', selectedDays.toString())
        return next
      }, { replace: true })
    }
  }, [pets, selectedDays, selectedPetId, setSearchParams])

  useEffect(() => {
    setHistoryPage(0)
  }, [effectivePetId])

  const handlePetChange = (petId: number) => {
    setHistoryPage(0)
    setSearchParams(new URLSearchParams({ petId: petId.toString(), days: selectedDays.toString() }))
  }

  const handleRangeChange = (days: number) => {
    setSearchParams(new URLSearchParams({ petId: effectivePetId.toString(), days: days.toString() }))
  }

  const handleCreate = async (request: { glucoseValue: number; readingTime: string; notes?: string }) => {
    setSubmitError(null)

    try {
      await createGlucoseReadingMutation.mutateAsync(request)
    } catch (requestError) {
      setSubmitError(extractApiErrorMessage(requestError, 'Unable to save glucose reading. Please try again.'))
    }
  }

  if (petsQuery.isLoading) {
    return <p className="text-sm text-slate-600">Loading glucose tracking workspace…</p>
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
        <h1 className="text-2xl font-semibold text-slate-900">Glucose tracker</h1>
        <p className="mt-2 text-sm text-slate-600">Create a pet profile before logging glucose readings and reviewing analytics.</p>
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
          <h1 className="text-3xl font-semibold text-slate-900">Glucose tracker</h1>
          <p className="mt-2 text-sm text-slate-600">Log new glucose readings, inspect history, and monitor trends for {selectedPet.name}.</p>
        </div>
        <div className="w-full max-w-xs">
          <GlucosePetSelector pets={pets} selectedPetId={selectedPet.id} onChange={handlePetChange} />
        </div>
      </div>
      <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="text-lg font-semibold text-slate-900">Analytics window</h2>
            <p className="mt-1 text-sm text-slate-600">Switch the period to compare recent readings and the current range distribution.</p>
          </div>
          <div className="flex flex-wrap gap-2">
            {glucoseRangeOptions.map((days) => (
              <button
                key={days}
                type="button"
                onClick={() => handleRangeChange(days)}
                className={`rounded-md px-3 py-1.5 text-sm font-medium transition ${
                  selectedDays === days
                    ? 'bg-indigo-600 text-white'
                    : 'border border-slate-300 text-slate-700 hover:bg-slate-50'
                }`}
              >
                Last {days} days
              </button>
            ))}
          </div>
        </div>
      </section>
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[minmax(0,0.95fr)_minmax(0,1.05fr)]">
        <GlucoseEntryForm
          petName={selectedPet.name}
          isSubmitting={createGlucoseReadingMutation.isPending}
          error={submitError}
          onSubmit={handleCreate}
        />
        <GlucoseAnalyticsCards analytics={glucoseAnalyticsQuery.data} isLoading={glucoseAnalyticsQuery.isLoading} />
      </div>
      <GlucoseCharts
        readings={glucoseRangeQuery.data}
        analytics={glucoseAnalyticsQuery.data}
        isLoading={glucoseRangeQuery.isLoading || glucoseAnalyticsQuery.isLoading}
      />
      <GlucoseHistoryTable
        historyPage={glucoseHistoryQuery.data}
        currentPage={historyPage}
        isLoading={glucoseHistoryQuery.isFetching}
        onPageChange={setHistoryPage}
      />
    </div>
  )
}
