import { authHeaders, parseErrorMessage } from './http'

const API_URL = import.meta.env.VITE_API_URL

export async function getInvitationsByReservation(reservationId) {
  const response = await fetch(`${API_URL}/invitations?reservationId=${reservationId}`, {
    headers: { ...authHeaders() },
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }

  return response.json()
}

export async function getMyInvitations() {
  const response = await fetch(`${API_URL}/invitations/me`, {
    headers: { ...authHeaders() },
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }

  return response.json()
}

export async function createInvitation({ reservationId, identifier }) {
  const response = await fetch(`${API_URL}/invitations`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify({ reservationId, identifier }),
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }

  return response.json()
}

export async function acceptInvitation(id) {
  const response = await fetch(`${API_URL}/invitations/${id}/accept`, {
    method: 'PATCH',
    headers: { ...authHeaders() },
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }

  return response.json()
}

export async function rejectInvitation(id) {
  const response = await fetch(`${API_URL}/invitations/${id}/reject`, {
    method: 'PATCH',
    headers: { ...authHeaders() },
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }

  return response.json()
}

export async function cancelInvitation(id) {
  const response = await fetch(`${API_URL}/invitations/${id}`, {
    method: 'DELETE',
    headers: { ...authHeaders() },
  })

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response))
  }
}
