import { useState } from 'react'
import { Link, Navigate, useNavigate, useParams } from 'react-router-dom'
import { PetDetail } from '../components/pet/PetDetail'
import { PetForm } from '../components/pet/PetForm'
import { useDeletePetMutation, usePet, useUpdatePetMutation } from '../hooks/usePets'
import type { UpdatePetRequest } from '../types/pet'
import { extractApiErrorMessage } from '../utils/apiError'

export function PetDetailPage() {
  const navigate = useNavigate()
  const params = useParams()
  const petId = Number(params.petId)
  const { data: pet, isLoading, isError } = usePet(petId)
  const updatePetMutation = useUpdatePetMutation(petId)
  const deletePetMutation = useDeletePetMutation()
  const [submitError, setSubmitError] = useState<string | null>(null)
  const [deleteError, setDeleteError] = useState<string | null>(null)

  if (!Number.isFinite(petId)) {
    return <Navigate to="/pets" replace />
  }

  const handleUpdate = async (request: UpdatePetRequest) => {
    setSubmitError(null)

    try {
      await updatePetMutation.mutateAsync(request)
    } catch (requestError) {
      setSubmitError(extractApiErrorMessage(requestError, 'Unable to update pet. Please try again.'))
    }
  }

  const handleDelete = async () => {
    setDeleteError(null)

    if (!window.confirm('Delete this pet? You can add it again later if needed.')) {
      return
    }

    try {
      await deletePetMutation.mutateAsync(petId)
      navigate('/pets', { replace: true })
    } catch (requestError) {
      setDeleteError(extractApiErrorMessage(requestError, 'Unable to delete pet. Please try again.'))
    }
  }

  if (isLoading) {
    return <p className="text-sm text-slate-600">Loading pet details…</p>
  }

  if (isError || !pet) {
    return (
      <section className="rounded-xl border border-red-200 bg-red-50 p-6">
        <h1 className="text-xl font-semibold text-red-900">Pet not found</h1>
        <p className="mt-2 text-sm text-red-700">The requested pet could not be loaded.</p>
        <Link className="mt-4 inline-flex text-sm font-medium text-red-800 underline" to="/pets">
          Back to pets
        </Link>
      </section>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <Link className="text-sm font-medium text-indigo-600 hover:text-indigo-500" to="/pets">
            ← Back to pets
          </Link>
          <p className="mt-2 text-sm text-slate-600">Review, update, or delete this pet profile.</p>
        </div>
        <button
          type="button"
          onClick={handleDelete}
          disabled={deletePetMutation.isPending}
          className="rounded-md bg-red-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-red-500 disabled:cursor-not-allowed disabled:bg-red-400"
        >
          {deletePetMutation.isPending ? 'Deleting…' : 'Delete pet'}
        </button>
      </div>
      {deleteError ? <p className="text-sm text-red-600">{deleteError}</p> : null}
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[minmax(0,1.1fr)_minmax(0,0.9fr)]">
        <PetDetail pet={pet} />
        <PetForm
          initialPet={pet}
          includeSpeciesField={false}
          submitLabel="Save changes"
          isSubmitting={updatePetMutation.isPending}
          error={submitError}
          onSubmit={(request) => handleUpdate(request as UpdatePetRequest)}
        />
      </div>
    </div>
  )
}
