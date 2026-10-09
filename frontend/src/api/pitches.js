const API_URL = import.meta.env.VITE_API_URL

export async function searchPitches({ name, city, pitchAccess, page, size, sort = [] } = {}) {
  const params = new URLSearchParams()
  if (name) params.set('name', name)
  if (city) params.set('city', city)
  if (pitchAccess) params.set('pitchAccess', pitchAccess)
  if (page != null) params.set('page', page)
  if (size != null) params.set('size', size)
  sort.forEach((rule) => params.append('sort', rule))

  const query = params.toString()
  const response = await fetch(`${API_URL}/pitches${query ? `?${query}` : ''}`)

  if (!response.ok) {
    throw new Error('Não foi possível obter os campos.')
  }

  return response.json()
}

export async function getFeaturedPitches() {
  const response = await fetch(`${API_URL}/pitches/featured`)

  if (!response.ok) {
    throw new Error('Não foi possível obter os campos em destaque.')
  }

  return response.json()
}

export async function getAllPitchesForMap() {
  const response = await fetch(`${API_URL}/pitches/map`)

  if (!response.ok) {
    throw new Error('Não foi possível obter os campos para o mapa.')
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
