import { getTheme } from '../theme'
import './mapaPin.css'

const ESTILOS_MAPA = {
  dark: 'mapbox://styles/mapbox/dark-v11',
  light: 'mapbox://styles/mapbox/light-v11',
}

export function estiloMapa() {
  return ESTILOS_MAPA[getTheme()]
}

// o estilo do mapa segue o tema do site; devolve a funcao que para de observar
export function seguirTema(map) {
  const observador = new MutationObserver(() => map.setStyle(estiloMapa()))
  observador.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] })
  return () => observador.disconnect()
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
