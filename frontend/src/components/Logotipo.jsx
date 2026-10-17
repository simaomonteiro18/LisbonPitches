import './Logotipo.css'

// ponte 25 de Abril por cima de um campo; a ponte usa a cor do texto para seguir o tema
export function Simbolo({ className = '' }) {
  return (
    <svg className={`logotipo__simbolo ${className}`} viewBox="0 0 160 100" aria-hidden="true">
      <g className="logotipo__campo">
        <rect x="30" y="4" width="100" height="92" rx="10" />
        <line x1="80" y1="4" x2="80" y2="96" />
        <circle cx="80" cy="47" r="13" />
      </g>
      <g className="logotipo__cabos">
        <path d="M5 64 Q 28 58 35 20" />
        <path d="M45 20 Q 80 104 115 20" />
        <path d="M125 20 Q 132 58 155 64" />
      </g>
      <g className="logotipo__ponte">
        <rect x="1" y="61" width="158" height="8" rx="1" />
        <rect x="31" y="16" width="5" height="73" />
        <rect x="44" y="16" width="5" height="73" />
        <rect x="31" y="16" width="18" height="5" />
        <rect x="31" y="29" width="18" height="5" />
        <rect x="31" y="42" width="18" height="5" />
        <rect x="111" y="16" width="5" height="73" />
        <rect x="124" y="16" width="5" height="73" />
        <rect x="111" y="16" width="18" height="5" />
        <rect x="111" y="29" width="18" height="5" />
        <rect x="111" y="42" width="18" height="5" />
        <rect x="17" y="57" width="3" height="5" />
        <rect x="24" y="50" width="3" height="12" />
        <rect x="58" y="47" width="3" height="15" />
        <rect x="66" y="55" width="3" height="7" />
        <rect x="91" y="55" width="3" height="7" />
        <rect x="99" y="47" width="3" height="15" />
        <rect x="133" y="50" width="3" height="12" />
        <rect x="140" y="57" width="3" height="5" />
      </g>
    </svg>
  )
}

function Logotipo({ className = '' }) {
  return (
    <span className={`logotipo ${className}`}>
      <Simbolo />
      <span className="logotipo__texto">
        Lisbon<span>Pitches</span>
      </span>
    </span>
  )
}

export default Logotipo
