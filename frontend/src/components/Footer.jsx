import { Link } from 'react-router-dom'
import Logotipo from './Logotipo'
import './Footer.css'

function Footer() {
  return (
    <footer className="footer">
      <div className="container footer__inner">
        <div className="footer__col">
          <Logotipo className="footer__logo" />
        </div>

        <div className="footer__col">
          <h4>Páginas</h4>
          <Link to="/">Início</Link>
          <Link to="/pitches">Campos</Link>
          <Link to="/iniciativa">Iniciativa</Link>
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
