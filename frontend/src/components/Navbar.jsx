import { Link } from 'react-router-dom'
import './Navbar.css'

function Navbar() {
  return (
    <header className="navbar">
      <div className="container navbar__inner">
        <nav className="navbar__links navbar__links--left">
          <Link to="/pitches">Pitches</Link>
          <Link to="/iniciativa">Iniciativa</Link>
        </nav>

        <Link to="/" className="navbar__logo">
          {/* substituir por <img src={logo} alt="Pitch Booking" /> quando o ficheiro existir */}
          Pitch<span>Booking</span>
        </Link>

        <nav className="navbar__links navbar__links--right">
          <Link to="/login" className="navbar__login">Login / Registo</Link>
        </nav>
      </div>
    </header>
  )
}

export default Navbar
