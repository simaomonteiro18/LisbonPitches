import { getTheme } from '../theme'
import './mapaPin.css'

let mapboxPromessa = null

// o mapbox e a parte mais pesada do site: so e descarregado quando um mapa vai mesmo aparecer
export function carregarMapbox() {
  if (!mapboxPromessa) {
    mapboxPromessa = import('mapbox-gl').then(({ default: mapboxgl }) => {
      mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN
      return mapboxgl
    })
  }
  return mapboxPromessa
}

const ESTILOS_MAPA = {
  dark: 'mapbox://styles/mapbox/dark-v11',
  light: 'mapbox://styles/mapbox/light-v11',
}

export function estiloMapa() {
  return ESTILOS_MAPA[getTheme()]
}

// imagem de satelite do campo, para quando nao ha fotografia ou ela falha
export function imagemSatelite(latitude, longitude, tipo) {
  const token = import.meta.env.VITE_MAPBOX_TOKEN
  if (!token || latitude == null || longitude == null) return null
  // os campos maiores precisam de um zoom mais aberto
  const zoom = tipo === 'ELEVEN' || tipo === 'SEVEN' ? 18 : 19
  return `https://api.mapbox.com/styles/v1/mapbox/satellite-v9/static/${longitude},${latitude},${zoom},0/640x360@2x?access_token=${token}`
}

// o estilo claro tem etiquetas cinzentas muito claras, por isso escurecem-se para se lerem
function escurecerEtiquetas(map) {
  if (getTheme() !== 'light') return
  for (const camada of map.getStyle().layers) {
    if (camada.type === 'symbol' && camada.layout?.['text-field']) {
      map.setPaintProperty(camada.id, 'text-color', '#2b2b2b')
    }
  }
}

// o estilo do mapa segue o tema do site; devolve a funcao que para de observar
export function seguirTema(map) {
  const ajustar = () => escurecerEtiquetas(map)
  map.on('style.load', ajustar)
  if (map.isStyleLoaded()) ajustar()
  const observador = new MutationObserver(() => map.setStyle(estiloMapa()))
  observador.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] })
  return () => {
    observador.disconnect()
    map.off('style.load', ajustar)
  }
}

// pin igual ao do hero; a queda anima um elemento interior porque o mapbox usa o transform da raiz
export function criarPin(pitch, atraso = 0) {
  const el = document.createElement('div')
  el.className = `mapa-pin mapa-pin--${pitch.pitchAccess === 'PRIVATE' ? 'privado' : 'publico'}`
  el.style.setProperty('--atraso', `${atraso}s`)
  el.setAttribute('aria-label', pitch.name ?? '')
  el.innerHTML = `
    <div class="mapa-pin__queda">
      <span class="mapa-pin__anel"></span>
      <svg class="mapa-pin__corpo" viewBox="-16 -44 32 46" width="32" height="46" aria-hidden="true">
        <path d="M0 0 C -6 -10 -14 -16 -14 -26 A14 14 0 1 1 14 -26 C 14 -16 6 -10 0 0 Z" />
        <circle cx="0" cy="-26" r="5" />
      </svg>
    </div>
  `
  return el
}
