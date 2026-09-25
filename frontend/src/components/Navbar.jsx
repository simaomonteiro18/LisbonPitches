import { Link, useLocation } from 'react-router-dom'
import { getSession } from '../auth'
import './Navbar.css'

function Navbar() {
  useLocation()
  const session = getSession()

  return (
    <header className="navbar">
      <div className="container navbar__inner">
        <nav className="navbar__links navbar__links--left">
          <Link to="/pitches">Pitches</Link>
          <Link to="/iniciativa">Iniciativa</Link>
        </nav>

        <Link to="/" className="navbar__logo">
          {/* substituir por <img src={logo} alt="LisbonPitches" /> quando o ficheiro existir */}
          Lisbon<span>Pitches</span>
        </Link>

        <nav className="navbar__links navbar__links--right">
          {session ? (
            <Link to="/perfil" className="navbar__perfil" aria-label="Perfil">
              <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="8" r="4" />
                <path d="M4 20c0-4.4 3.6-8 8-8s8 3.6 8 8" />
              </svg>
            </Link>
          ) : (
            <Link to="/login" className="navbar__login">Login / Registo</Link>
          )}
        </nav>
      </div>
    </header>
  )
}

export default Navbar
