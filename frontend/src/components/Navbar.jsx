import { Link, useLocation } from 'react-router-dom'
import { getSession } from '../auth'
import './Navbar.css'

function Navbar() {
  useLocation()
  const session = getSession()

  return (
    <header className="navbar">
      <div className="container">
        <div className="navbar__inner">
          <Link to="/pitches" className="navbar__link">Pitches</Link>

          <Link to="/" className="navbar__logo">
            {/* substituir por <img src={logo} alt="LisbonPitches" /> quando o ficheiro existir */}
            Lisbon<span>Pitches</span>
          </Link>

          <div className="navbar__right">
            <Link to="/iniciativa" className="navbar__link">Iniciativa</Link>
            {session && (
              <Link to="/perfil" className="navbar__perfil" aria-label="Perfil">
                <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="8" r="4" />
                  <path d="M4 20c0-4.4 3.6-8 8-8s8 3.6 8 8" />
                </svg>
              </Link>
            )}
            {/* Login/Registo escondido do menu de proposito: sem campos com reserva online real ainda,
                a autenticacao nao desbloqueia funcionalidade para um visitante novo. A rota /login
                continua a funcionar normalmente, so nao esta em destaque. */}
          </div>
        </div>
      </div>
    </header>
  )
}

export default Navbar
