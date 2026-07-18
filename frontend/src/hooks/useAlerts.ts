import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createAlert,
  deleteAlert,
  getAlertsByPet,
  updateAlert,
} from '../services/alertService'
import type { AlertsQueryParams, CreateGlucoseAlertRequest, UpdateGlucoseAlertRequest } from '../types/alert'

function isValidPetId(petId: number): boolean {
  return Number.isFinite(petId) && petId > 0
}

export function useAlertsByPet(petId: number, params: AlertsQueryParams) {
  return useQuery({
    queryKey: ['alerts', petId, params],
    queryFn: () => getAlertsByPet(petId, params),
    enabled: isValidPetId(petId),
    placeholderData: (previousData) => previousData,
  })
}

export function useCreateAlertMutation(petId: number) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: CreateGlucoseAlertRequest) => createAlert(petId, request),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['alerts', petId] })
    },
  })
}

export function useUpdateAlertMutation(petId: number, alertId: number) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: UpdateGlucoseAlertRequest) => updateAlert(petId, alertId, request),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['alerts', petId] })
    },
  })
}

export function useDeleteAlertMutation(petId: number) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (alertId: number) => deleteAlert(petId, alertId),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['alerts', petId] })
    },
  })
}
