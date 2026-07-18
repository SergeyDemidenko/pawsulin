import { useEffect, useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { GlucoseAnalyticsCards } from '../components/glucose/GlucoseAnalyticsCards'
import { GlucoseCharts } from '../components/glucose/GlucoseCharts'
import {
  buildGlucoseRange,
  dashboardHistoryPageSize,
  defaultPetPageSize,
  glucoseRangeOptions,
} from '../components/glucose/glucoseConfig'
import { GlucoseHistoryTable } from '../components/glucose/GlucoseHistoryTable'
import { GlucosePetSelector } from '../components/glucose/GlucosePetSelector'
import { useGlucoseAnalytics, useGlucoseHistory, useGlucoseRange } from '../hooks/useGlucose'
import { usePets } from '../hooks/usePets'

export function DashboardPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [historyPage, setHistoryPage] = useState(0)
  const petsQuery = usePets({
    page: 0,
    size: defaultPetPageSize,
    sortBy: 'name',
    direction: 'ASC',
  })

  const selectedDays = glucoseRangeOptions.includes(Number(searchParams.get('days')) as (typeof glucoseRangeOptions)[number])
    ? Number(searchParams.get('days'))
    : 7
  const selectedPetId = Number(searchParams.get('petId'))
  const pets = useMemo(() => petsQuery.data?.content ?? [], [petsQuery.data?.content])
  const effectivePetId = pets.some((pet) => pet.id === selectedPetId) ? selectedPetId : (pets[0]?.id ?? Number.NaN)
  const selectedPet = pets.find((pet) => pet.id === effectivePetId)
  const range = useMemo(() => buildGlucoseRange(selectedDays), [selectedDays])
  const glucoseRangeQuery = useGlucoseRange(effectivePetId, range)
  const glucoseAnalyticsQuery = useGlucoseAnalytics(effectivePetId, range)
  const glucoseHistoryQuery = useGlucoseHistory(effectivePetId, {
    page: historyPage,
    size: dashboardHistoryPageSize,
    sortBy: 'readingTime',
    direction: 'DESC',
  })

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

  if (petsQuery.isLoading) {
    return <p className="text-sm text-slate-600">Loading dashboard…</p>
  }

  if (petsQuery.isError) {
    return (
      <section className="rounded-xl border border-red-200 bg-red-50 p-6">
        <h1 className="text-xl font-semibold text-red-900">Could not load dashboard</h1>
        <p className="mt-2 text-sm text-red-700">Refresh the page or try again later.</p>
      </section>
    )
  }

  if (!selectedPet) {
    return (
      <section className="rounded-xl border border-dashed border-slate-300 bg-white p-6 shadow-sm">
        <h1 className="text-3xl font-semibold text-slate-900">Care dashboard</h1>
        <p className="mt-2 text-sm text-slate-600">Create your first pet profile to start reviewing glucose analytics and recent activity.</p>
        <Link
          to="/pets"
          className="mt-4 inline-flex rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500"
        >
          Add a pet
        </Link>
      </section>
    )
  }

  return (
    <div className="space-y-6">
      <section className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="text-sm font-medium uppercase tracking-wide text-indigo-600">Care dashboard</p>
            <h1 className="mt-2 text-3xl font-semibold text-slate-900">{selectedPet.name}&rsquo;s glucose overview</h1>
            <p className="mt-2 max-w-2xl text-sm text-slate-600">
              Review recent trends, range distribution, and the latest logged readings without leaving the dashboard.
            </p>
          </div>
          <div className="w-full max-w-xs">
            <GlucosePetSelector
              pets={pets}
              selectedPetId={selectedPet.id}
              onChange={(petId) => setSearchParams(new URLSearchParams({ petId: petId.toString(), days: selectedDays.toString() }))}
            />
          </div>
        </div>
        <div className="mt-5 flex flex-wrap items-center gap-2">
          {glucoseRangeOptions.map((days) => (
            <button
              key={days}
              type="button"
              onClick={() => setSearchParams(new URLSearchParams({ petId: selectedPet.id.toString(), days: days.toString() }))}
              className={`rounded-md px-3 py-1.5 text-sm font-medium transition ${
                selectedDays === days ? 'bg-indigo-600 text-white' : 'border border-slate-300 text-slate-700 hover:bg-slate-50'
              }`}
            >
              Last {days} days
            </button>
          ))}
          <Link
            to={`/glucose?petId=${selectedPet.id}&days=${selectedDays}`}
            className="ml-auto rounded-md border border-indigo-200 bg-indigo-50 px-3 py-1.5 text-sm font-medium text-indigo-700 transition hover:bg-indigo-100"
          >
            Open glucose tracker
          </Link>
        </div>
      </section>
      <GlucoseAnalyticsCards analytics={glucoseAnalyticsQuery.data} isLoading={glucoseAnalyticsQuery.isLoading} />
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
