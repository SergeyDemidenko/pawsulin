import { apiClient } from './apiClient'
import type { CreatePetRequest, PageResponse, Pet, PetsQueryParams, UpdatePetRequest } from '../types/pet'

const petsBasePath = '/v1/pets'

export async function getPets(params: PetsQueryParams): Promise<PageResponse<Pet>> {
  const response = await apiClient.get<PageResponse<Pet>>(petsBasePath, { params })
  return response.data
}

export async function getPetById(petId: number): Promise<Pet> {
  const response = await apiClient.get<Pet>(`${petsBasePath}/${petId}`)
  return response.data
}

export async function createPet(request: CreatePetRequest): Promise<Pet> {
  const response = await apiClient.post<Pet>(petsBasePath, request)
  return response.data
}

export async function updatePet(petId: number, request: UpdatePetRequest): Promise<Pet> {
  const response = await apiClient.put<Pet>(`${petsBasePath}/${petId}`, request)
  return response.data
}

export async function deletePet(petId: number): Promise<void> {
  await apiClient.delete(`${petsBasePath}/${petId}`)
}
