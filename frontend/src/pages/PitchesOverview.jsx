import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import mapboxgl from 'mapbox-gl'
import 'mapbox-gl/dist/mapbox-gl.css'
import { getFeaturedPitches, getAllPitchesForMap } from '../api/pitches'
import './PitchesOverview.css'

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

function escapeHtml(texto) {
  const div = document.createElement('div')
  div.textContent = texto ?? ''
  return div.innerHTML
}

function PitchesOverview() {
  const [featuredPitches, setFeaturedPitches] = useState([])
  const [featuredError, setFeaturedError] = useState(null)

  const [mapPitches, setMapPitches] = useState([])
  const [mapError, setMapError] = useState(null)

  const mapaContainerRef = useRef(null)

  useEffect(() => {
    getFeaturedPitches()
      .then(setFeaturedPitches)
      .catch((err) => setFeaturedError(err.message))

    getAllPitchesForMap()
      .then(setMapPitches)
      .catch((err) => setMapError(err.message))
  }, [])

  useEffect(() => {
    if (!MAPBOX_TOKEN || !mapaContainerRef.current || mapPitches.length === 0) {
      return
    }

    const map = new mapboxgl.Map({
      container: mapaContainerRef.current,
      style: 'mapbox://styles/mapbox/streets-v12',
      center: [mapPitches[0].longitude, mapPitches[0].latitude],
      zoom: 10,
    })

    map.addControl(new mapboxgl.NavigationControl(), 'top-right')

    const bounds = new mapboxgl.LngLatBounds()

    mapPitches.forEach((pitch) => {
      if (pitch.latitude == null || pitch.longitude == null) {
        return
      }

      const coordenadas = [pitch.longitude, pitch.latitude]
      bounds.extend(coordenadas)

      const imagemHtml = pitch.imageUrl
        ? `<img src="${escapeHtml(pitch.imageUrl)}" alt="${escapeHtml(pitch.name)}" class="pitches-overview__popup-imagem" onerror="this.remove()" />`
        : ''

      const tipoLabel = TIPOS[pitch.type] ?? pitch.type
      const acessoLabel = ACESSOS[pitch.pitchAccess] ?? pitch.pitchAccess

      const popupHtml = `
        <div class="pitches-overview__popup">
          ${imagemHtml}
          <div class="pitches-overview__popup-info">
            <strong>${escapeHtml(pitch.name)}</strong>
            <span>${escapeHtml(pitch.city)}</span>
            <span class="pitches-overview__popup-meta">${escapeHtml(tipoLabel)} - ${escapeHtml(acessoLabel)}</span>
            <a href="/pitches/${pitch.id}" class="pitches-overview__popup-btn">Ver campo</a>
          </div>
        </div>
      `

      const markerColor = pitch.pitchAccess === 'PRIVATE' ? '#e0a05f' : '#3fae4a'

      new mapboxgl.Marker({ color: markerColor })
        .setLngLat(coordenadas)
        .setPopup(new mapboxgl.Popup({ offset: 24 }).setHTML(popupHtml))
        .addTo(map)
    })

    if (!bounds.isEmpty()) {
      map.fitBounds(bounds, { padding: 60, maxZoom: 13 })
    }

    return () => {
      map.remove()
    }
  }, [mapPitches])

  return (
    <section className="pitches-overview">
      <div className="container">
        <div className="pitches-overview__header">
          <h1>Campos</h1>
          <Link to="/pitches/todos" className="btn-ghost">
            Ver todos os campos
          </Link>
        </div>

        <h2>Campos em destaque</h2>

        {featuredError && (
          <p className="pitches-overview__status pitches-overview__status--error">{featuredError}</p>
        )}
        {!featuredError && featuredPitches.length === 0 && (
          <p className="pitches-overview__status">Ainda sem campos em destaque.</p>
        )}

        <div className="pitches-overview__grid">
          {featuredPitches.map((pitch) => (
            <Link to={`/pitches/${pitch.id}`} className="pitches-overview__card" key={pitch.id}>
              <h3>{pitch.name}</h3>
              <p className="pitches-overview__card-city">{pitch.city}</p>
              <span className="badge">{TIPOS[pitch.type] ?? pitch.type}</span>
            </Link>
          ))}
        </div>

        <h2>Mapa de Campos</h2>

        {MAPBOX_TOKEN ? (
          <div className="pitches-overview__mapa-wrapper">
            <div ref={mapaContainerRef} className="pitches-overview__mapa" />
            <div className="pitches-overview__legenda">
              <span className="badge">Público</span>
              <span className="badge badge--privado">Privado</span>
            </div>
          </div>
        ) : (
          <p className="pitches-overview__status">Mapa indisponível.</p>
        )}
        {mapError && (
          <p className="pitches-overview__status pitches-overview__status--error">{mapError}</p>
        )}
      </div>
    </section>
  )
}

export default PitchesOverview
