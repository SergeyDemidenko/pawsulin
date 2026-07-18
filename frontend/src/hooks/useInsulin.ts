import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createInsulinLog,
  getInsulinLogs,
  getInsulinLogsByRange,
} from '../services/insulinService'
import type {
  CreateInsulinLogRequest,
  InsulinHistoryQueryParams,
  InsulinRangeQueryParams,
} from '../types/insulin'

function isValidPetId(petId: number): boolean {
  return Number.isFinite(petId) && petId > 0
}

export function useInsulinHistory(petId: number, params: InsulinHistoryQueryParams) {
  return useQuery({
    queryKey: ['insulin', petId, 'history', params],
    queryFn: () => getInsulinLogs(petId, params),
    enabled: isValidPetId(petId),
    placeholderData: (previousData) => previousData,
  })
}

export function useInsulinRange(petId: number, params: InsulinRangeQueryParams) {
  return useQuery({
    queryKey: ['insulin', petId, 'range', params],
    queryFn: () => getInsulinLogsByRange(petId, params),
    enabled: isValidPetId(petId),
  })
}

export function useCreateInsulinLogMutation(petId: number) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: CreateInsulinLogRequest) => createInsulinLog(petId, request),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['insulin', petId] })
    },
  })
}
