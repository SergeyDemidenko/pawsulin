import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createGlucoseReading,
  getGlucoseAnalytics,
  getGlucoseReadings,
  getGlucoseReadingsByRange,
} from '../services/glucoseService'
import type {
  CreateGlucoseReadingRequest,
  GlucoseHistoryQueryParams,
  GlucoseRangeQueryParams,
} from '../types/glucose'

function isValidPetId(petId: number): boolean {
  return Number.isFinite(petId) && petId > 0
}

export function useGlucoseHistory(petId: number, params: GlucoseHistoryQueryParams) {
  return useQuery({
    queryKey: ['glucose', petId, 'history', params],
    queryFn: () => getGlucoseReadings(petId, params),
    enabled: isValidPetId(petId),
    placeholderData: (previousData) => previousData,
  })
}

export function useGlucoseRange(petId: number, params: GlucoseRangeQueryParams) {
  return useQuery({
    queryKey: ['glucose', petId, 'range', params],
    queryFn: () => getGlucoseReadingsByRange(petId, params),
    enabled: isValidPetId(petId),
  })
}

export function useGlucoseAnalytics(petId: number, params: GlucoseRangeQueryParams) {
  return useQuery({
    queryKey: ['glucose', petId, 'analytics', params],
    queryFn: () => getGlucoseAnalytics(petId, params),
    enabled: isValidPetId(petId),
  })
}

export function useCreateGlucoseReadingMutation(petId: number) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: CreateGlucoseReadingRequest) => createGlucoseReading(petId, request),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['glucose', petId] })
    },
  })
}
