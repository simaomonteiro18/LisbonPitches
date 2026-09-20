const STORAGE_KEY = 'pitchbooking_session'

export function saveSession(token, user) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ token, user }))
}

export function getSession() {
  const raw = localStorage.getItem(STORAGE_KEY)
  return raw ? JSON.parse(raw) : null
}

export function clearSession() {
  localStorage.removeItem(STORAGE_KEY)
}
