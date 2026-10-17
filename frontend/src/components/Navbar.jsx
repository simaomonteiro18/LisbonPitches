import { useEffect, useRef, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { AnimatePresence, motion, useMotionValueEvent, useScroll } from 'motion/react'
import { getSession } from '../auth'
import { durations, easings, stagger } from '../motion'
import Logotipo from './Logotipo'
import './Navbar.css'

const linksMenu = [
  { to: '/', texto: 'Início' },
  { to: '/pitches', texto: 'Campos' },
  { to: '/pitches/todos', texto: 'Todos os campos' },
  { to: '/iniciativa', texto: 'Iniciativa' },
  { to: { pathname: '/', hash: '#sugerir-campo' }, texto: 'Sugerir um campo' },
]

// distancia a partir da qual a navbar fica compacta e pode esconder-se
const LIMITE_TOPO = 24
// movimento minimo para mudar de estado, para nao tremer com scroll muito lento
const FOLGA_SCROLL = 6

// a pagina atual na pilula: tudo o que comeca por /pitches conta como Campos
function linkAtivo(pathname) {
  if (pathname === '/') return 'inicio'
  if (pathname.startsWith('/pitches')) return 'campos'
  if (pathname === '/iniciativa') return 'iniciativa'
  return null
}

function Navbar() {
  const { pathname } = useLocation()
  const session = getSession()
  const [menuAberto, setMenuAberto] = useState(false)
  const [escondida, setEscondida] = useState(false)
  const [compacta, setCompacta] = useState(false)
  const botaoRef = useRef(null)
  const navbarRef = useRef(null)
  const { scrollY } = useScroll()

  // as paginas comecam por baixo da navbar fixa; a altura muda com a sessao e com o ecra
  useEffect(() => {
    const el = navbarRef.current
    if (!el) return
    const observador = new ResizeObserver(() => {
      document.documentElement.style.setProperty('--altura-navbar', `${el.offsetHeight}px`)
    })
    observador.observe(el)
    return () => observador.disconnect()
  }, [])

  useMotionValueEvent(scrollY, 'change', (atual) => {
    const anterior = scrollY.getPrevious() ?? 0
    setCompacta(atual > LIMITE_TOPO)

    if (atual <= LIMITE_TOPO) {
      setEscondida(false)
    } else if (atual - anterior > FOLGA_SCROLL) {
      setEscondida(true)
    } else if (anterior - atual > FOLGA_SCROLL) {
      setEscondida(false)
    }
  })

  const ativo = linkAtivo(pathname)
  const visivel = !escondida || menuAberto

  const links = [...linksMenu, session ? { to: '/perfil', texto: 'Perfil' } : { to: '/login', texto: 'Entrar ou registar' }]
  const contaAtiva = pathname === '/perfil' || pathname === '/login'

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
    <motion.header
      ref={navbarRef}
      className={`navbar${compacta ? ' navbar--compacta' : ''}`}
      animate={{ y: visivel ? 0 : '-110%' }}
      transition={{ duration: durations.base, ease: easings.out }}
    >
      <div className="container navbar__container">
        <div className="navbar__inner">
          <div className="navbar__esquerda">
            <LinkPilula to="/" ativo={ativo === 'inicio'}>
              Início
            </LinkPilula>
            <LinkPilula to="/pitches" ativo={ativo === 'campos'}>
              Campos
            </LinkPilula>
          </div>

          <Link to="/" className="navbar__logo" onClick={fecharMenu} aria-label="LisbonPitches, início">
            <Logotipo />
          </Link>

          <div className="navbar__right">
            <LinkPilula to="/iniciativa" ativo={ativo === 'iniciativa'}>
              Iniciativa
            </LinkPilula>
            {/* conta sempre visivel: entrar ou registar sem sessao, perfil com sessao */}
            <Link
              to={session ? '/perfil' : '/login'}
              className={`navbar__conta${session ? ' navbar__conta--sessao' : ''}${contaAtiva ? ' navbar__conta--ativa' : ''}`}
              aria-label={session ? 'Perfil' : 'Entrar ou registar'}
              title={session ? 'Perfil' : 'Entrar ou registar'}
              aria-current={contaAtiva ? 'page' : undefined}
              onClick={fecharMenu}
            >
              <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="8" r="4" />
                <path d="M4 20c0-4.4 3.6-8 8-8s8 3.6 8 8" />
              </svg>
            </Link>
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
                    <Link
                      to={link.to}
                      className="navbar__menu-link"
                      onClick={fecharMenu}
                      aria-current={typeof link.to === 'string' && link.to === pathname ? 'page' : undefined}
                    >
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
    </motion.header>
  )
}

// o sublinhado verde desliza entre links com layoutId
function LinkPilula({ to, ativo, children }) {
  return (
    <Link
      to={to}
      className={`navbar__link${ativo ? ' navbar__link--ativo' : ''}`}
      aria-current={ativo ? 'page' : undefined}
    >
      {children}
      {ativo && (
        <motion.span
          layoutId="navbar-sublinhado"
          className="navbar__sublinhado"
          transition={{ type: 'spring', stiffness: 500, damping: 40 }}
        />
      )}
    </Link>
  )
}

export default Navbar
