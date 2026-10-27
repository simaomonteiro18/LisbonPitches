import { Fragment, useEffect, useRef, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { AnimatePresence, motion, useAnimate, useReducedMotion } from 'motion/react'
import 'mapbox-gl/dist/mapbox-gl.css'
import { getAvailability, getPitchById } from '../api/pitches'
import { createReservation } from '../api/reservations'
import { getSession } from '../auth'
import { durations, easings, stagger } from '../motion'
import { carregarMapbox, criarPin, estiloMapa, seguirTema } from '../components/mapaPin'
import CheckAnimado from '../components/CheckAnimado'
import './Pitch.css'

const TIPOS = {
  FIVE: 'Futebol 5',
  SEVEN: 'Futebol 7',
  ELEVEN: 'Futebol 11',
  FUTSAL: 'Futsal',
}

const ACESSOS = {
  PUBLIC: 'Público',
  PRIVATE: 'Privado',
}

const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_TOKEN

// tempo que a confirmacao fica no ecra antes de abrir a reserva
const PAUSA_SUCESSO = 1600

const aparecer = {
  escondido: { opacity: 0, y: 24 },
  visivel: (atraso = 0) => ({
    opacity: 1,
    y: 0,
    transition: { duration: durations.slow, ease: easings.out, delay: atraso },
  }),
}

const painelReserva = {
  initial: { opacity: 0, y: -10 },
  animate: { opacity: 1, y: 0, transition: { duration: durations.base, ease: easings.out } },
  exit: { opacity: 0, y: -10, transition: { duration: durations.fast, ease: easings.out } },
}

const avisoForm = {
  initial: { opacity: 0, y: -4 },
  animate: { opacity: 1, y: 0 },
  exit: { opacity: 0 },
  transition: { duration: durations.fast },
}

function googleMapsUrl(latitude, longitude) {
  if (latitude == null || longitude == null) {
    return null
  }

  return `https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}`
}

const doisDigitos = (n) => String(n).padStart(2, '0')

// dia seguinte sem somar milissegundos, que falha nos dias de mudanca de hora
function diaSeguinte(dia) {
  const [ano, mes, numero] = dia.split('-').map(Number)
  return formatarLocal(new Date(ano, mes - 1, numero + 1)).slice(0, 10)
}

// mesmo formato do input datetime-local que a API ja recebia
function formatarLocal(data) {
  return `${data.getFullYear()}-${doisDigitos(data.getMonth() + 1)}-${doisDigitos(data.getDate())}T${doisDigitos(data.getHours())}:00`
}

function formatarEuros(valor) {
  return Number.isInteger(valor) ? String(valor) : valor.toFixed(2).replace('.', ',')
}

// desenho de campo para quando nao ha fotografia
function ImagemSubstituta() {
  return (
    <svg
      className="pitch__imagem-substituta"
      viewBox="0 0 600 280"
      preserveAspectRatio="xMidYMid slice"
      aria-hidden="true"
    >
      <rect x="60" y="30" width="480" height="220" rx="4" />
      <line x1="300" y1="30" x2="300" y2="250" />
      <circle cx="300" cy="140" r="40" />
      <rect x="60" y="85" width="70" height="110" />
      <rect x="470" y="85" width="70" height="110" />
    </svg>
  )
}

function Voltar() {
  return (
    <Link to="/pitches" className="pitch__voltar">
      <span className="pitch__voltar-seta" aria-hidden="true">
        <svg viewBox="0 0 16 16" width="14" height="14">
          <path d="M13 8 H3 M7 4 L3 8 L7 12" />
        </svg>
      </span>
      Voltar aos campos
    </Link>
  )
}

function Esqueleto() {
  return (
    <section className="pitch" aria-busy="true">
      <div className="container pitch__inner" aria-hidden="true">
        <span className="pitch__esqueleto pitch__esqueleto--voltar" />
        <div className="pitch__esqueleto pitch__esqueleto--imagem" />
        <span className="pitch__esqueleto pitch__esqueleto--titulo" />
        <span className="pitch__esqueleto pitch__esqueleto--morada" />
        <div className="pitch__layout">
          <div className="pitch__esqueleto pitch__esqueleto--cartao" />
          <div className="pitch__esqueleto pitch__esqueleto--mapa" />
        </div>
      </div>
    </section>
  )
}

function Pitch() {
  const { id } = useParams()
  const navigate = useNavigate()
  const reduzir = useReducedMotion()
  const inicio = reduzir ? false : 'escondido'

  const [pitch, setPitch] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  // guarda o url que falhou, assim outro campo volta a tentar a sua imagem
  const [imagemFalhada, setImagemFalhada] = useState(null)
  const [mapaCarregado, setMapaCarregado] = useState(false)

  const [reserving, setReserving] = useState(false)
  const [dia, setDia] = useState('')
  // horas de inicio escolhidas, sempre seguidas e por ordem
  const [selecionadas, setSelecionadas] = useState([])
  const [disponibilidade, setDisponibilidade] = useState(null)
  const [disponibilidadeLoading, setDisponibilidadeLoading] = useState(false)
  const [disponibilidadeError, setDisponibilidadeError] = useState(null)
  const [recarregar, setRecarregar] = useState(0)
  // momento em que o formulario abriu, para bloquear horas que ja passaram
  const [agora, setAgora] = useState(() => new Date())
  const [reservaLoading, setReservaLoading] = useState(false)
  const [reservaError, setReservaError] = useState(null)
  const [reservaFeita, setReservaFeita] = useState(false)

  const mapaContainerRef = useRef(null)
  const sucessoTimerRef = useRef(null)
  const [campos, animarCampos] = useAnimate()

  useEffect(() => {
    setLoading(true)
    setError(null)

    getPitchById(id)
      .then(setPitch)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  useEffect(() => () => clearTimeout(sucessoTimerRef.current), [])

  useEffect(() => {
    if (!reserving || !dia || !pitch) return

    let cancelado = false

    getAvailability(pitch.id, dia)
      .then((dados) => {
        if (cancelado) return
        setDisponibilidade(dados)
        setDisponibilidadeError(null)
      })
      .catch((err) => {
        if (!cancelado) setDisponibilidadeError(err.message)
      })
      .finally(() => {
        if (!cancelado) setDisponibilidadeLoading(false)
      })

    return () => {
      cancelado = true
    }
  }, [reserving, dia, pitch, recarregar])

  useEffect(() => {
    if (!pitch || !MAPBOX_TOKEN || pitch.latitude == null || pitch.longitude == null || !mapaContainerRef.current) {
      return
    }

    let cancelado = false
    let limpar = null

    carregarMapbox().then((mapboxgl) => {
      if (cancelado || !mapaContainerRef.current) return

      const map = new mapboxgl.Map({
        container: mapaContainerRef.current,
        style: estiloMapa(),
        center: [pitch.longitude, pitch.latitude],
        zoom: 14,
      })

      map.addControl(new mapboxgl.NavigationControl(), 'top-right')
      map.on('load', () => setMapaCarregado(true))

      new mapboxgl.Marker({ element: criarPin(pitch, 0.2), anchor: 'bottom' })
        .setLngLat([pitch.longitude, pitch.latitude])
        .addTo(map)

      const pararTema = seguirTema(map)

      limpar = () => {
        pararTema()
        setMapaCarregado(false)
        map.remove()
      }
    })

    return () => {
      cancelado = true
      limpar?.()
    }
  }, [pitch])

  function abrirFormularioReserva() {
    if (!getSession()) {
      navigate('/login')
      return
    }
    setReservaError(null)
    setDia('')
    setSelecionadas([])
    setDisponibilidade(null)
    setDisponibilidadeLoading(false)
    setDisponibilidadeError(null)
    setAgora(new Date())
    setReserving((prev) => !prev)
  }

  function tremerFormulario() {
    if (!reduzir && campos.current) {
      animarCampos(campos.current, { x: [0, -10, 10, -6, 6, 0] }, { duration: 0.45 })
    }
  }

  function escolherDia(valor) {
    setDia(valor)
    setSelecionadas([])
    setDisponibilidade(null)
    setDisponibilidadeLoading(Boolean(valor))
    setDisponibilidadeError(null)
    setReservaError(null)
  }

  function alternarHora(hora) {
    setReservaError(null)
    setSelecionadas((atuais) => {
      if (atuais.length === 0) return [hora]
      const primeira = atuais[0]
      const ultima = atuais[atuais.length - 1]
      // so se tira uma hora das pontas, para as horas ficarem sempre seguidas
      if (atuais.includes(hora)) {
        return hora === primeira || hora === ultima ? atuais.filter((h) => h !== hora) : [hora]
      }
      if (hora === primeira - 1) return [hora, ...atuais]
      if (hora === ultima + 1) return [...atuais, hora]
      return [hora]
    })
  }

  const hoje = formatarLocal(agora).slice(0, 10)
  const horasLivres = disponibilidade?.status === 'BOOKABLE' ? disponibilidade.freeHours : []
  const primeiraHora = selecionadas[0]
  const horaFim = selecionadas.length > 0 ? selecionadas[selecionadas.length - 1] + 1 : null
  const fimNoDiaSeguinte = horaFim === 24
  const inicioTexto = primeiraHora != null ? `${dia}T${doisDigitos(primeiraHora)}:00` : null
  const fimTexto =
    horaFim == null ? null : fimNoDiaSeguinte ? `${diaSeguinte(dia)}T00:00` : `${dia}T${doisDigitos(horaFim)}:00`

  let avisoDisponibilidade = null
  if (disponibilidade && !disponibilidadeLoading && !disponibilidadeError) {
    if (disponibilidade.status === 'NOT_BOOKABLE') {
      avisoDisponibilidade = 'Este campo não aceita reservas online.'
    } else if (disponibilidade.status === 'NO_SCHEDULE') {
      avisoDisponibilidade = 'Este campo ainda não tem horário definido para esse dia.'
    } else if (horasLivres.length === 0) {
      avisoDisponibilidade = 'Não há horas livres neste dia.'
    }
  }

  async function confirmarReserva(e) {
    e.preventDefault()
    if (!inicioTexto || !fimTexto) {
      tremerFormulario()
      return
    }

    setReservaLoading(true)
    setReservaError(null)

    try {
      const reservation = await createReservation({
        pitchId: pitch.id,
        startTime: inicioTexto,
        endTime: fimTexto,
      })
      const abrirReserva = () => navigate(`/reservas/${reservation.id}`, { state: { acabadaDeCriar: true } })

      if (reduzir) {
        abrirReserva()
        return
      }
      setReservaFeita(true)
      sucessoTimerRef.current = setTimeout(abrirReserva, PAUSA_SUCESSO)
    } catch (err) {
      setReservaError(err.message)
      setSelecionadas([])
      setDisponibilidadeLoading(true)
      setRecarregar((n) => n + 1)
      tremerFormulario()
    } finally {
      setReservaLoading(false)
    }
  }

  if (loading) {
    return <Esqueleto />
  }

  if (error || !pitch) {
    return (
      <section className="pitch">
        <div className="container pitch__inner">
          <Voltar />
          <p className="pitch__status pitch__status--error">{error ?? 'Campo não encontrado.'}</p>
        </div>
      </section>
    )
  }

  const temMapa = Boolean(MAPBOX_TOKEN) && pitch.latitude != null && pitch.longitude != null
  const palavras = pitch.name.split(' ')
  const fimTitulo = 0.15 + palavras.length * stagger
  const linhas = [
    { rotulo: 'Tipo', valor: TIPOS[pitch.type] ?? pitch.type },
    { rotulo: 'Acesso', valor: ACESSOS[pitch.pitchAccess] ?? pitch.pitchAccess },
    pitch.pitchAccess !== 'PUBLIC' && {
      rotulo: 'Preço por hora',
      valor: pitch.pricePerHour != null ? `A partir de: ${pitch.pricePerHour} EUR` : 'Sem preço',
    },
    { rotulo: 'Cidade', valor: pitch.city },
  ].filter(Boolean)
  const atrasoInfo = fimTitulo + 0.1
  const atrasoDepoisCartao = atrasoInfo + linhas.length * 0.07 + 0.1

  return (
    <section className="pitch">
      <div className="container pitch__inner">
        <Voltar />

        <motion.div
          className="pitch__imagem-moldura"
          initial={reduzir ? false : { opacity: 0, scale: 1.06 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: durations.slower, ease: easings.out }}
        >
          {pitch.imageUrl && pitch.imageUrl !== imagemFalhada ? (
            <img
              className="pitch__imagem"
              fetchPriority="high"
              src={pitch.imageUrl}
              alt={pitch.name}
              onError={() => setImagemFalhada(pitch.imageUrl)}
            />
          ) : (
            <ImagemSubstituta />
          )}
        </motion.div>

        <h1 aria-label={pitch.name}>
          {palavras.map((palavra, i) => (
            <Fragment key={`${palavra}-${i}`}>
              <motion.span
                className="pitch__palavra"
                aria-hidden="true"
                variants={aparecer}
                custom={0.15 + i * stagger}
                initial={inicio}
                animate="visivel"
              >
                {palavra}
              </motion.span>{' '}
            </Fragment>
          ))}
        </h1>
        <motion.p
          className="pitch__morada"
          variants={aparecer}
          custom={fimTitulo}
          initial={inicio}
          animate="visivel"
        >
          {pitch.address ?? pitch.city}
        </motion.p>

        <div className="pitch__layout">
          <div className="pitch__info">
            <motion.h2 variants={aparecer} custom={atrasoInfo} initial={inicio} animate="visivel">
              Detalhes
            </motion.h2>
            <div className="pitch__card">
              {linhas.map((linha, i) => (
                <motion.div
                  key={linha.rotulo}
                  className="pitch__campo"
                  variants={aparecer}
                  custom={atrasoInfo + i * 0.07}
                  initial={inicio}
                  animate="visivel"
                >
                  <span>{linha.rotulo}</span>
                  <strong>{linha.valor}</strong>
                </motion.div>
              ))}
            </div>

            {pitch.pitchAccess !== 'PUBLIC' && (pitch.contactPhone || pitch.contactEmail) && (
              <motion.div
                className="pitch__contacto"
                variants={aparecer}
                custom={atrasoDepoisCartao}
                initial={inicio}
                animate="visivel"
              >
                <h2>Contacto</h2>
                {pitch.contactPhone && (
                  <p>
                    Telefone: <a href={`tel:${pitch.contactPhone}`}>{pitch.contactPhone}</a>
                  </p>
                )}
                {pitch.contactEmail && (
                  <p>
                    Email: <a href={`mailto:${pitch.contactEmail}`}>{pitch.contactEmail}</a>
                  </p>
                )}
              </motion.div>
            )}

            {pitch.pitchAccess === 'PRIVATE' && !pitch.reservable && (
              <motion.p
                className="pitch__status pitch__aviso-reserva"
                variants={aparecer}
                custom={atrasoDepoisCartao + 0.1}
                initial={inicio}
                animate="visivel"
              >
                Este campo ainda não tem reserva online disponível, contacta diretamente.
              </motion.p>
            )}

            {pitch.reservable && (
              <motion.div
                className="pitch__reserva"
                variants={aparecer}
                custom={atrasoDepoisCartao + 0.1}
                initial={inicio}
                animate="visivel"
              >
                <button
                  type="button"
                  className={reserving ? 'btn-ghost pitch__reservar' : 'btn-primary pitch__reservar'}
                  onClick={abrirFormularioReserva}
                  aria-expanded={reserving}
                >
                  {reserving ? 'Cancelar' : 'Reservar'}
                </button>

                <AnimatePresence initial={false}>
                  {reserving && (
                    <motion.form className="pitch__form" onSubmit={confirmarReserva} {...painelReserva}>
                      <div className="pitch__form-corpo" ref={campos}>
                        <div className="pitch__form-horas">
                          <label className="pitch__form-dia">
                            Dia
                            <input
                              type="date"
                              value={dia}
                              min={hoje}
                              onChange={(e) => escolherDia(e.target.value)}
                              required
                            />
                          </label>
                        </div>

                        {dia && (
                          <div className="pitch__horas">
                            {disponibilidadeLoading && <p className="pitch__status">A ver as horas livres...</p>}
                            {disponibilidadeError && (
                              <p className="pitch__status pitch__status--error" role="alert">
                                {disponibilidadeError}
                              </p>
                            )}
                            {avisoDisponibilidade && <p className="pitch__status">{avisoDisponibilidade}</p>}
                            {horasLivres.length > 0 && (
                              <>
                                <p className="pitch__horas-titulo" id="pitch-horas-titulo">
                                  Escolhe as horas (seguidas)
                                </p>
                                <div className="pitch__horas-lista" role="group" aria-labelledby="pitch-horas-titulo">
                                  {horasLivres.map((h) => (
                                    <button
                                      key={h}
                                      type="button"
                                      className={`pitch__hora${selecionadas.includes(h) ? ' pitch__hora--ativa' : ''}`}
                                      aria-pressed={selecionadas.includes(h)}
                                      onClick={() => alternarHora(h)}
                                    >
                                      {doisDigitos(h)}:00
                                    </button>
                                  ))}
                                </div>
                              </>
                            )}
                          </div>
                        )}

                        <AnimatePresence mode="wait" initial={false}>
                          {inicioTexto && (
                            <motion.div
                              key={`${dia}-${primeiraHora}-${selecionadas.length}`}
                              className="pitch__resumo"
                              {...avisoForm}
                            >
                              <span>
                                Das <strong>{doisDigitos(primeiraHora)}:00</strong> às{' '}
                                <strong>{doisDigitos(horaFim % 24)}:00</strong>
                                {fimNoDiaSeguinte && ' do dia seguinte'}
                              </span>
                              {pitch.pricePerHour != null && (
                                <span>
                                  Estimativa: a partir de{' '}
                                  <strong>{formatarEuros(pitch.pricePerHour * selecionadas.length)} EUR</strong>
                                </span>
                              )}
                            </motion.div>
                          )}
                        </AnimatePresence>

                        <AnimatePresence>
                          {reservaError && (
                            <motion.p className="pitch__status pitch__status--error" role="alert" {...avisoForm}>
                              {reservaError}
                            </motion.p>
                          )}
                        </AnimatePresence>

                        <button type="submit" className="btn-primary" disabled={reservaLoading || reservaFeita || selecionadas.length === 0}>
                          {reservaLoading ? (
                            <>
                              <span className="pitch__spinner" aria-hidden="true" />
                              A confirmar...
                            </>
                          ) : (
                            'Confirmar reserva'
                          )}
                        </button>
                      </div>
                    </motion.form>
                  )}
                </AnimatePresence>
              </motion.div>
            )}
          </div>

          <motion.div
            className={`pitch__mapa-coluna${mapaCarregado ? ' mapa-pins--a-cair' : ''}`}
            variants={aparecer}
            custom={atrasoInfo + 0.1}
            initial={inicio}
            animate="visivel"
          >
            <h2>Localização</h2>
            {temMapa ? (
              <>
                <div ref={mapaContainerRef} className="pitch__mapa" />
                <a
                  href={googleMapsUrl(pitch.latitude, pitch.longitude)}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="pitch__mapa-link"
                >
                  Abrir no Google Maps
                </a>
              </>
            ) : (
              <p className="pitch__status">Localização não disponível.</p>
            )}
          </motion.div>
        </div>
      </div>

      <AnimatePresence>
        {reservaFeita && (
          <motion.div
            className="pitch__sucesso"
            role="status"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: durations.base, ease: easings.out }}
          >
            <motion.div
              className="pitch__sucesso-cartao"
              initial={{ opacity: 0, scale: 0.92, y: 16 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              transition={{ duration: durations.slow, ease: easings.out }}
            >
              <CheckAnimado tamanho={80} atraso={0.1} />
              <h2>Reserva confirmada</h2>
              <p>A abrir a tua reserva...</p>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </section>
  )
}

export default Pitch
