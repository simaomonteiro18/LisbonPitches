import { Suspense, lazy, useEffect, useRef } from 'react'
import { Routes, Route, useLocation, useNavigationType } from 'react-router-dom'
import { AnimatePresence, motion } from 'motion/react'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import ThemeToggle from './components/ThemeToggle'
import BarraProgresso from './components/BarraProgresso'
import { durations, easings } from './motion'
import Home from './pages/Home'

// a Home vem no pacote inicial; as outras paginas (e o Mapbox) so descarregam quando sao precisas
const paginas = {
  Iniciativa: () => import('./pages/Iniciativa'),
  Pitches: () => import('./pages/Pitches'),
  PitchesOverview: () => import('./pages/PitchesOverview'),
  Pitch: () => import('./pages/Pitch'),
  Login: () => import('./pages/Login'),
  Perfil: () => import('./pages/Perfil'),
  Reserva: () => import('./pages/Reserva'),
}

const Iniciativa = lazy(paginas.Iniciativa)
const Pitches = lazy(paginas.Pitches)
const PitchesOverview = lazy(paginas.PitchesOverview)
const Pitch = lazy(paginas.Pitch)
const Login = lazy(paginas.Login)
const Perfil = lazy(paginas.Perfil)
const Reserva = lazy(paginas.Reserva)

// a primeira interacao (rato, toque, teclado ou scroll) vai buscando as outras paginas em segundo plano,
// para a navegacao continuar instantanea sem atrasar o primeiro carregamento
const EVENTOS_INTERACAO = ['pointermove', 'pointerdown', 'keydown', 'scroll', 'touchstart']

function preCarregarPaginas() {
  const carregar = () => Object.values(paginas).forEach((importar) => importar())
  if ('requestIdleCallback' in window) {
    window.requestIdleCallback(carregar, { timeout: 2000 })
  } else {
    setTimeout(carregar, 200)
  }
}

const TITULOS = {
  '/': 'LisbonPitches',
  '/iniciativa': 'Iniciativa | LisbonPitches',
  '/pitches': 'Campos | LisbonPitches',
  '/pitches/todos': 'Todos os campos | LisbonPitches',
  '/login': 'Entrar ou registar | LisbonPitches',
  '/perfil': 'Perfil | LisbonPitches',
}

function tituloDaPagina(pathname) {
  if (TITULOS[pathname]) return TITULOS[pathname]
  if (pathname.startsWith('/pitches/')) return 'Campo | LisbonPitches'
  if (pathname.startsWith('/reservas/')) return 'Reserva | LisbonPitches'
  return 'LisbonPitches'
}

const transicaoPagina = {
  initial: { opacity: 0, y: 16 },
  animate: { opacity: 1, y: 0, transition: { duration: durations.base, ease: easings.out } },
  exit: { opacity: 0, transition: { duration: durations.fast, ease: easings.out } },
}

function App() {
  const location = useLocation()
  const tipoNavegacao = useNavigationType()
  const mainRef = useRef(null)

  // guarda a navegacao que provocou a saida, para decidir o scroll quando a pagina nova entra
  const navegacaoRef = useRef({ tipo: tipoNavegacao, hash: location.hash })
  useEffect(() => {
    navegacaoRef.current = { tipo: tipoNavegacao, hash: location.hash }
  }, [tipoNavegacao, location.hash])

  useEffect(() => {
    document.title = tituloDaPagina(location.pathname)
  }, [location.pathname])

  useEffect(() => {
    function aoInteragir() {
      EVENTOS_INTERACAO.forEach((tipo) => window.removeEventListener(tipo, aoInteragir))
      preCarregarPaginas()
    }
    EVENTOS_INTERACAO.forEach((tipo) => window.addEventListener(tipo, aoInteragir, { passive: true }))
    return () => EVENTOS_INTERACAO.forEach((tipo) => window.removeEventListener(tipo, aoInteragir))
  }, [])

  function aoTrocarPagina() {
    const { tipo, hash } = navegacaoRef.current
    // o voltar do browser e as ancoras (ex: #sugerir-campo) tratam do seu proprio scroll
    if (tipo !== 'POP' && !hash) {
      window.scrollTo({ top: 0, behavior: 'instant' })
    }
    // leitores de ecra passam a ler a pagina nova a partir do inicio do conteudo
    mainRef.current?.focus({ preventScroll: true })
  }

  function saltarParaConteudo(e) {
    e.preventDefault()
    mainRef.current?.focus()
  }

  return (
    <div className="app">
      <a href="#conteudo" className="saltar-conteudo" onClick={saltarParaConteudo}>
        Saltar para o conteúdo
      </a>
      <BarraProgresso />
      <Navbar />
      <main id="conteudo" ref={mainRef} tabIndex={-1}>
        <AnimatePresence mode="wait" onExitComplete={aoTrocarPagina}>
          <motion.div key={location.pathname} className="pagina" {...transicaoPagina}>
            <Suspense fallback={<div className="pagina__a-carregar" />}>
              <Routes location={location}>
                <Route path="/" element={<Home />} />
                <Route path="/iniciativa" element={<Iniciativa />} />
                <Route path="/pitches" element={<PitchesOverview />} />
                <Route path="/pitches/todos" element={<Pitches />} />
                <Route path="/pitches/:id" element={<Pitch />} />
                <Route path="/login" element={<Login />} />
                <Route path="/perfil" element={<Perfil />} />
                <Route path="/reservas/:id" element={<Reserva />} />
              </Routes>
            </Suspense>
          </motion.div>
        </AnimatePresence>
      </main>
      <Footer />
      <ThemeToggle />
    </div>
  )
}

export default App
