import { getSession } from '../auth'

export function authHeaders() {
  const session = getSession()
  return session ? { Authorization: `Bearer ${session.token}` } : {}
}

export async function parseErrorMessage(response) {
  try {
    const data = await response.json()
    return data.message || 'Ocorreu um erro. Tenta outra vez.'
  } catch {
    return 'Ocorreu um erro. Tenta outra vez.'
  }
}
