import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { getReservationById } from '../api/reservations'
import {
  acceptInvitation,
  cancelInvitation,
  createInvitation,
  getInvitationsByReservation,
  rejectInvitation,
} from '../api/invitations'
import { getSession } from '../auth'
import './Reserva.css'

const ESTADOS = {
  PENDING: 'Pendente',
  ACCEPTED: 'Aceite',
  REJECTED: 'Rejeitado',
}

function formatarData(value) {
  return new Date(value).toLocaleString('pt-PT', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function Reserva() {
  const { id } = useParams()
  const navigate = useNavigate()
  const session = getSession()

  const [reservation, setReservation] = useState(null)
  const [invitations, setInvitations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const [identifier, setIdentifier] = useState('')
  const [convidarLoading, setConvidarLoading] = useState(false)
  const [convidarError, setConvidarError] = useState(null)

  const [actionError, setActionError] = useState(null)

  function carregar() {
    setLoading(true)
    setError(null)

    Promise.all([getReservationById(id), getInvitationsByReservation(id)])
      .then(([reservationData, invitationsData]) => {
        setReservation(reservationData)
        setInvitations(invitationsData)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    if (!session) {
      navigate('/login')
      return
    }
    carregar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id])

  if (loading) {
    return (
      <section className="reserva">
        <div className="container">
          <p className="reserva__status">A carregar...</p>
        </div>
      </section>
    )
  }

  if (error || !reservation) {
    return (
      <section className="reserva">
        <div className="container">
          <p className="reserva__status reserva__status--error">{error ?? 'Reserva não encontrada.'}</p>
        </div>
      </section>
    )
  }

  const userId = session.user.id
  const isOrganizer = reservation.organizerId === userId
  const isAcceptedGuest = invitations.some((i) => i.guestId === userId && i.status === 'ACCEPTED')
  const podeConvidar = isOrganizer || isAcceptedGuest

  async function handleConvidar(e) {
    e.preventDefault()
    setConvidarLoading(true)
    setConvidarError(null)

    try {
      await createInvitation({ reservationId: Number(id), identifier })
      setIdentifier('')
      carregar()
    } catch (err) {
      setConvidarError(err.message)
    } finally {
      setConvidarLoading(false)
    }
  }

  async function handleAceitar(invitationId) {
    setActionError(null)
    try {
      await acceptInvitation(invitationId)
      carregar()
    } catch (err) {
      setActionError(err.message)
    }
  }

  async function handleRejeitar(invitationId) {
    setActionError(null)
    try {
      await rejectInvitation(invitationId)
      carregar()
    } catch (err) {
      setActionError(err.message)
    }
  }

  async function handleCancelar(invitationId) {
    setActionError(null)
    try {
      await cancelInvitation(invitationId)
      carregar()
    } catch (err) {
      setActionError(err.message)
    }
  }

  return (
    <section className="reserva">
      <div className="container reserva__inner">
        <h1>{reservation.pitch.name}</h1>
        <p className="reserva__city">{reservation.pitch.city}</p>

        <div className="reserva__card">
          <div className="reserva__campo">
            <span>Organizador</span>
            <strong>{reservation.organizerName}</strong>
          </div>
          <div className="reserva__campo">
            <span>Início</span>
            <strong>{formatarData(reservation.startTime)}</strong>
          </div>
          <div className="reserva__campo">
            <span>Fim</span>
            <strong>{formatarData(reservation.endTime)}</strong>
          </div>
          <div className="reserva__campo">
            <span>Preço por pessoa</span>
            <strong>{reservation.pricePerPerson} EUR</strong>
          </div>
          <div className="reserva__campo">
            <span>Convites aceites</span>
            <strong>{reservation.invitesAccepted}</strong>
          </div>
        </div>

        <h2>Convites</h2>

        {actionError && <p className="reserva__status reserva__status--error">{actionError}</p>}

        {invitations.length === 0 && <p className="reserva__status">Ainda não há convites.</p>}

        <ul className="reserva__convites">
          {invitations.map((invitation) => {
            const podeGerir = invitation.status === 'PENDING' && invitation.guestId === userId
            const podeCancelar = isOrganizer || invitation.invitedById === userId

            return (
              <li key={invitation.id} className="convite">
                <span className="convite__nome">{invitation.guestName}</span>
                <span className={`badge convite__estado convite__estado--${invitation.status.toLowerCase()}`}>
                  {ESTADOS[invitation.status] ?? invitation.status}
                </span>

                <div className="convite__acoes">
                  {podeGerir && (
                    <>
                      <button type="button" className="btn-primary" onClick={() => handleAceitar(invitation.id)}>
                        Aceitar
                      </button>
                      <button type="button" className="btn-ghost" onClick={() => handleRejeitar(invitation.id)}>
                        Rejeitar
                      </button>
                    </>
                  )}
                  {podeCancelar && (
                    <button type="button" className="btn-ghost" onClick={() => handleCancelar(invitation.id)}>
                      Cancelar
                    </button>
                  )}
                </div>
              </li>
            )
          })}
        </ul>

        {podeConvidar && (
          <form className="reserva__convidar" onSubmit={handleConvidar}>
            <input
              type="text"
              placeholder="Username ou email"
              value={identifier}
              onChange={(e) => setIdentifier(e.target.value)}
              required
            />
            <button type="submit" className="btn-primary" disabled={convidarLoading}>
              {convidarLoading ? 'A convidar...' : 'Convidar'}
            </button>
            {convidarError && <p className="reserva__status reserva__status--error">{convidarError}</p>}
          </form>
        )}
      </div>
    </section>
  )
}

export default Reserva
