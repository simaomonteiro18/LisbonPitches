import { Link } from 'react-router-dom'
import './Footer.css'

function Footer() {
  return (
    <footer className="footer">
      <div className="container footer__inner">
        <div className="footer__col">
          <span className="footer__logo">Lisbon<span>Pitches</span></span>
        </div>

        <div className="footer__col">
          <h4>Páginas</h4>
          <Link to="/">Home</Link>
          <Link to="/iniciativa">Iniciativa</Link>
          <Link to="/pitches">Pitches</Link>
        </div>

        <div className="footer__col">
          <h4>Contacto</h4>
          <a href="https://github.com/simaomonteiro18" target="_blank" rel="noreferrer">GitHub</a>
          <a href="https://www.linkedin.com/in/sim%C3%A3o-monteiro-62866a278/" target="_blank" rel="noreferrer">LinkedIn</a>
        </div>
      </div>

      <div className="container footer__bottom">
        <span>&copy; 2026 LisbonPitches</span>
        <a href="https://www.linkedin.com/in/sim%C3%A3o-monteiro-62866a278/" target="_blank" rel="noreferrer">
          Simão Monteiro
        </a>
      </div>
    </footer>
  )
}

export default Footer
