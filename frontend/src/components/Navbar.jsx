import { useEffect, useRef, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { AnimatePresence, motion } from 'motion/react'
import { getSession } from '../auth'
import { durations, easings, stagger } from '../motion'
import './Navbar.css'

const linksMenu = [
  { to: '/', texto: 'Início' },
  { to: '/pitches', texto: 'Campos' },
  { to: '/pitches/todos', texto: 'Todos os campos' },
  { to: '/iniciativa', texto: 'Iniciativa' },
  { to: { pathname: '/', hash: '#sugerir-campo' }, texto: 'Sugerir um campo' },
]

function Navbar() {
  useLocation()
  const session = getSession()
  const [menuAberto, setMenuAberto] = useState(false)
  const botaoRef = useRef(null)

  const links = session ? [...linksMenu, { to: '/perfil', texto: 'Perfil' }] : linksMenu

  useEffect(() => {
    if (!menuAberto) return

    function aoCarregar(e) {
      if (e.key === 'Escape') {
        setMenuAberto(false)
        botaoRef.current?.focus()
      }
    }

    function aoRedimensionar() {
      if (window.innerWidth > 640) setMenuAberto(false)
    }

    window.addEventListener('keydown', aoCarregar)
    window.addEventListener('resize', aoRedimensionar)
    return () => {
      window.removeEventListener('keydown', aoCarregar)
      window.removeEventListener('resize', aoRedimensionar)
    }
  }, [menuAberto])

  function fecharMenu() {
    setMenuAberto(false)
  }

  return (
    <header className="navbar">
      <div className="container navbar__container">
        <div className="navbar__inner">
          <Link to="/pitches" className="navbar__link">Pitches</Link>

          <Link to="/" className="navbar__logo" onClick={fecharMenu}>
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
            <button
              ref={botaoRef}
              type="button"
              className="navbar__hamburguer"
              aria-expanded={menuAberto}
              aria-controls="navbar-menu"
              aria-label={menuAberto ? 'Fechar menu' : 'Abrir menu'}
              onClick={() => setMenuAberto((aberto) => !aberto)}
            >
              <span />
              <span />
              <span />
            </button>
          </div>
        </div>

        <AnimatePresence>
          {menuAberto && (
            <motion.nav
              id="navbar-menu"
              className="navbar__menu"
              aria-label="Menu principal"
              initial={{ opacity: 0, y: -12, scale: 0.96 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: -8, scale: 0.98 }}
              transition={{ duration: durations.base, ease: easings.out }}
            >
              <ul>
                {links.map((link, i) => (
                  <motion.li
                    key={link.texto}
                    initial={{ opacity: 0, x: 16 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ duration: durations.base, ease: easings.out, delay: 0.08 + i * stagger }}
                  >
                    <Link to={link.to} className="navbar__menu-link" onClick={fecharMenu}>
                      {link.texto}
                      <span aria-hidden="true">→</span>
                    </Link>
                  </motion.li>
                ))}
              </ul>
            </motion.nav>
          )}
        </AnimatePresence>
      </div>

      <AnimatePresence>
        {menuAberto && (
          <motion.div
            className="navbar__fundo"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: durations.base }}
            onClick={fecharMenu}
          />
        )}
      </AnimatePresence>
    </header>
  )
}

export default Navbar
