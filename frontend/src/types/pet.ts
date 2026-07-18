export interface Pet {
  id: number
  userId: number
  name: string
  species: string
  breed: string | null
  ageYears: number
  weightKg: number | null
  diabetesType: string
  medicalNotes: string | null
  createdAt: string
  updatedAt: string
  isActive: boolean
}

export interface CreatePetRequest {
  name: string
  species: string
  breed?: string
  ageYears: number
  weightKg?: number
  diabetesType: string
  medicalNotes?: string
}

export interface UpdatePetRequest {
  name: string
  breed?: string
  ageYears: number
  weightKg?: number
  diabetesType: string
  medicalNotes?: string
}

export interface PageResponse<T> {
  content: T[]
  totalPages: number
  totalElements: number
  number: number
  size: number
  numberOfElements: number
  first: boolean
  last: boolean
  empty: boolean
}

export interface PetsQueryParams {
  page: number
  size: number
  sortBy?: string
  direction?: 'ASC' | 'DESC'
}
