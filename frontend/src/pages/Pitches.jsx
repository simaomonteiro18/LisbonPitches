import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { AnimatePresence, motion, useReducedMotion } from 'motion/react'
import { getAllPitchesForMap, searchPitches } from '../api/pitches'
import { createReservation } from '../api/reservations'
import { getSession } from '../auth'
import { aoVer, durations, easings, revelar } from '../motion'
import Contador from '../components/Contador'
import './Pitches.css'

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

const OPCOES_ACESSO = [
  { value: '', label: 'Todos' },
  { value: 'PUBLIC', label: 'Públicos' },
  { value: 'PRIVATE', label: 'Privados' },
]

const PAGE_SIZE = 12

const ORDEM_INICIAL = 'name-asc'

const SORT_OPTIONS = [
  { value: 'name-asc', label: 'Nome A-Z', rules: ['name,asc'] },
  { value: 'name-desc', label: 'Nome Z-A', rules: ['name,desc'] },
  { value: 'price-asc', label: 'Preço crescente', rules: ['pricePerHour,asc', 'name,asc'], byPrice: true },
  { value: 'price-desc', label: 'Preço decrescente', rules: ['pricePerHour,desc', 'name,asc'], byPrice: true },
  { value: 'city-asc', label: 'Cidade A-Z', rules: ['city,asc', 'name,asc'] },
]

// cartoes que chegam depois de mudar filtros ou pagina aparecem no sitio, sem esperar pelo scroll
const surgir = {
  escondido: { opacity: 0, scale: 0.92 },
  visivel: (atraso = 0) => ({
    opacity: 1,
    scale: 1,
    transition: { duration: durations.slow, ease: easings.out, delay: atraso },
  }),
}

const sair = { opacity: 0, scale: 0.92, transition: { duration: durations.fast, ease: easings.out } }

const formularioReserva = {
  initial: { opacity: 0, y: -8 },
  animate: { opacity: 1, y: 0, transition: { duration: durations.base, ease: easings.out } },
  exit: { opacity: 0, y: -8, transition: { duration: durations.fast, ease: easings.out } },
}

function Pitches() {
  const navigate = useNavigate()
  const reduzir = useReducedMotion()
  const inicio = reduzir ? false : 'escondido'

  const [name, setName] = useState('')
  const [city, setCity] = useState('')
  const [pitchAccess, setPitchAccess] = useState('')
  const [sort, setSort] = useState(ORDEM_INICIAL)
  const [pitches, setPitches] = useState(null)
  const [total, setTotal] = useState(null)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [cities, setCities] = useState([])
  const [aCarregar, setACarregar] = useState(true)
  const [error, setError] = useState(null)
  const [tentativa, setTentativa] = useState(0)
  // como cada cartao entra: 'scroll' na primeira carga, 'filtro' depois
  const [entradas, setEntradas] = useState(() => new Map())

  const [reservingId, setReservingId] = useState(null)
  const [startTime, setStartTime] = useState('')
  const [endTime, setEndTime] = useState('')
  const [reservaLoading, setReservaLoading] = useState(false)
  const [reservaError, setReservaError] = useState(null)

  const topoRef = useRef(null)
  const pedidoRef = useRef(0)
  const primeiraCargaRef = useRef(true)

  useEffect(() => {
    getAllPitchesForMap()
      .then((all) => {
        const uniqueCities = [...new Set(all.map((pitch) => pitch.city))].sort()
        setCities(uniqueCities)
      })
      .catch(() => {})
  }, [])

  const sortOption = SORT_OPTIONS.find((option) => option.value === sort)
  const accessFilter = sortOption.byPrice ? 'PRIVATE' : pitchAccess

  useEffect(() => {
    const timeout = setTimeout(() => {
      // so a resposta do pedido mais recente conta, para nao mostrar resultados antigos
      const pedido = ++pedidoRef.current
      setACarregar(true)
      setError(null)

      searchPitches({ name, city, pitchAccess: accessFilter, page, size: PAGE_SIZE, sort: sortOption.rules })
        .then((data) => {
          if (pedido !== pedidoRef.current) return

          const modo = primeiraCargaRef.current ? 'scroll' : 'filtro'
          primeiraCargaRef.current = false
          setEntradas((anteriores) => new Map(data.content.map((p) => [p.id, anteriores.get(p.id) ?? modo])))

          setPitches(data.content)
          setTotal(data.page.totalElements)
          setTotalPages(data.page.totalPages)
        })
        .catch(() => {
          if (pedido === pedidoRef.current) setError('Não foi possível carregar os campos.')
        })
        .finally(() => {
          if (pedido === pedidoRef.current) setACarregar(false)
        })
    }, 350)

    return () => clearTimeout(timeout)
  }, [name, city, accessFilter, sort, page, tentativa])

  function mudarFiltro(setter) {
    return (valor) => {
      setter(valor)
      setPage(0)
    }
  }

  const mudarNome = mudarFiltro(setName)
  const mudarCidade = mudarFiltro(setCity)
  const mudarAcesso = mudarFiltro(setPitchAccess)
  const mudarOrdem = mudarFiltro(setSort)

  // a ordem por preco tambem filtra (so privados), por isso conta como filtro
  const temFiltros = Boolean(name || city || pitchAccess || sort !== ORDEM_INICIAL)

  function limparFiltros() {
    setName('')
    setCity('')
    setPitchAccess('')
    setSort(ORDEM_INICIAL)
    setPage(0)
  }

  function irParaPagina(novaPagina) {
    setPage(novaPagina)
    topoRef.current?.scrollIntoView({ behavior: reduzir ? 'auto' : 'smooth', block: 'start' })
  }

  function abrirFormularioReserva(pitchId) {
    if (!getSession()) {
      navigate('/login')
      return
    }
    setReservaError(null)
    setStartTime('')
    setEndTime('')
    setReservingId(reservingId === pitchId ? null : pitchId)
  }

  async function confirmarReserva(e, pitchId) {
    e.preventDefault()
    setReservaLoading(true)
    setReservaError(null)

    try {
      const reservation = await createReservation({ pitchId, startTime, endTime })
      navigate(`/reservas/${reservation.id}`)
    } catch (err) {
      setReservaError(err.message)
    } finally {
      setReservaLoading(false)
    }
  }

  const aAtualizar = aCarregar && pitches !== null

  return (
    <section className="pitches">
      <div className="container">
        <h1>Campos</h1>

        <div className="pitches__filtros">
          <input
            type="text"
            placeholder="Pesquisar por nome"
            aria-label="Pesquisar por nome"
            value={name}
            onChange={(e) => mudarNome(e.target.value)}
          />
          <select value={city} onChange={(e) => mudarCidade(e.target.value)} aria-label="Cidade">
            <option value="">Todas as cidades</option>
            {cities.map((cityOption) => (
              <option key={cityOption} value={cityOption}>
                {cityOption}
              </option>
            ))}
          </select>
          <select value={sort} onChange={(e) => mudarOrdem(e.target.value)} aria-label="Ordenar por">
            {SORT_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>

          <div className="pitches__acesso" role="group" aria-label="Acesso">
            {OPCOES_ACESSO.map((opcao) => {
              const ativa = accessFilter === opcao.value
              return (
                <button
                  type="button"
                  key={opcao.value}
                  className={`pitches__acesso-opcao${ativa ? ' pitches__acesso-opcao--ativa' : ''}`}
                  aria-pressed={ativa}
                  disabled={sortOption.byPrice}
                  onClick={() => mudarAcesso(opcao.value)}
                >
                  {ativa && (
                    <motion.span
                      layoutId="pitches-acesso-indicador"
                      className="pitches__acesso-indicador"
                      transition={{ type: 'spring', stiffness: 500, damping: 38 }}
                    />
                  )}
                  <span className="pitches__acesso-texto">{opcao.label}</span>
                </button>
              )
            })}
          </div>
        </div>

        <div className="pitches__resumo" ref={topoRef}>
          <p className="pitches__total" aria-live="polite">
            {total != null && !error && (
              <>
                <Contador valor={total} duracao={0.6} /> {total === 1 ? 'campo encontrado' : 'campos encontrados'}
              </>
            )}
          </p>
          <AnimatePresence>
            {temFiltros && (
              <motion.button
                type="button"
                className="pitches__limpar"
                onClick={limparFiltros}
                initial={{ opacity: 0, x: 8 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: 8 }}
                transition={{ duration: durations.fast, ease: easings.out }}
              >
                Limpar filtros
              </motion.button>
            )}
          </AnimatePresence>
        </div>

        {error && (
          <div className="pitches__aviso pitches__aviso--erro">
            <p>{error}</p>
            <button type="button" className="btn-ghost" onClick={() => setTentativa((t) => t + 1)}>
              Tentar outra vez
            </button>
          </div>
        )}

        {!error && pitches === null && (
          <div className="pitches__grid" aria-hidden="true">
            {Array.from({ length: PAGE_SIZE }, (_, i) => (
              <div className="pitches__esqueleto" key={i}>
                <span />
                <span />
                <span />
              </div>
            ))}
          </div>
        )}

        {!error && pitches?.length === 0 && (
          <div className="pitches__aviso">
            <svg className="pitches__aviso-pin" viewBox="-16 -44 32 46" width="40" height="58" aria-hidden="true">
              <path d="M0 0 C -6 -10 -14 -16 -14 -26 A14 14 0 1 1 14 -26 C 14 -16 6 -10 0 0 Z" />
              <circle cx="0" cy="-26" r="5" />
            </svg>
            <p>Nenhum campo encontrado.</p>
            {temFiltros && (
              <button type="button" className="btn-ghost" onClick={limparFiltros}>
                Limpar filtros
              </button>
            )}
          </div>
        )}

        {!error && pitches?.length > 0 && (
          <div className={`pitches__resultados${aAtualizar ? ' pitches__resultados--a-atualizar' : ''}`}>
            {aAtualizar && <div className="pitches__progresso" aria-hidden="true" />}

            <div className="pitches__grid">
              <AnimatePresence mode="popLayout">
                {pitches.map((pitch, i) => {
                  const porScroll = entradas.get(pitch.id) === 'scroll'
                  const entrada = porScroll
                    ? { variants: revelar, custom: (i % 3) * 0.12, whileInView: 'visivel', viewport: aoVer }
                    : { variants: surgir, custom: i * 0.03, animate: 'visivel' }

                  return (
                    <motion.div
                      key={pitch.id}
                      className="pitches__item"
                      layout="position"
                      initial={inicio}
                      exit={sair}
                      {...entrada}
                    >
                      <div className="pitch-card">
                        <Link to={`/pitches/${pitch.id}`} className="pitch-card__link">
                          <h2>{pitch.name}</h2>
                          <p className="pitch-card__city">{pitch.city}</p>
                          <div className="pitch-card__footer">
                            <span className="badge">{TIPOS[pitch.type] ?? pitch.type}</span>
                            <span
                              className={`badge badge--acesso ${pitch.pitchAccess === 'PRIVATE' ? 'badge--privado' : ''}`}
                            >
                              {ACESSOS[pitch.pitchAccess] ?? pitch.pitchAccess}
                            </span>
                          </div>
                          {pitch.pitchAccess !== 'PUBLIC' && (
                            <p className="pitch-card__price">
                              {pitch.pricePerHour != null ? `A partir de: ${pitch.pricePerHour} EUR/hora` : 'Sem preço'}
                            </p>
                          )}
                        </Link>

                        {pitch.reservable && (
                          <>
                            <button
                              type="button"
                              className="btn-ghost pitch-card__reservar"
                              onClick={() => abrirFormularioReserva(pitch.id)}
                              aria-expanded={reservingId === pitch.id}
                            >
                              {reservingId === pitch.id ? 'Cancelar' : 'Reservar'}
                            </button>

                            <AnimatePresence initial={false}>
                              {reservingId === pitch.id && (
                                <motion.form
                                  className="pitch-card__form"
                                  onSubmit={(e) => confirmarReserva(e, pitch.id)}
                                  {...formularioReserva}
                                >
                                  <label>
                                    Início
                                    <input
                                      type="datetime-local"
                                      value={startTime}
                                      onChange={(e) => setStartTime(e.target.value)}
                                      required
                                    />
                                  </label>
                                  <label>
                                    Fim
                                    <input
                                      type="datetime-local"
                                      value={endTime}
                                      onChange={(e) => setEndTime(e.target.value)}
                                      required
                                    />
                                  </label>

                                  {reservaError && (
                                    <p className="pitches__status pitches__status--error">{reservaError}</p>
                                  )}

                                  <button type="submit" className="btn-primary" disabled={reservaLoading}>
                                    {reservaLoading ? 'A confirmar...' : 'Confirmar reserva'}
                                  </button>
                                </motion.form>
                              )}
                            </AnimatePresence>
                          </>
                        )}
                      </div>
                    </motion.div>
                  )
                })}
              </AnimatePresence>
            </div>
          </div>
        )}

        {!error && totalPages > 1 && (
          <nav className="pitches__pagination" aria-label="Paginação">
            <button
              type="button"
              className="btn-ghost"
              onClick={() => irParaPagina(page - 1)}
              disabled={page === 0}
            >
              Anterior
            </button>
            <span>
              Página{' '}
              <span className="pitches__pagina-numero">
                <AnimatePresence mode="popLayout" initial={false}>
                  <motion.span
                    key={page}
                    initial={{ opacity: 0, y: 12 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -12 }}
                    transition={{ duration: durations.base, ease: easings.out }}
                  >
                    {page + 1}
                  </motion.span>
                </AnimatePresence>
              </span>{' '}
              de {totalPages}
            </span>
            <button
              type="button"
              className="btn-ghost"
              onClick={() => irParaPagina(page + 1)}
              disabled={page >= totalPages - 1}
            >
              Seguinte
            </button>
          </nav>
        )}
      </div>
    </section>
  )
}

export default Pitches
