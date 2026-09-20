const API_URL = import.meta.env.VITE_API_URL

async function parseErrorMessage(response) {
  try {
    const data = await response.json()
    return data.message || 'Ocorreu um erro. Tenta outra vez.'
  } catch {
    return 'Ocorreu um erro. Tenta outra vez.'
  }
}

export async function registerUser({ name, email, password, phone, city }) {
  const response = await fetch(`${API_URL}/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, email, password, phone, city }),
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }

  return response.json()
}

export async function loginUser({ email, password }) {
  const response = await fetch(`${API_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }

  return response.json()
}
