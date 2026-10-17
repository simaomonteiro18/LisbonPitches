import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { AnimatePresence, motion } from 'motion/react'
import { clearSession, getSession } from '../auth'
import { getMyReservations } from '../api/reservations'
import { acceptInvitation, getMyInvitations, rejectInvitation } from '../api/invitations'
import { durations, easings } from '../motion'
import './Perfil.css'

const ESTADOS = {
  PENDING: 'Pendente',
  ACCEPTED: 'Aceite',
  REJECTED: 'Rejeitado',
}

const TABS = [
  { valor: 'perfil', texto: 'Perfil' },
  { valor: 'reservas', texto: 'As minhas reservas' },
  { valor: 'convites', texto: 'Convites' },
]

const trocaTab = {
  initial: { opacity: 0, y: 8 },
  animate: { opacity: 1, y: 0, transition: { duration: durations.base, ease: easings.out } },
  exit: { opacity: 0, transition: { duration: durations.fast, ease: easings.out } },
}

function Esqueletos({ quantos }) {
  return (
    <div className="perfil__esqueletos" aria-hidden="true">
      {Array.from({ length: quantos }, (_, i) => (
        <span key={i} className="perfil__esqueleto" />
      ))}
    </div>
  )
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

  const [convites, setConvites] = useState(null)
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
  const pendentes = convites?.filter((c) => c.status === 'PENDING').length ?? 0

  return (
    <section className="perfil">
      <div className="container perfil__inner">
        <h1>O teu perfil</h1>

        <div className="perfil__tabs">
          {TABS.map((opcao) => (
            <button
              key={opcao.valor}
              className={tab === opcao.valor ? 'active' : ''}
              onClick={() => setTab(opcao.valor)}
              type="button"
              aria-pressed={tab === opcao.valor}
            >
              {opcao.texto}
              {opcao.valor === 'convites' && pendentes > 0 && (
                <span className="perfil__badge-count">{pendentes}</span>
              )}
              {tab === opcao.valor && (
                <motion.span
                  layoutId="perfil-tab-indicador"
                  className="perfil__tab-indicador"
                  transition={{ type: 'spring', stiffness: 500, damping: 40 }}
                />
              )}
            </button>
          ))}
        </div>

        <AnimatePresence mode="wait" initial={false}>
          {tab === 'perfil' && (
            <motion.div key="perfil" {...trocaTab}>
              <div className="perfil__card">
                <div className="perfil__campo">
                  <span>Nome</span>
                  <strong>{user.name}</strong>
                </div>
                <div className="perfil__campo">
                  <span>Username</span>
                  <strong>{user.username}</strong>
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
            </motion.div>
          )}

          {tab === 'reservas' && (
            <motion.div key="reservas" className="perfil__reservas" {...trocaTab}>
              {reservasError && <p className="perfil__status perfil__status--error">{reservasError}</p>}

              <h2>Organizadas por mim</h2>
              {!reservas && !reservasError && <Esqueletos quantos={2} />}
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
              {!reservas && !reservasError && <Esqueletos quantos={2} />}
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
            </motion.div>
          )}

          {tab === 'convites' && (
            <motion.div key="convites" className="perfil__convites" {...trocaTab}>
              {convitesError && <p className="perfil__status perfil__status--error">{convitesError}</p>}
              {actionError && <p className="perfil__status perfil__status--error">{actionError}</p>}

              {!convites && !convitesError && <Esqueletos quantos={2} />}
              {convites?.length === 0 && <p className="perfil__status">Não tens convites.</p>}

              <ul className="perfil__lista-convites">
                {convites?.map((c) => (
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
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </section>
  )
}

export default Perfil
