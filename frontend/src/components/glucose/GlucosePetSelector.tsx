import type { Pet } from '../../types/pet'

interface GlucosePetSelectorProps {
  pets: Pet[]
  selectedPetId: number
  onChange: (petId: number) => void
}

export function GlucosePetSelector({ pets, selectedPetId, onChange }: GlucosePetSelectorProps) {
  return (
    <label className="block">
      <span className="mb-1 block text-sm font-medium text-slate-700">Pet</span>
      <select
        value={selectedPetId}
        onChange={(event) => onChange(Number(event.target.value))}
        className="w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
      >
        {pets.map((pet) => (
          <option key={pet.id} value={pet.id}>
            {pet.name}
          </option>
        ))}
      </select>
    </label>
  )
}
