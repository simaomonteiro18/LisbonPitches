const API_URL = import.meta.env.VITE_API_URL

export async function searchPitches({ name, city } = {}) {
  const params = new URLSearchParams()
  if (name) params.set('name', name)
  if (city) params.set('city', city)

  const query = params.toString()
  const response = await fetch(`${API_URL}/pitches${query ? `?${query}` : ''}`)

  if (!response.ok) {
    throw new Error('Não foi possível obter os campos.')
  }

  return response.json()
}

export async function getPitchById(id) {
  const response = await fetch(`${API_URL}/pitches/${id}`)

  if (!response.ok) {
    throw new Error('Não foi possível obter o campo.')
  }

  return response.json()
}
