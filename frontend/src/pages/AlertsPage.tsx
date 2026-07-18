import { useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { AlertConfigForm } from '../components/alerts/AlertConfigForm'
import { AlertConfigList } from '../components/alerts/AlertConfigList'
import { GlucosePetSelector } from '../components/glucose/GlucosePetSelector'
import { useAlertsByPet, useCreateAlertMutation, useDeleteAlertMutation } from '../hooks/useAlerts'
import { usePets } from '../hooks/usePets'
import type { CreateGlucoseAlertRequest } from '../types/alert'
import { extractApiErrorMessage } from '../utils/apiError'

const defaultPetPageSize = 50
const alertsPageSize = 20

export function AlertsPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [submitError, setSubmitError] = useState<string | null>(null)

  const petsQuery = usePets({ page: 0, size: defaultPetPageSize, sortBy: 'name', direction: 'ASC' })
  const pets = useMemo(() => petsQuery.data?.content ?? [], [petsQuery.data?.content])

  const selectedPetId = Number(searchParams.get('petId'))
  const effectivePetId = pets.some((pet) => pet.id === selectedPetId) ? selectedPetId : (pets[0]?.id ?? Number.NaN)
  const selectedPet = pets.find((pet) => pet.id === effectivePetId)

  const alertsQuery = useAlertsByPet(effectivePetId, { page: 0, size: alertsPageSize, sortBy: 'createdAt', direction: 'DESC' })
  const createAlertMutation = useCreateAlertMutation(effectivePetId)
  const deleteAlertMutation = useDeleteAlertMutation(effectivePetId)

  const handlePetChange = (petId: number) => {
    setSearchParams(new URLSearchParams({ petId: petId.toString() }))
  }

  const handleCreate = async (request: CreateGlucoseAlertRequest) => {
    setSubmitError(null)
    try {
      await createAlertMutation.mutateAsync(request)
    } catch (err) {
      setSubmitError(extractApiErrorMessage(err, 'Unable to save alert. Please try again.'))
    }
  }

  const handleDelete = async (alertId: number) => {
    try {
      await deleteAlertMutation.mutateAsync(alertId)
    } catch (err) {
      setSubmitError(extractApiErrorMessage(err, 'Unable to delete alert. Please try again.'))
    }
  }

  if (petsQuery.isLoading) {
    return <p className="text-sm text-slate-600">Loading alerts workspace…</p>
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
        <h1 className="text-2xl font-semibold text-slate-900">Alerts</h1>
        <p className="mt-2 text-sm text-slate-600">Create a pet profile before configuring glucose alerts.</p>
        <Link
          to="/pets"
          className="mt-4 inline-flex rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500"
        >
          Go to pet management
        </Link>
      </section>
    )
  }

  const alerts = alertsQuery.data?.content ?? []

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-3xl font-semibold text-slate-900">Alerts</h1>
          <p className="mt-2 text-sm text-slate-600">
            Configure glucose thresholds for {selectedPet.name}. You'll receive real-time notifications when a reading
            exceeds these limits.
          </p>
        </div>
        <div className="w-full max-w-xs">
          <GlucosePetSelector pets={pets} selectedPetId={selectedPet.id} onChange={handlePetChange} />
        </div>
      </div>

      <AlertConfigForm
        petName={selectedPet.name}
        isSubmitting={createAlertMutation.isPending}
        error={submitError}
        onSubmit={handleCreate}
      />

      <section>
        <h2 className="mb-3 text-lg font-semibold text-slate-900">Active alerts</h2>
        <AlertConfigList
          alerts={alerts}
          isLoading={alertsQuery.isLoading}
          onDelete={handleDelete}
          isDeleting={deleteAlertMutation.isPending}
        />
      </section>
    </div>
  )
}
