import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getAllPitchesForMap, searchPitches } from '../api/pitches'
import { createReservation } from '../api/reservations'
import { getSession } from '../auth'
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

const PAGE_SIZE = 12

const SORT_OPTIONS = [
  { value: 'name-asc', label: 'Nome A-Z', rules: ['name,asc'] },
  { value: 'name-desc', label: 'Nome Z-A', rules: ['name,desc'] },
  { value: 'price-asc', label: 'Preço crescente', rules: ['pricePerHour,asc', 'name,asc'], byPrice: true },
  { value: 'price-desc', label: 'Preço decrescente', rules: ['pricePerHour,desc', 'name,asc'], byPrice: true },
  { value: 'city-asc', label: 'Cidade A-Z', rules: ['city,asc', 'name,asc'] },
]

function Pitches() {
  const navigate = useNavigate()
  const [name, setName] = useState('')
  const [city, setCity] = useState('')
  const [pitchAccess, setPitchAccess] = useState('')
  const [sort, setSort] = useState('name-asc')
  const [pitches, setPitches] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [cities, setCities] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const [reservingId, setReservingId] = useState(null)
  const [startTime, setStartTime] = useState('')
  const [endTime, setEndTime] = useState('')
  const [reservaLoading, setReservaLoading] = useState(false)
  const [reservaError, setReservaError] = useState(null)

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
      setLoading(true)
      setError(null)

      searchPitches({ name, city, pitchAccess: accessFilter, page, size: PAGE_SIZE, sort: sortOption.rules })
        .then((data) => {
          setPitches(data.content)
          setTotalPages(data.page.totalPages)
        })
        .catch(() => setError('Não foi possível carregar os campos. Tenta outra vez.'))
        .finally(() => setLoading(false))
    }, 350)

    return () => clearTimeout(timeout)
  }, [name, city, accessFilter, sort, page])

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

  return (
    <section className="pitches">
      <div className="container">
        <h1>Campos</h1>

        <div className="pitches__search">
          <input
            type="text"
            placeholder="Pesquisar por nome"
            value={name}
            onChange={(e) => {
              setName(e.target.value)
              setPage(0)
            }}
          />
          <select
            value={city}
            onChange={(e) => {
              setCity(e.target.value)
              setPage(0)
            }}
          >
            <option value="">Todas as cidades</option>
            {cities.map((cityOption) => (
              <option key={cityOption} value={cityOption}>
                {cityOption}
              </option>
            ))}
          </select>
          <select
            value={accessFilter}
            disabled={sortOption.byPrice}
            onChange={(e) => {
              setPitchAccess(e.target.value)
              setPage(0)
            }}
          >
            <option value="">Públicos e privados</option>
            <option value="PUBLIC">Só públicos</option>
            <option value="PRIVATE">Só privados</option>
          </select>
          <select
            value={sort}
            onChange={(e) => {
              setSort(e.target.value)
              setPage(0)
            }}
            aria-label="Ordenar por"
          >
            {SORT_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>

        {loading && <p className="pitches__status">A carregar...</p>}
        {error && <p className="pitches__status pitches__status--error">{error}</p>}
        {!loading && !error && pitches.length === 0 && (
          <p className="pitches__status">Nenhum campo encontrado.</p>
        )}

        <div className="pitches__grid">
          {pitches.map((pitch) => (
            <div className="pitch-card" key={pitch.id}>
              <Link to={`/pitches/${pitch.id}`} className="pitch-card__link">
                <h3>{pitch.name}</h3>
                <p className="pitch-card__city">{pitch.city}</p>
                <div className="pitch-card__footer">
                  <span className="badge">{TIPOS[pitch.type] ?? pitch.type}</span>
                  <span className={`badge badge--acesso ${pitch.pitchAccess === 'PRIVATE' ? 'badge--privado' : ''}`}>
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
                  >
                    {reservingId === pitch.id ? 'Cancelar' : 'Reservar'}
                  </button>

                  {reservingId === pitch.id && (
                    <form className="pitch-card__form" onSubmit={(e) => confirmarReserva(e, pitch.id)}>
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

                      {reservaError && <p className="pitches__status pitches__status--error">{reservaError}</p>}

                      <button type="submit" className="btn-primary" disabled={reservaLoading}>
                        {reservaLoading ? 'A confirmar...' : 'Confirmar reserva'}
                      </button>
                    </form>
                  )}
                </>
              )}
            </div>
          ))}
        </div>

        {totalPages > 1 && (
          <div className="pitches__pagination">
            <button
              type="button"
              className="btn-ghost"
              onClick={() => setPage(page - 1)}
              disabled={page === 0}
            >
              Anterior
            </button>
            <span>
              Página {page + 1} de {totalPages}
            </span>
            <button
              type="button"
              className="btn-ghost"
              onClick={() => setPage(page + 1)}
              disabled={page >= totalPages - 1}
            >
              Seguinte
            </button>
          </div>
        )}
      </div>
    </section>
  )
}

export default Pitches
