import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { searchPitches } from '../api/pitches'
import { createReservation } from '../api/reservations'
import { getSession } from '../auth'
import './Pitches.css'

const TIPOS = {
  FIVE: 'Futebol 5',
  SEVEN: 'Futebol 7',
  ELEVEN: 'Futebol 11',
  FUTSAL: 'Futsal',
}

function Pitches() {
  const navigate = useNavigate()
  const [name, setName] = useState('')
  const [city, setCity] = useState('')
  const [pitches, setPitches] = useState([])
  const [cities, setCities] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const [reservingId, setReservingId] = useState(null)
  const [startTime, setStartTime] = useState('')
  const [endTime, setEndTime] = useState('')
  const [reservaLoading, setReservaLoading] = useState(false)
  const [reservaError, setReservaError] = useState(null)

  useEffect(() => {
    searchPitches({})
      .then((all) => {
        const uniqueCities = [...new Set(all.map((pitch) => pitch.city))].sort()
        setCities(uniqueCities)
      })
      .catch(() => {})
  }, [])

  useEffect(() => {
    const timeout = setTimeout(() => {
      setLoading(true)
      setError(null)

      searchPitches({ name, city })
        .then(setPitches)
        .catch(() => setError('Não foi possível carregar os campos. Tenta outra vez.'))
        .finally(() => setLoading(false))
    }, 350)

    return () => clearTimeout(timeout)
  }, [name, city])

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
            onChange={(e) => setName(e.target.value)}
          />
          <select value={city} onChange={(e) => setCity(e.target.value)}>
            <option value="">Todas as cidades</option>
            {cities.map((cityOption) => (
              <option key={cityOption} value={cityOption}>
                {cityOption}
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
              <h3>{pitch.name}</h3>
              <p className="pitch-card__city">{pitch.city}</p>
              <div className="pitch-card__footer">
                <span className="badge">{TIPOS[pitch.type] ?? pitch.type}</span>
                <span className="pitch-card__price">
                  {pitch.pricePerHour != null ? `${pitch.pricePerHour} EUR/hora` : 'Sem preço'}
                </span>
              </div>

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
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

export default Pitches
