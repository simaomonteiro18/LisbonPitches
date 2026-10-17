import { useEffect, useRef } from 'react'
import { Routes, Route, useLocation, useNavigationType } from 'react-router-dom'
import { AnimatePresence, motion } from 'motion/react'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import ThemeToggle from './components/ThemeToggle'
import BarraProgresso from './components/BarraProgresso'
import { durations, easings } from './motion'
import Home from './pages/Home'
import Iniciativa from './pages/Iniciativa'
import Pitches from './pages/Pitches'
import PitchesOverview from './pages/PitchesOverview'
import Pitch from './pages/Pitch'
import Login from './pages/Login'
import Perfil from './pages/Perfil'
import Reserva from './pages/Reserva'

const transicaoPagina = {
  initial: { opacity: 0, y: 16 },
  animate: { opacity: 1, y: 0, transition: { duration: durations.base, ease: easings.out } },
  exit: { opacity: 0, transition: { duration: durations.fast, ease: easings.out } },
}

function App() {
  const location = useLocation()
  const tipoNavegacao = useNavigationType()

  // guarda a navegacao que provocou a saida, para decidir o scroll quando a pagina nova entra
  const navegacaoRef = useRef({ tipo: tipoNavegacao, hash: location.hash })
  useEffect(() => {
    navegacaoRef.current = { tipo: tipoNavegacao, hash: location.hash }
  }, [tipoNavegacao, location.hash])

  function aoTrocarPagina() {
    const { tipo, hash } = navegacaoRef.current
    // o voltar do browser e as ancoras (ex: #sugerir-campo) tratam do seu proprio scroll
    if (tipo !== 'POP' && !hash) {
      window.scrollTo({ top: 0, behavior: 'instant' })
    }
  }

  return (
    <div className="app">
      <BarraProgresso />
      <Navbar />
      <main>
        <AnimatePresence mode="wait" onExitComplete={aoTrocarPagina}>
          <motion.div key={location.pathname} className="pagina" {...transicaoPagina}>
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
          </motion.div>
        </AnimatePresence>
      </main>
      <Footer />
      <ThemeToggle />
    </div>
  )
}

export default App
