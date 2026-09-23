import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getPitchById } from '../api/pitches'
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

function mapaUrl(latitude, longitude) {
  if (!MAPBOX_TOKEN || latitude == null || longitude == null) {
    return null
  }

  return `https://api.mapbox.com/styles/v1/mapbox/streets-v12/static/pin-l+16a34a(${longitude},${latitude})/${longitude},${latitude},13,0/640x320@2x?access_token=${MAPBOX_TOKEN}`
}

function googleMapsUrl(latitude, longitude) {
  if (latitude == null || longitude == null) {
    return null
  }

  return `https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}`
}

function Pitch() {
  const { id } = useParams()

  const [pitch, setPitch] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    setLoading(true)
    setError(null)

    getPitchById(id)
      .then(setPitch)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) {
    return (
      <section className="pitch">
        <div className="container">
          <p className="pitch__status">A carregar...</p>
        </div>
      </section>
    )
  }

  if (error || !pitch) {
    return (
      <section className="pitch">
        <div className="container">
          <p className="pitch__status pitch__status--error">{error ?? 'Campo não encontrado.'}</p>
        </div>
      </section>
    )
  }

  const mapa = mapaUrl(pitch.latitude, pitch.longitude)

  return (
    <section className="pitch">
      <div className="container pitch__inner">
        <Link to="/pitches" className="pitch__voltar">
          &larr; Voltar aos campos
        </Link>

        {pitch.imageUrl && (
          <img className="pitch__imagem" src={pitch.imageUrl} alt={pitch.name} />
        )}

        <h1>{pitch.name}</h1>
        <p className="pitch__morada">{pitch.address ?? pitch.city}</p>

        <div className="pitch__card">
          <div className="pitch__campo">
            <span>Tipo</span>
            <strong>{TIPOS[pitch.type] ?? pitch.type}</strong>
          </div>
          <div className="pitch__campo">
            <span>Acesso</span>
            <strong>{ACESSOS[pitch.pitchAccess] ?? pitch.pitchAccess}</strong>
          </div>
          {pitch.pitchAccess !== 'PUBLIC' && (
            <div className="pitch__campo">
              <span>Preço por hora</span>
              <strong>{pitch.pricePerHour != null ? `${pitch.pricePerHour} EUR` : 'Sem preço'}</strong>
            </div>
          )}
          <div className="pitch__campo">
            <span>Cidade</span>
            <strong>{pitch.city}</strong>
          </div>
        </div>

        <h2>Localização</h2>
        {mapa ? (
          <a
            href={googleMapsUrl(pitch.latitude, pitch.longitude)}
            target="_blank"
            rel="noopener noreferrer"
            className="pitch__mapa-link"
          >
            <img className="pitch__mapa" src={mapa} alt={`Localização de ${pitch.name}, abre no Google Maps`} />
          </a>
        ) : (
          <p className="pitch__status">Localização não disponível.</p>
        )}

        {pitch.pitchAccess !== 'PUBLIC' && (
          <Link to="/pitches" className="btn-primary pitch__reservar">
            Reservar na lista de campos
          </Link>
        )}
      </div>
    </section>
  )
}

export default Pitch
