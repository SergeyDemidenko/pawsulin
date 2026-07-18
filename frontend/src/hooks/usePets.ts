import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createPet, deletePet, getPetById, getPets, updatePet } from '../services/petService'
import type { CreatePetRequest, PetsQueryParams, UpdatePetRequest } from '../types/pet'

export function usePets(params: PetsQueryParams) {
  return useQuery({
    queryKey: ['pets', params],
    queryFn: () => getPets(params),
    placeholderData: (previousData) => previousData,
  })
}

export function usePet(petId: number) {
  return useQuery({
    queryKey: ['pets', petId],
    queryFn: () => getPetById(petId),
    enabled: Number.isFinite(petId),
  })
}

export function useCreatePetMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: CreatePetRequest) => createPet(request),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['pets'] })
    },
  })
}

export function useUpdatePetMutation(petId: number) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: UpdatePetRequest) => updatePet(petId, request),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['pets'] })
      void queryClient.invalidateQueries({ queryKey: ['pets', petId] })
    },
  })
}

export function useDeletePetMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (petId: number) => deletePet(petId),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['pets'] })
    },
  })
}
