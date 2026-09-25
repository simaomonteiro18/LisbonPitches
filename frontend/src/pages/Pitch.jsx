import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import mapboxgl from 'mapbox-gl'
import 'mapbox-gl/dist/mapbox-gl.css'
import { getPitchById } from '../api/pitches'
import { createReservation } from '../api/reservations'
import { getSession } from '../auth'
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

if (MAPBOX_TOKEN) {
  mapboxgl.accessToken = MAPBOX_TOKEN
}

function googleMapsUrl(latitude, longitude) {
  if (latitude == null || longitude == null) {
    return null
  }

  return `https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}`
}

function Pitch() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [pitch, setPitch] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const [reserving, setReserving] = useState(false)
  const [startTime, setStartTime] = useState('')
  const [endTime, setEndTime] = useState('')
  const [reservaLoading, setReservaLoading] = useState(false)
  const [reservaError, setReservaError] = useState(null)

  const mapaContainerRef = useRef(null)
  const mapaRef = useRef(null)

  useEffect(() => {
    setLoading(true)
    setError(null)

    getPitchById(id)
      .then(setPitch)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  useEffect(() => {
    if (!pitch || !MAPBOX_TOKEN || pitch.latitude == null || pitch.longitude == null || !mapaContainerRef.current) {
      return
    }

    mapaRef.current = new mapboxgl.Map({
      container: mapaContainerRef.current,
      style: 'mapbox://styles/mapbox/streets-v12',
      center: [pitch.longitude, pitch.latitude],
      zoom: 14,
    })

    mapaRef.current.addControl(new mapboxgl.NavigationControl(), 'top-right')

    new mapboxgl.Marker({ color: '#3fae4a' })
      .setLngLat([pitch.longitude, pitch.latitude])
      .addTo(mapaRef.current)

    return () => {
      mapaRef.current?.remove()
      mapaRef.current = null
    }
  }, [pitch])

  function abrirFormularioReserva() {
    if (!getSession()) {
      navigate('/login')
      return
    }
    setReservaError(null)
    setStartTime('')
    setEndTime('')
    setReserving((prev) => !prev)
  }

  async function confirmarReserva(e) {
    e.preventDefault()
    setReservaLoading(true)
    setReservaError(null)

    try {
      const reservation = await createReservation({ pitchId: pitch.id, startTime, endTime })
      navigate(`/reservas/${reservation.id}`)
    } catch (err) {
      setReservaError(err.message)
    } finally {
      setReservaLoading(false)
    }
  }

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

  const temMapa = Boolean(MAPBOX_TOKEN) && pitch.latitude != null && pitch.longitude != null

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
              <strong>{pitch.pricePerHour != null ? `A partir de: ${pitch.pricePerHour} EUR` : 'Sem preço'}</strong>
            </div>
          )}
          <div className="pitch__campo">
            <span>Cidade</span>
            <strong>{pitch.city}</strong>
          </div>
        </div>

        {pitch.pitchAccess !== 'PUBLIC' && (pitch.contactPhone || pitch.contactEmail) && (
          <div className="pitch__contacto">
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
          </div>
        )}

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

        {pitch.pitchAccess === 'PRIVATE' && !pitch.reservable && (
          <p className="pitch__status">Este campo ainda não tem reserva online disponível, contacta diretamente.</p>
        )}

        {pitch.reservable && (
          <>
            <button type="button" className="btn-primary pitch__reservar" onClick={abrirFormularioReserva}>
              {reserving ? 'Cancelar' : 'Reservar'}
            </button>

            {reserving && (
              <form className="pitch__form" onSubmit={confirmarReserva}>
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

                {reservaError && <p className="pitch__status pitch__status--error">{reservaError}</p>}

                <button type="submit" className="btn-primary" disabled={reservaLoading}>
                  {reservaLoading ? 'A confirmar...' : 'Confirmar reserva'}
                </button>
              </form>
            )}
          </>
        )}
      </div>
    </section>
  )
}

export default Pitch
