import { authHeaders, parseErrorMessage } from './http'

const API_URL = import.meta.env.VITE_API_URL

export async function createReservation({ pitchId, startTime, endTime }) {
  const response = await fetch(`${API_URL}/reservations`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify({ pitchId, startTime, endTime }),
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }

  return response.json()
}

export async function getReservationById(id) {
  const response = await fetch(`${API_URL}/reservations/${id}`, {
    headers: { ...authHeaders() },
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }

  return response.json()
}

export async function getMyReservations(userId) {
  const response = await fetch(`${API_URL}/reservations?userId=${userId}`, {
    headers: { ...authHeaders() },
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }

  return response.json()
}
