import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { clearSession, getSession } from '../auth'
import { getMyReservations } from '../api/reservations'
import { acceptInvitation, getMyInvitations, rejectInvitation } from '../api/invitations'
import './Perfil.css'

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

function Perfil() {
  const navigate = useNavigate()
  const [session, setSession] = useState(null)
  const [tab, setTab] = useState('perfil')

  const [reservas, setReservas] = useState(null)
  const [reservasError, setReservasError] = useState(null)

  const [convites, setConvites] = useState([])
  const [convitesError, setConvitesError] = useState(null)
  const [actionError, setActionError] = useState(null)

  useEffect(() => {
    const current = getSession()
    if (!current) {
      navigate('/login')
      return
    }
    setSession(current)

    getMyReservations(current.user.id)
      .then(setReservas)
      .catch((err) => setReservasError(err.message))

    carregarConvites()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [navigate])

  function carregarConvites() {
    getMyInvitations()
      .then(setConvites)
      .catch((err) => setConvitesError(err.message))
  }

  function handleLogout() {
    clearSession()
    navigate('/login')
  }

  async function handleAceitar(id) {
    setActionError(null)
    try {
      await acceptInvitation(id)
      carregarConvites()
    } catch (err) {
      setActionError(err.message)
    }
  }

  async function handleRejeitar(id) {
    setActionError(null)
    try {
      await rejectInvitation(id)
      carregarConvites()
    } catch (err) {
      setActionError(err.message)
    }
  }

  if (!session) {
    return null
  }

  const { user } = session
  const pendentes = convites.filter((c) => c.status === 'PENDING').length

  return (
    <section className="perfil">
      <div className="container perfil__inner">
        <h1>O teu perfil</h1>

        <div className="perfil__tabs">
          <button className={tab === 'perfil' ? 'active' : ''} onClick={() => setTab('perfil')} type="button">
            Perfil
          </button>
          <button className={tab === 'reservas' ? 'active' : ''} onClick={() => setTab('reservas')} type="button">
            As minhas reservas
          </button>
          <button className={tab === 'convites' ? 'active' : ''} onClick={() => setTab('convites')} type="button">
            Convites
            {pendentes > 0 && <span className="perfil__badge-count">{pendentes}</span>}
          </button>
        </div>

        {tab === 'perfil' && (
          <>
            <div className="perfil__card">
              <div className="perfil__campo">
                <span>Nome</span>
                <strong>{user.name}</strong>
              </div>
              <div className="perfil__campo">
                <span>Email</span>
                <strong>{user.email}</strong>
              </div>
              <div className="perfil__campo">
                <span>Telemóvel</span>
                <strong>{user.phone}</strong>
              </div>
              <div className="perfil__campo">
                <span>Cidade</span>
                <strong>{user.city}</strong>
              </div>
            </div>

            <button onClick={handleLogout} className="btn-ghost">
              Terminar sessão
            </button>
          </>
        )}

        {tab === 'reservas' && (
          <div className="perfil__reservas">
            {reservasError && <p className="perfil__status perfil__status--error">{reservasError}</p>}

            <h2>Organizadas por mim</h2>
            {reservas?.organized.length === 0 && <p className="perfil__status">Ainda não organizaste reservas.</p>}
            <ul className="perfil__lista-reservas">
              {reservas?.organized.map((r) => (
                <li key={r.id}>
                  <Link to={`/reservas/${r.id}`}>
                    <strong>{r.pitch.name}</strong>
                    <span>{formatarData(r.startTime)}</span>
                  </Link>
                </li>
              ))}
            </ul>

            <h2>Reservas em que participo</h2>
            {reservas?.participating.length === 0 && <p className="perfil__status">Ainda não participas em reservas.</p>}
            <ul className="perfil__lista-reservas">
              {reservas?.participating.map((r) => (
                <li key={r.id}>
                  <Link to={`/reservas/${r.id}`}>
                    <strong>{r.pitch.name}</strong>
                    <span>{formatarData(r.startTime)}</span>
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        )}

        {tab === 'convites' && (
          <div className="perfil__convites">
            {convitesError && <p className="perfil__status perfil__status--error">{convitesError}</p>}
            {actionError && <p className="perfil__status perfil__status--error">{actionError}</p>}

            {convites.length === 0 && <p className="perfil__status">Não tens convites.</p>}

            <ul className="perfil__lista-convites">
              {convites.map((c) => (
                <li key={c.id} className="convite">
                  <Link to={`/reservas/${c.reservationId}`} className="convite__link">
                    Ver reserva
                  </Link>
                  <span className={`badge convite__estado convite__estado--${c.status.toLowerCase()}`}>
                    {ESTADOS[c.status] ?? c.status}
                  </span>

                  {c.status === 'PENDING' && (
                    <div className="convite__acoes">
                      <button type="button" className="btn-primary" onClick={() => handleAceitar(c.id)}>
                        Aceitar
                      </button>
                      <button type="button" className="btn-ghost" onClick={() => handleRejeitar(c.id)}>
                        Rejeitar
                      </button>
                    </div>
                  )}
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </section>
  )
}

export default Perfil
