import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion, useInView, useReducedMotion } from 'motion/react'
import 'mapbox-gl/dist/mapbox-gl.css'
import { getFeaturedPitches, getAllPitchesForMap } from '../api/pitches'
import { aoVer, revelar } from '../motion'
import CartaoInclinado from '../components/CartaoInclinado'
import Contador from '../components/Contador'
import { carregarMapbox, criarPin, estiloMapa, imagemSatelite, seguirTema } from '../components/mapaPin'
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

const VISTA_GERAL = { padding: 60, maxZoom: 13 }

function escapeHtml(texto) {
  const div = document.createElement('div')
  div.textContent = texto ?? ''
  return div.innerHTML
}

function PitchesOverview() {
  const reduzir = useReducedMotion()
  const inicio = reduzir ? false : 'escondido'

  const [featuredPitches, setFeaturedPitches] = useState(null)
  const [featuredError, setFeaturedError] = useState(null)

  const [mapPitches, setMapPitches] = useState([])
  const [mapError, setMapError] = useState(null)
  const [mapaCarregado, setMapaCarregado] = useState(false)

  const mapaContainerRef = useRef(null)
  const mapaWrapperRef = useRef(null)
  const mapaRef = useRef(null)
  const limitesRef = useRef(null)
  const pinsRef = useRef(new Map())
  const voarTimerRef = useRef(null)
  const mapaEmVista = useInView(mapaWrapperRef, aoVer)

  useEffect(() => {
    // erros de rede chegam em ingles ("Failed to fetch"), por isso a mensagem e sempre a nossa
    getFeaturedPitches()
      .then(setFeaturedPitches)
      .catch(() => setFeaturedError('Não foi possível obter os campos em destaque.'))

    getAllPitchesForMap()
      .then(setMapPitches)
      .catch(() => setMapError('Não foi possível obter os campos para o mapa.'))
  }, [])

  useEffect(() => {
    if (!MAPBOX_TOKEN || !mapaContainerRef.current || mapPitches.length === 0) {
      return
    }

    let cancelado = false
    let limpar = null

    carregarMapbox().then((mapboxgl) => {
      if (cancelado || !mapaContainerRef.current) return

      const map = new mapboxgl.Map({
        container: mapaContainerRef.current,
        style: estiloMapa(),
        center: [mapPitches[0].longitude, mapPitches[0].latitude],
        zoom: 10,
      })
      mapaRef.current = map

      map.addControl(new mapboxgl.NavigationControl(), 'top-right')
      map.on('load', () => setMapaCarregado(true))

      const comCoordenadas = mapPitches.filter((p) => p.latitude != null && p.longitude != null)
      const bounds = new mapboxgl.LngLatBounds()
      comCoordenadas.forEach((p) => bounds.extend([p.longitude, p.latitude]))
      limitesRef.current = bounds.isEmpty() ? null : bounds

      // os pins caem do centro para fora, em onda
      const centro = bounds.isEmpty() ? null : bounds.getCenter()
      const distancia = (p) =>
        centro ? Math.hypot(p.longitude - centro.lng, p.latitude - centro.lat) : 0
      const ordem = [...comCoordenadas].sort((a, b) => distancia(a) - distancia(b))
      const passo = Math.min(0.08, 1.4 / Math.max(ordem.length, 1))

      const pins = pinsRef.current
      ordem.forEach((pitch, i) => {
        const coordenadas = [pitch.longitude, pitch.latitude]

        // fotografia do campo e, se nao houver ou falhar, o satelite
        const satelite = imagemSatelite(pitch.latitude, pitch.longitude, pitch.type)
        const imagemPopup = pitch.imageUrl || satelite
        const alternativa = satelite && imagemPopup !== satelite ? ` data-alternativa="${escapeHtml(satelite)}"` : ''
        const imagemHtml = imagemPopup
          ? `<img src="${escapeHtml(imagemPopup)}"${alternativa} alt="${escapeHtml(pitch.name)}" class="pitches-overview__popup-imagem" onerror="const alt=this.dataset.alternativa;if(alt){delete this.dataset.alternativa;this.src=alt}else{this.remove()}" />`
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

        const el = criarPin(pitch, 0.15 + i * passo)
        pins.set(pitch.id, el)

        new mapboxgl.Marker({ element: el, anchor: 'bottom' })
          .setLngLat(coordenadas)
          .setPopup(new mapboxgl.Popup({ offset: 40 }).setHTML(popupHtml))
          .addTo(map)
      })

      if (limitesRef.current) {
        map.fitBounds(limitesRef.current, { ...VISTA_GERAL, animate: false })
      }

      const pararTema = seguirTema(map)

      limpar = () => {
        pararTema()
        clearTimeout(voarTimerRef.current)
        pins.clear()
        mapaRef.current = null
        map.remove()
      }
    })

    return () => {
      cancelado = true
      limpar?.()
    }
  }, [mapPitches])

  function marcarAtivo(id) {
    pinsRef.current.forEach((el, pinId) => el.classList.toggle('mapa-pin--ativo', pinId === id))
  }

  function voarPara(pitchId) {
    clearTimeout(voarTimerRef.current)
    voarTimerRef.current = setTimeout(() => {
      const pitch = mapPitches.find((p) => p.id === pitchId)
      if (!pitch || pitch.latitude == null || !mapaRef.current) return
      mapaRef.current.flyTo({ center: [pitch.longitude, pitch.latitude], zoom: 14, speed: 1.2, curve: 1.4 })
      marcarAtivo(pitchId)
    }, 150)
  }

  function voltarVistaGeral() {
    clearTimeout(voarTimerRef.current)
    voarTimerRef.current = setTimeout(() => {
      if (mapaRef.current && limitesRef.current) {
        mapaRef.current.fitBounds(limitesRef.current, VISTA_GERAL)
      }
      marcarAtivo(null)
    }, 250)
  }

  function aoSairDaGrelha(e) {
    if (e.type === 'blur' && e.currentTarget.contains(e.relatedTarget)) return
    voltarVistaGeral()
  }

  const totalCampos = mapPitches.length
  const pinsACair = mapaCarregado && mapaEmVista

  return (
    <section className="pitches-overview">
      <div className="container">
        <div className="pitches-overview__header">
          <div>
            <h1>Campos</h1>
            <p className="pitches-overview__contador">
              {totalCampos > 0 && (
                <>
                  <Contador valor={totalCampos} /> {totalCampos === 1 ? 'campo' : 'campos'} no mapa
                </>
              )}
            </p>
          </div>
          <Link to="/pitches/todos" className="btn-ghost">
            Ver todos os campos
          </Link>
        </div>

        <div className="pitches-overview__layout">
          <div className="pitches-overview__destaques">
            <h2>Campos em destaque</h2>

            {featuredError && (
              <p className="pitches-overview__status pitches-overview__status--error">{featuredError}</p>
            )}
            {!featuredError && featuredPitches?.length === 0 && (
              <p className="pitches-overview__status">Ainda sem campos em destaque.</p>
            )}

            {!featuredError && featuredPitches === null && (
              <div className="pitches-overview__grid" aria-hidden="true">
                {[0, 1, 2, 3].map((i) => (
                  <div className="pitches-overview__esqueleto" key={i}>
                    <span />
                    <span />
                    <span />
                  </div>
                ))}
              </div>
            )}

            {featuredPitches?.length > 0 && (
              <motion.div
                className="pitches-overview__grid"
                initial={inicio}
                whileInView="visivel"
                viewport={aoVer}
                onPointerLeave={aoSairDaGrelha}
                onBlur={aoSairDaGrelha}
              >
                {featuredPitches.map((pitch, i) => (
                  <motion.div key={pitch.id} variants={revelar} custom={i * 0.12}>
                    <Link
                      to={`/pitches/${pitch.id}`}
                      className="pitches-overview__card-link"
                      onPointerEnter={(e) => e.pointerType === 'mouse' && voarPara(pitch.id)}
                      onFocus={() => voarPara(pitch.id)}
                    >
                      <CartaoInclinado className="pitches-overview__card">
                        <h3>{pitch.name}</h3>
                        <p className="pitches-overview__card-city">{pitch.city}</p>
                        <span className="badge">{TIPOS[pitch.type] ?? pitch.type}</span>
                      </CartaoInclinado>
                    </Link>
                  </motion.div>
                ))}
              </motion.div>
            )}
          </div>

          <div className="pitches-overview__mapa-coluna">
            <h2>Mapa de Campos</h2>

            {!MAPBOX_TOKEN && <p className="pitches-overview__status">Mapa indisponível.</p>}
            {MAPBOX_TOKEN && mapError && (
              <p className="pitches-overview__status pitches-overview__status--error">{mapError}</p>
            )}
            {MAPBOX_TOKEN && !mapError && (
              <motion.div
                ref={mapaWrapperRef}
                className={`pitches-overview__mapa-wrapper${pinsACair ? ' mapa-pins--a-cair' : ''}`}
                initial={inicio}
                whileInView="visivel"
                viewport={aoVer}
                variants={revelar}
              >
                <div ref={mapaContainerRef} className="pitches-overview__mapa" />
                <div className="pitches-overview__legenda">
                  <span className="badge">Público</span>
                  <span className="badge badge--privado">Privado</span>
                </div>
              </motion.div>
            )}
          </div>
        </div>
      </div>
    </section>
  )
}

export default PitchesOverview
