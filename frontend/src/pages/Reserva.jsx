import { useEffect, useRef, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { AnimatePresence, motion, useAnimate, useReducedMotion } from 'motion/react'
import { getReservationById } from '../api/reservations'
import {
  acceptInvitation,
  cancelInvitation,
  createInvitation,
  getInvitationsByReservation,
  rejectInvitation,
} from '../api/invitations'
import { getSession } from '../auth'
import { durations, easings } from '../motion'
import CheckAnimado from '../components/CheckAnimado'
import './Reserva.css'

const ESTADOS = {
  PENDING: 'Pendente',
  ACCEPTED: 'Aceite',
  REJECTED: 'Rejeitado',
}

const aparecer = {
  escondido: { opacity: 0, y: 24 },
  visivel: (atraso = 0) => ({
    opacity: 1,
    y: 0,
    transition: { duration: durations.slow, ease: easings.out, delay: atraso },
  }),
}

const aviso = {
  initial: { opacity: 0, y: -4 },
  animate: { opacity: 1, y: 0 },
  exit: { opacity: 0 },
  transition: { duration: durations.fast },
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

function Esqueleto() {
  return (
    <section className="reserva" aria-busy="true">
      <div className="container reserva__inner" aria-hidden="true">
        <span className="reserva__esqueleto reserva__esqueleto--titulo" />
        <span className="reserva__esqueleto reserva__esqueleto--cidade" />
        <div className="reserva__esqueleto reserva__esqueleto--cartao" />
        <span className="reserva__esqueleto reserva__esqueleto--subtitulo" />
        <div className="reserva__esqueleto reserva__esqueleto--convite" />
        <div className="reserva__esqueleto reserva__esqueleto--convite" />
      </div>
    </section>
  )
}

function Reserva() {
  const { id } = useParams()
  const navigate = useNavigate()
  const location = useLocation()
  const session = getSession()
  const reduzir = useReducedMotion()
  const inicio = reduzir ? false : 'escondido'

  // so mostra a confirmacao a quem chega acabado de reservar, nao ao atualizar a pagina
  const [acabadaDeCriar] = useState(() => Boolean(location.state?.acabadaDeCriar))

  const [reservation, setReservation] = useState(null)
  const [invitations, setInvitations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const [identifier, setIdentifier] = useState('')
  const [convidarLoading, setConvidarLoading] = useState(false)
  const [convidarError, setConvidarError] = useState(null)
  const [conviteEnviado, setConviteEnviado] = useState(false)

  const [actionError, setActionError] = useState(null)

  const enviadoTimerRef = useRef(null)
  const [formConvidar, animarFormConvidar] = useAnimate()

  // depois da primeira carga, atualiza sem voltar ao esqueleto para a lista poder animar
  function carregar({ silencioso = false } = {}) {
    if (!silencioso) setLoading(true)
    setError(null)

    return Promise.all([getReservationById(id), getInvitationsByReservation(id)])
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

  useEffect(() => {
    if (acabadaDeCriar) {
      navigate(location.pathname, { replace: true, state: null })
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  useEffect(() => () => clearTimeout(enviadoTimerRef.current), [])

  if (loading) {
    return <Esqueleto />
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
    setConviteEnviado(false)
    clearTimeout(enviadoTimerRef.current)

    try {
      await createInvitation({ reservationId: Number(id), identifier })
      setIdentifier('')
      setConviteEnviado(true)
      enviadoTimerRef.current = setTimeout(() => setConviteEnviado(false), 2500)
      carregar({ silencioso: true })
    } catch (err) {
      setConvidarError(err.message)
      if (!reduzir && formConvidar.current) {
        animarFormConvidar(formConvidar.current, { x: [0, -10, 10, -6, 6, 0] }, { duration: 0.45 })
      }
    } finally {
      setConvidarLoading(false)
    }
  }

  async function executarAcao(acao, invitationId) {
    setActionError(null)
    try {
      await acao(invitationId)
      carregar({ silencioso: true })
    } catch (err) {
      setActionError(err.message)
    }
  }

  const linhas = [
    { rotulo: 'Organizador', valor: reservation.organizerName },
    { rotulo: 'Início', valor: formatarData(reservation.startTime) },
    { rotulo: 'Fim', valor: formatarData(reservation.endTime) },
    { rotulo: 'Preço por pessoa', valor: `${reservation.pricePerPerson} EUR` },
    { rotulo: 'Convites aceites', valor: reservation.invitesAccepted },
  ]
  const atrasoBase = acabadaDeCriar ? 0.35 : 0.1
  const atrasoConvites = atrasoBase + 0.2 + linhas.length * 0.07

  return (
    <section className="reserva">
      <div className="container reserva__inner">
        {acabadaDeCriar && (
          <motion.div
            className="reserva__confirmada"
            role="status"
            initial={reduzir ? false : { opacity: 0, y: -16, scale: 0.97 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            transition={{ duration: durations.slow, ease: easings.out }}
          >
            <CheckAnimado tamanho={44} atraso={0.15} />
            <div>
              <strong>Reserva confirmada</strong>
              <p>Agora convida os teus amigos para o jogo.</p>
            </div>
          </motion.div>
        )}

        <motion.h1 variants={aparecer} custom={atrasoBase - 0.1} initial={inicio} animate="visivel">
          {reservation.pitch.name}
        </motion.h1>
        <motion.p
          className="reserva__city"
          variants={aparecer}
          custom={atrasoBase - 0.05}
          initial={inicio}
          animate="visivel"
        >
          {reservation.pitch.city}
        </motion.p>

        <div className="reserva__card">
          {linhas.map((linha, i) => (
            <motion.div
              key={linha.rotulo}
              className="reserva__campo"
              variants={aparecer}
              custom={atrasoBase + 0.1 + i * 0.07}
              initial={inicio}
              animate="visivel"
            >
              <span>{linha.rotulo}</span>
              <strong>{linha.valor}</strong>
            </motion.div>
          ))}
        </div>

        <motion.div variants={aparecer} custom={atrasoConvites} initial={inicio} animate="visivel">
          <h2>Convites</h2>

          <AnimatePresence>
            {actionError && (
              <motion.p className="reserva__status reserva__status--error" role="alert" {...aviso}>
                {actionError}
              </motion.p>
            )}
          </AnimatePresence>

          {invitations.length === 0 && <p className="reserva__status">Ainda não há convites.</p>}

          <ul className="reserva__convites">
            <AnimatePresence initial={false}>
              {invitations.map((invitation) => {
                const podeGerir = invitation.status === 'PENDING' && invitation.guestId === userId
                const podeCancelar = isOrganizer || invitation.invitedById === userId

                return (
                  <motion.li
                    key={invitation.id}
                    className="convite"
                    layout="position"
                    initial={{ opacity: 0, y: -12 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, x: -32, transition: { duration: durations.fast, ease: easings.out } }}
                    transition={{ duration: durations.base, ease: easings.out }}
                  >
                    <span className="convite__nome">{invitation.guestName}</span>
                    <motion.span
                      key={invitation.status}
                      className={`badge convite__estado convite__estado--${invitation.status.toLowerCase()}`}
                      initial={reduzir ? false : { scale: 0.7, opacity: 0 }}
                      animate={{ scale: 1, opacity: 1 }}
                      transition={{ type: 'spring', stiffness: 500, damping: 22 }}
                    >
                      {ESTADOS[invitation.status] ?? invitation.status}
                    </motion.span>

                    <div className="convite__acoes">
                      {podeGerir && (
                        <>
                          <button
                            type="button"
                            className="btn-primary"
                            onClick={() => executarAcao(acceptInvitation, invitation.id)}
                          >
                            Aceitar
                          </button>
                          <button
                            type="button"
                            className="btn-ghost"
                            onClick={() => executarAcao(rejectInvitation, invitation.id)}
                          >
                            Rejeitar
                          </button>
                        </>
                      )}
                      {podeCancelar && (
                        <button
                          type="button"
                          className="btn-ghost"
                          onClick={() => executarAcao(cancelInvitation, invitation.id)}
                        >
                          Cancelar
                        </button>
                      )}
                    </div>
                  </motion.li>
                )
              })}
            </AnimatePresence>
          </ul>

          {podeConvidar && (
            <form className="reserva__convidar" onSubmit={handleConvidar}>
              <div className="reserva__convidar-linha" ref={formConvidar}>
                <input
                  type="text"
                  placeholder="Username ou email"
                  aria-label="Username ou email"
                  value={identifier}
                  onChange={(e) => setIdentifier(e.target.value)}
                  required
                />
                <button type="submit" className="btn-primary" disabled={convidarLoading}>
                  {convidarLoading ? (
                    <>
                      <span className="reserva__spinner" aria-hidden="true" />
                      A convidar...
                    </>
                  ) : (
                    'Convidar'
                  )}
                </button>
              </div>

              <AnimatePresence mode="wait">
                {convidarError && (
                  <motion.p key="erro" className="reserva__status reserva__status--error" role="alert" {...aviso}>
                    {convidarError}
                  </motion.p>
                )}
                {conviteEnviado && (
                  <motion.p key="enviado" className="reserva__status reserva__status--sucesso" role="status" {...aviso}>
                    Convite enviado.
                  </motion.p>
                )}
              </AnimatePresence>
            </form>
          )}
        </motion.div>
      </div>
    </section>
  )
}

export default Reserva
