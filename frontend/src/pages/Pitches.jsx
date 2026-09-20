import { useEffect, useState } from 'react'
import { searchPitches } from '../api/pitches'
import './Pitches.css'

const TIPOS = {
  FIVE: 'Futebol 5',
  SEVEN: 'Futebol 7',
  ELEVEN: 'Futebol 11',
  FUTSAL: 'Futsal',
}

function Pitches() {
  const [name, setName] = useState('')
  const [city, setCity] = useState('')
  const [pitches, setPitches] = useState([])
  const [cities, setCities] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

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
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

export default Pitches
