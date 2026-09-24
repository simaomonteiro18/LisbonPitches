import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import Home from './pages/Home'
import Iniciativa from './pages/Iniciativa'
import Pitches from './pages/Pitches'
import PitchesOverview from './pages/PitchesOverview'
import Pitch from './pages/Pitch'
import Login from './pages/Login'
import Perfil from './pages/Perfil'
import Reserva from './pages/Reserva'

function App() {
  return (
    <div className="app">
      <Navbar />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/iniciativa" element={<Iniciativa />} />
          <Route path="/pitches" element={<PitchesOverview />} />
          <Route path="/pitches/todos" element={<Pitches />} />
          <Route path="/pitches/:id" element={<Pitch />} />
          <Route path="/login" element={<Login />} />
          <Route path="/perfil" element={<Perfil />} />
          <Route path="/reservas/:id" element={<Reserva />} />
        </Routes>
      </main>
      <Footer />
    </div>
  )
}

export default App
